package edu.byu.cs.tweeter.server.lambda;

import edu.byu.cs.tweeter.server.dao.DynamoDBDAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoDBDAOFactoryInterface;

public class HandlerConfig {

    private static HandlerConfig config;

    private HandlerConfig() {}

    public static HandlerConfig getInstance() {

        if (config == null) {
            config = new HandlerConfig();
        }

        return config;
    }

    public DynamoDBDAOFactoryInterface getFactory() {
        return new DynamoDBDAOFactory();
    }
}
