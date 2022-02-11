package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDBDAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.implementation.AuthTableDAO;
import edu.byu.cs.tweeter.server.dao.implementation.FeedDao;
import edu.byu.cs.tweeter.server.dao.implementation.FollowDAO;
import edu.byu.cs.tweeter.server.dao.implementation.StoryDAO;
import edu.byu.cs.tweeter.server.dao.implementation.UserDAO;
import edu.byu.cs.tweeter.server.util.JsonSerializer;
import edu.byu.cs.tweeter.server.util.Pair;

public class StatusService {

    private final DynamoDBDAOFactoryInterface factory;

    private static AmazonDynamoDB client = AmazonDynamoDBAsyncClientBuilder.standard().withRegion("us-west-1").build();
    private static DynamoDB dynamoDB = new DynamoDB(client);

    public StatusService(DynamoDBDAOFactoryInterface factory) {
        super();
        this.factory = factory;
    }

    public FeedResponse getFeed(FeedRequest request) {

        FeedDao feedDao = factory.getFeedDAO();

//        if (!authTableDAO.validateUser(request.getAuthToken(), request.getFollowerAlias())) {
//            throw new RuntimeException("User Session Timed Out");
//        }

        String lastStatusTime = null;
        if (request.getLastStatus() != null) {
            lastStatusTime = request.getLastStatus().getDate();
        }

        return feedDao.getFeed(request.getUser().getAlias(), lastStatusTime, request.getLimit());
    }

    public StoryResponse getStory(StoryRequest request) {
        StoryDAO storyDAO = factory.getStoryDAO();
        UserDAO userDAO = factory.getUserDAO();

        List<Status> statuses = new ArrayList<>();

//        ItemCollection<QueryOutcome> stories = storyDAO.getStories(request);

        Table table = dynamoDB.getTable("Story");

        QuerySpec spec = new QuerySpec().withKeyConditionExpression("Alias" + " = :_id")
                .withValueMap( new ValueMap().withString(":_id", request.getFollowerAlias()))
                .withScanIndexForward(true)
                .withMaxResultSize(request.getLimit());

        ItemCollection<QueryOutcome> stories = table.query(spec);

        Iterator<Item> storyIterator = stories.iterator();

//        Item userItem = userDAO.getUser(new UserRequest(request.getFollowerAlias()));

        Table usertable = dynamoDB.getTable("Users");
        GetItemSpec userspec = new GetItemSpec().withPrimaryKey("Alias", request.getFollowerAlias());

        Item userItem = usertable.getItem(userspec);

        String alias = userItem.getString("Alias");
        String firstName = userItem.getString("FirstName");
        String lastName = userItem.getString("LastName");
        String imageUrl = userItem.getString("ImageUrl");

        User user = new User(firstName, lastName, alias, imageUrl);

        while (storyIterator.hasNext()) {

            Item storyItem = storyIterator.next();

            String post = storyItem.getString("Post");
            String timeStamp = storyItem.getString("TimeStamp");
            List<String> urls = storyItem.getList("Urls");
            List<String> mentions = storyItem.getList("Mentions");

            Status newStatus = new Status(post, user, timeStamp, urls, mentions);
            statuses.add(newStatus);
        }

        Pair<List<Status>, Boolean> allStories = getPageOfStatus(request.getLastStatus(), request.getLimit(), statuses);

        List<Status> allStatuses = allStories.getFirst();
        boolean hasMorePages = allStories.getSecond();

        List<Status> responseStory = new ArrayList<>(request.getLimit());

        if (request.getLimit() > 0) {
            if (allStatuses != null) {
                int index = getStartingIndex(request.getLastStatus(), allStatuses);

                for (int limitCounter = 0; index < allStatuses.size() && limitCounter < request.getLimit(); index++, limitCounter++) {
                    responseStory.add(allStatuses.get(index));
                }
            }
        }

        StoryResponse response = new StoryResponse(responseStory, hasMorePages);

        return response;

    }


    public PostStatusResponse postStatus(PostStatusRequest request) {

        factory.getStoryDAO().postStatus(request.getStatus());
        request.getStatus().getUser().setImageBytes(null);
        String messageBody = JsonSerializer.serialize(request.getStatus());

        String queueUrl = "https://sqs.us-west-1.amazonaws.com/587140454100/CS340PostStatus";


        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(messageBody);

//        System.out.println("Message sent");

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageRequest);
//        System.out.println("Message added to queue");

//        String msgId = sendMessageResult.getMessageId();
//        System.out.println("Message ID: " + msgId);


        return new PostStatusResponse();

    }

    private int getStartingIndex(Status lastStatus, List<Status> statuses) {

        int index = 0;

        if (lastStatus != null) {
            for (int i = 0; i < statuses.size(); i++) {
                if (lastStatus.equals(statuses.get(i).getUser())) {
                    index = i + 1;
                    break;
                }
            }
        }

        return index;
    }

    public Pair<List<Status>, Boolean> getPageOfStatus(Status lastStatus, int limit, List<Status> statuses) {

        Pair<List<Status>, Boolean> result = new Pair<>(new ArrayList<>(), false);

        int index = 0;

        if (lastStatus != null) {
            for (int i = 0; i < statuses.size(); ++i) {
                Status curStatus = statuses.get(i);
                if (curStatus.getUser().getAlias().equals(lastStatus.getUser().getAlias()) &&
                        curStatus.getDate().equals(lastStatus.getDate())) {
                    index = i + 1;
                    break;
                }
            }
        }

        for (int count = 0; index < statuses.size() && count < limit; ++count, ++index) {
            Status curStatus = statuses.get(index);
            result.getFirst().add(curStatus);
        }

        result.setSecond(index < statuses.size());

        return result;
    }
}
