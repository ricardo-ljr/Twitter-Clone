package edu.byu.cs.tweeter.server.dao.implementation;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.UserDTO;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.server.dao.DynamoDBUtil;
import edu.byu.cs.tweeter.server.dao.interfaces.FollowDAOInterface;

public class FollowDAO implements FollowDAOInterface {

    private static final String tableName = "Follow";
    private static final String partitionKey = "Follower";
    private static final String sortKey = "Followee";
    private static final String followerNameAttr = "FollowerName";
    private static final String followeeNameAttr = "FolloweeName";

    private static AmazonDynamoDB client = AmazonDynamoDBAsyncClientBuilder.standard().withRegion("us-west-1").build();
    private static DynamoDB dynamoDB = new DynamoDB(client);

    @Override
    public ItemCollection<QueryOutcome> getFollowing(FollowingRequest request) {

        ItemCollection<QueryOutcome> outcome = getDynamoDB().getitem(tableName, partitionKey, request.getFollowerAlias());

        return outcome;
    }

    @Override
    public ItemCollection<QueryOutcome> getFollowers(FollowerRequest request) {

        Table table = dynamoDB.getTable(tableName);

        QuerySpec spec = new QuerySpec().withKeyConditionExpression(sortKey + " = :_id").withValueMap( new ValueMap().withString(":_id", request.getFollowerAlias()));

        Index index = table.getIndex("follows_index");
        return index.query(spec);
    }

    @Override
    public PutItemOutcome follow(FollowRequest request) {

        Table table = dynamoDB.getTable(tableName);

        PutItemOutcome putItemOutcome = table.putItem(new Item()
                .withPrimaryKey
                        (partitionKey, request.getCurrentUser().getAlias(),
                                sortKey, request.getTargetUser().getAlias())
                .withString(followerNameAttr, request.getTargetUser().getFirstName())
                .withString(followeeNameAttr, request.getCurrentUser().getFirstName()));

        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("Alias", request.getCurrentUser().getAlias())
                .withUpdateExpression("set FollowerCount = FollowerCount + :p")
                .withValueMap(new ValueMap().withInt(":p", 1))
                .withReturnValues(ReturnValue.UPDATED_NEW);

        AmazonDynamoDB client = AmazonDynamoDBAsyncClientBuilder.standard().withRegion("us-west-1").build();
        DynamoDB dynamoDB = new DynamoDB(client);

        Table userTable = dynamoDB.getTable("Users");

        userTable.updateItem(updateItemSpec);

        return putItemOutcome;
    }

    @Override
    public DeleteItemResult unfollow(UnfollowRequest request) {

        Table table = dynamoDB.getTable(tableName);

        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey(partitionKey, request.getCurrentUser().getAlias(),
                                                sortKey, request.getTargetUser().getAlias()))
                .withReturnValues(ReturnValue.ALL_OLD);

        DeleteItemOutcome outcome = table.deleteItem(deleteItemSpec);

        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("Alias", request.getCurrentUser().getAlias())
                .withUpdateExpression("set FollowerCount = FollowerCount - :p")
                .withValueMap(new ValueMap().withInt(":p", 1))
                .withReturnValues(ReturnValue.UPDATED_NEW);

        AmazonDynamoDB client = AmazonDynamoDBAsyncClientBuilder.standard().withRegion("us-west-1").build();
        DynamoDB dynamoDB = new DynamoDB(client);

        Table userTable = dynamoDB.getTable("Users");

        userTable.updateItem(updateItemSpec);

        return outcome.getDeleteItemResult();
    }

    public List<User> getFollowingQueue(String alias) {
        List<User> followees = new ArrayList<>();

        Map<String, String> attrNames = new HashMap<String, String>();
        attrNames.put("#handle", "Followee");

        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":alias", new AttributeValue().withS(alias));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(tableName)
                .withIndexName("follows_index")
                .withKeyConditionExpression("#handle = :alias")
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues);

        QueryResult queryResult = client.query(queryRequest);

        List<Map<String, AttributeValue>> items = queryResult.getItems();

        if (items != null) {
            for (Map<String, AttributeValue> item : items) {
                User user = new User();
                user.setAlias(item.get("Follower").getS());
                followees.add(user);
            }
        }

        return followees;
    }

    public void followTarget(String user, String followTarget) {
        Table table = dynamoDB.getTable(tableName);

        Item item = new Item().withPrimaryKey(partitionKey, user).withString(sortKey, followTarget);

        try {
            table.putItem(item);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void addFollowersBatch(List<String> users, String followTarget) {
        TableWriteItems items = new TableWriteItems(tableName);

        for (String user : users) {
            Item item = new Item()
                    .withPrimaryKey(partitionKey, user, sortKey, followTarget)
                    .withString("Name", user);
            items.addItemToPut(item);

            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems(tableName);
            }
        }

        // Write any leftover items
        if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {
            loopBatchWrite(items);
        }
    }

    private void loopBatchWrite(TableWriteItems items) {

        // The 'dynamoDB' object is of type DynamoDB and is declared statically in this example
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(items);
        System.out.println("Wrote Followers Batch");

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
            System.out.println("Wrote More Followers");
        }
    }

    public DynamoDBUtil getDynamoDB() {
        return new DynamoDBUtil();
    }

}
