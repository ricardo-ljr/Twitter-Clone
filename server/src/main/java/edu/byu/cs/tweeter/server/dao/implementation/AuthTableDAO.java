package edu.byu.cs.tweeter.server.dao.implementation;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.DynamoDBUtil;
import edu.byu.cs.tweeter.server.dao.interfaces.AuthTableDAOInterface;

public class AuthTableDAO implements AuthTableDAOInterface {

    private static final String tableName = "AuthToken";
    private static final String partitionKey = "Password";
    private static final String sortKey = "TimeStamp";

    private static AmazonDynamoDB client = AmazonDynamoDBAsyncClientBuilder.standard().withRegion("us-west-1").build();
    private static DynamoDB dynamoDB = new DynamoDB(client);

    private static final int SESSION_TIMEOUT = 5;


    @Override
    public void createSession(AuthToken token, String date) {

        Table table = dynamoDB.getTable(tableName);

        Item item = new Item()
                .withPrimaryKey(partitionKey, token.getToken(), sortKey, date);

        table.putItem(item);

    }

    @Override
    public boolean validateUser(AuthToken authToken) {

        try {
            Table table = dynamoDB.getTable(tableName);

            Item item = table.getItem(partitionKey, authToken.getToken());

            AuthToken token = new AuthToken(item.getString(partitionKey), item.getString(sortKey));

            LocalDateTime currTime = LocalDateTime.now();
            LocalDateTime expirationTime = LocalDateTime.parse(authToken.getDatetime());

            if (currTime.isAfter(expirationTime)) {
                return false;
            } else {
                authToken.setDatetime(currTime.plusMinutes(SESSION_TIMEOUT).toString());
//                updateSession(token);
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public void endSession(AuthToken token) {
        Table table = dynamoDB.getTable(tableName);
        table.deleteItem(partitionKey, token.getToken());
    }


    public DynamoDBUtil getDynamoDB() {
        return new DynamoDBUtil();
    }
}
