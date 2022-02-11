package edu.byu.cs.tweeter.server.dao.implementation;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.dynamodbv2.xspec.S;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.UserDTO;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDBUtil;
import edu.byu.cs.tweeter.server.dao.ResultsPage;
import edu.byu.cs.tweeter.server.dao.interfaces.FeedDAOInterface;

public class FeedDao implements FeedDAOInterface {

    private static final String tableName = "Feed";
    private static final String partitionKey = "Alias";
    private static final String sortKey = "TimeStamp";
    private static final String statusAttr = "Status";

    private static final Integer PAGE_SIZE = 10;

    private static AmazonDynamoDB client = AmazonDynamoDBAsyncClientBuilder.standard().withRegion("us-west-1").build();
    private static DynamoDB dynamoDB = new DynamoDB(client);


    @Override
    public ItemCollection<QueryOutcome> getFeedItems(FeedRequest request) {
        Table table = dynamoDB.getTable(tableName);

        QuerySpec spec = new QuerySpec().withKeyConditionExpression(partitionKey + " = :_id")
                .withValueMap( new ValueMap().withString(":_id", request.getUser().getAlias()))
                .withScanIndexForward(true)
                .withMaxResultSize(request.getLimit());

        // withExclusiveStartKey(alias....)

        return table.query(spec);

    }

    @Override
    public FeedResponse getFeed(String alias, String lastStatusDateTime, int limit) {
        List<Status> feed = new ArrayList<>();

        Map<String, String> attNames = new HashMap<String, String>();
        attNames.put("#alias", "Alias");

        Map<String, AttributeValue> attValues = new HashMap<>();
        attValues.put(":alias", new AttributeValue().withS(alias));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(tableName)
                .withKeyConditionExpression("#alias = :alias")
                .withScanIndexForward(false)
                .withExpressionAttributeNames(attNames)
                .withExpressionAttributeValues(attValues)
                .withLimit(limit);

        QueryRequest checkRequest = new QueryRequest()
                .withTableName(tableName)
                .withKeyConditionExpression("#alias = :alias")
                .withScanIndexForward(false)
                .withExpressionAttributeNames(attNames)
                .withExpressionAttributeValues(attValues)
                .withLimit(limit + 1);

        if (lastStatusDateTime != null) {
            Map<String, AttributeValue> lastKey = new HashMap<>();
            lastKey.put("Alias", new AttributeValue().withS(alias));
            lastKey.put("TimeStamp", new AttributeValue().withS(lastStatusDateTime));

            queryRequest = queryRequest.withExclusiveStartKey(lastKey);
            checkRequest = checkRequest.withExclusiveStartKey(lastKey);
        }

        QueryResult res = client.query(queryRequest);
        List<Map<String, AttributeValue>> items = res.getItems();
        res = client.query(checkRequest);
        List<Map<String, AttributeValue>> checkItems = res.getItems();

        if (items != null) {
            for (Map<String, AttributeValue> item : items) {

                User user = new User(item.get("FirstName").getS(),
                        item.get("LastName").getS(),
                        item.get("Alias").getS(),
                        item.get("ImageUrl").getS());
                List<String> urls = item.get("Urls").getSS();
                List<String> mentions = item.get("Mentions").getSS();

                Status status = new Status(item.get("Post").getS(),
                        user, item.get("TimeStamp").getS(),
                        urls, mentions);
                feed.add(status);
            }
        }

        System.out.println("Successfully got feed");
        System.out.println(feed);
        if (checkItems.size() > items.size()) {
            return new FeedResponse(feed, true);
        }
        else {
            System.out.println("No more status");
            return new FeedResponse(feed, false);
        }
    }

    @Override
    public void putFeed(Status status, List<User> followers) {
        TableWriteItems items = new TableWriteItems(tableName);

        for (User user: followers) {
            HashSet<String> mentions = new HashSet<>(status.getMentions());
            HashSet<String> urls = new HashSet<>(status.getUrls());
            mentions.add("");
            urls.add("");
            Item item = new Item().withPrimaryKey(partitionKey, user.getAlias(), sortKey, status.getDate())
                    .withString("AuthorAlias", status.getUser().getAlias())
                    .withString("Post", status.getPost())
                    .withStringSet("Mentions", mentions)
                    .withStringSet("Urls", urls)
                    .withString("ImageUrl", status.getUser().getImageUrl())
                    .withString("FirstName", status.getUser().getFirstName())
                    .with("LastName", status.getUser().getLastName());
            items.addItemToPut(item);

            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems(tableName);
            }

        }
        System.out.println("Feed table updated for all users");
    }

    private void loopBatchWrite(TableWriteItems items) {
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(items);
        System.out.println("Wrote User Batch");

        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);

        }
    }


    public DynamoDBUtil getDynamoDB() {
        return new DynamoDBUtil();
    }
}
