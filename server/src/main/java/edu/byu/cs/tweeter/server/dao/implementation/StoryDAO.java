package edu.byu.cs.tweeter.server.dao.implementation;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.transform.QueryResultJsonUnmarshaller;
import com.amazonaws.services.dynamodbv2.xspec.S;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDBUtil;
import edu.byu.cs.tweeter.server.dao.ResultsPage;
import edu.byu.cs.tweeter.server.dao.interfaces.StoryDAOInterface;

public class StoryDAO implements StoryDAOInterface {

    private static final String tableName = "Story";
    private static final String partitionKey = "Alias";
    private static final String sortKey = "TimeStamp";
    private static final String firstNameAttr = "FirstName";
    private static final String lastNameAttr = "Lastname";
    private static final String postAttr = "Post";
    private static final String imageAttr = "ImageUrl";
    private static final String urlsAttr = "Urls";
    private static final String mentionsAttr = "Mentions";

    private static AmazonDynamoDB client = AmazonDynamoDBAsyncClientBuilder.standard().withRegion("us-west-1").build();
    private static DynamoDB dynamoDB = new DynamoDB(client);

    @Override
    public void postStatus(Status status) {
        Table table = dynamoDB.getTable(tableName);

        HashSet<String> mentions = new HashSet<>(status.getMentions());
        HashSet<String> urls = new HashSet<>(status.getUrls());

        mentions.add("");
        urls.add("");

        Item item = new Item().withPrimaryKey(partitionKey, status.getUser().getAlias(), sortKey, status.getDate())
                .withString(firstNameAttr, status.getUser().getFirstName())
                .withString(lastNameAttr, status.getUser().getLastName())
                .withString(imageAttr, status.getUser().getImageUrl())
                .withString(postAttr, status.getPost())
                .withStringSet(urlsAttr, urls)
                .withStringSet(mentionsAttr, mentions);

        table.putItem(item);

    }

    @Override
    public ItemCollection<QueryOutcome> getStories(StoryRequest request) {
        Table table = dynamoDB.getTable(tableName);

        QuerySpec spec = new QuerySpec().withKeyConditionExpression(partitionKey + " = :_id")
                .withValueMap( new ValueMap().withString(":_id", request.getFollowerAlias()))
                .withScanIndexForward(true)
                .withMaxResultSize(request.getLimit());
        // withExclusiveStartKey(alias....)

        return table.query(spec);
    }


    private boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    public DynamoDBUtil getDynamoDB() {
        return new DynamoDBUtil();
    }

}
