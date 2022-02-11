package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import java.util.ArrayList;

// Simple implementation to remove duplication across dynamoDB Client
public class DynamoDBUtil {

    private static AmazonDynamoDB client = AmazonDynamoDBAsyncClientBuilder.standard().withRegion("us-west-1").build();
    private static DynamoDB dynamoDB = new DynamoDB(client);

    public DynamoDB getDynamoDB() {
        return dynamoDB;
    }

    public AmazonDynamoDB getClient() {
        return client;
    }

    public boolean createItem(String tableName, String partitionkey, Object keyValue, ArrayList<String> attributes, ArrayList<String> attributeValues) {
        Table table = dynamoDB.getTable(tableName);

        int listSize = attributeValues.size();

        Item item = new Item().withPrimaryKey(partitionkey, keyValue);

        for (int i = 0; i < attributes.size(); i++) {
            if (i < listSize) {
                item.withString(attributes.get(i), attributeValues.get(i));
            }
        }

        table.putItem(item);

        return true;
    }

    public ItemCollection<QueryOutcome> getitem(String tableName, String key, String keyValue) {

        Table table = dynamoDB.getTable(tableName);

        QuerySpec spec = new QuerySpec().withKeyConditionExpression(key + " = :_id").withValueMap( new ValueMap().withString(":_id", keyValue));

        return table.query(spec);
    }

    public QuerySpec getItemSpec(String tableName, String key, String keyValue) {

        Table table = dynamoDB.getTable(tableName);

        QuerySpec spec = new QuerySpec().withKeyConditionExpression(key + " = :_id").withValueMap( new ValueMap().withString(":_id", keyValue));

        return spec;
    }

    public void createItemTwoKeys(String tableName, String partitionKey, String keyValue, String sortKey, String sortKeyValue, boolean withAttributes, String attributeName, String attributeValue) {
        Table table = dynamoDB.getTable(tableName);

        Item item = new Item().withPrimaryKey(partitionKey, keyValue).with(sortKey, sortKeyValue);

        if (withAttributes) {
            item = item.with(attributeName, attributeValue);
        }

        table.putItem(item);

    }



}
