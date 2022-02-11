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
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.UserDTO;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.DynamoDBUtil;
import edu.byu.cs.tweeter.server.dao.interfaces.UserDAOInterface;
import edu.byu.cs.tweeter.server.service.MD5Hashing;

public class UserDAO implements UserDAOInterface {

    private static final String tableName = "Users";
    private static final String partitionKey = "Alias";
    private static final String firstNameAttr = "FirstName";
    private static final String lastNameAttr = "LastName";
    private static final String passwordAttr = "Password";
    private static final String imageUrlAttr = "ImageUrl";
    private static final String followerCountAttr = "FollowerCount";
    private static final String followeeCountAttr = "FolloweeCount";

    private static AmazonDynamoDB client = AmazonDynamoDBAsyncClientBuilder.standard().withRegion("us-west-1").build();
    private static DynamoDB dynamoDB = new DynamoDB(client);
    private MD5Hashing hashing = new MD5Hashing();

    @Override
    public Item register(RegisterRequest request) throws DataAccessException {

        Table table = dynamoDB.getTable(tableName);

        String url = getS3Dao().upload(request.getAlias(), request.getImageToUpload());

        String alias = request.getAlias();
        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String password = request.getPassword();
        String imageUrl = url;

        String hashedPassword = hashing.hashPassword(password);
        request.setPassword(hashedPassword);

        Item item = new Item().withPrimaryKey("Alias", alias)
                .withString(firstNameAttr, firstName)
                .withString(lastNameAttr, lastName)
                .withString(passwordAttr, hashedPassword)
                .withString(imageUrlAttr, imageUrl)
                .withString(followerCountAttr, "0")
                .withString(followeeCountAttr, "0");

        table.putItem(item);
        return item;
    }

    @Override
    public ItemCollection<QueryOutcome> login(LoginRequest request) {

        ItemCollection<QueryOutcome> outcome = getDynamoDB().getitem(tableName, partitionKey, request.getUsername());
        return outcome;

    }

    @Override
    public Item getUser(UserRequest request) {
        Table table = dynamoDB.getTable(tableName);
        GetItemSpec spec = new GetItemSpec().withPrimaryKey(partitionKey, request.getAlias());

        return table.getItem(spec);
    }

    public Integer getFollowersCount(FollowerCountRequest request) {
        Item userItem = getUser(new UserRequest(request.getUser().getAlias()));

        return userItem.getInt("FollowerCount");
    }

    public Integer getFollowingCount(FollowingCountRequest request) {
        Item userItem = getUser(new UserRequest(request.getUser().getAlias()));

        return userItem.getInt("FolloweeCount");
    }

    public Integer updateFollowerCount(FollowerCountRequest request) {

        Table table = dynamoDB.getTable(tableName);

        UpdateItemSpec update = new UpdateItemSpec()
                .withPrimaryKey(partitionKey, request.getUser().getAlias())
                .withUpdateExpression("set FollowerCount = :r")
                .withValueMap(new ValueMap().withInt(":r", request.getCount()))
                .withReturnValues(ReturnValue.UPDATED_NEW);

        UpdateItemOutcome outcome = table.updateItem(update);
        Item item = outcome.getItem();

        return item.getInt("FollowerCount");
    }

    public Integer updateFollowingCount(FollowingCountRequest request) {

        Table table = dynamoDB.getTable(tableName);

        UpdateItemSpec update = new UpdateItemSpec().withPrimaryKey("Alias", request.getUser().getAlias())
                .withUpdateExpression("set FolloweeCount = :r")
                .withValueMap(new ValueMap()
                        .withInt(":r", request.getCount()))
                .withReturnValues(ReturnValue.UPDATED_NEW);

        UpdateItemOutcome outcome = table.updateItem(update);
        Item item = outcome.getItem();

        return item.getInt("FolloweeCount");
    }

    public DynamoDBUtil getDynamoDB() {
        return new DynamoDBUtil();
    }

    public S3DAO getS3Dao() {
        return new S3DAO();
    }

    public void addUser(User user, String password) {
        Table table = dynamoDB.getTable(tableName);

        Item item = new Item()
                .withPrimaryKey(partitionKey, user.getAlias())
                .withString(firstNameAttr, user.getFirstName())
                .withString(lastNameAttr, user.getLastName())
                .withString(imageUrlAttr, user.getImageUrl())
                .withString(passwordAttr, password)
                .withString(followerCountAttr, "0")
                .withString(followeeCountAttr, "0");

        table.putItem(item);
    }

    public void addUserBatch(List<User> users) {

        // Constructor for TableWriteItems takes the name of the table, which I have stored in TABLE_USER
        TableWriteItems items = new TableWriteItems(tableName);

        // Add each user into the TableWriteItems object
        for (User user : users) {
            Item item = new Item()
                    .withPrimaryKey(partitionKey, user.getAlias())
                    .withString(firstNameAttr, user.getFirstName())
                    .withString(lastNameAttr, user.getLastName())
                    .withString(imageUrlAttr, user.getImageUrl())
                    .withString(followerCountAttr, "0")
                    .withString(followeeCountAttr, "0");
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
//        logger.log("Wrote User Batch");

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
//            logger.log("Wrote more Users");
        }
    }

}
