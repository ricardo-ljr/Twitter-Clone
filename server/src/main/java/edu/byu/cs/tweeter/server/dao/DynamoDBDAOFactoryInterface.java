package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.implementation.AuthTableDAO;
import edu.byu.cs.tweeter.server.dao.implementation.FeedDao;
import edu.byu.cs.tweeter.server.dao.implementation.FollowDAO;
import edu.byu.cs.tweeter.server.dao.implementation.S3DAO;
import edu.byu.cs.tweeter.server.dao.implementation.StoryDAO;
import edu.byu.cs.tweeter.server.dao.implementation.UserDAO;

public interface DynamoDBDAOFactoryInterface {

    public UserDAO getUserDAO();

    public AuthTableDAO getAuthTableDAO();

    public S3DAO getS3DAO();

    public StoryDAO getStoryDAO();

    public FollowDAO getFollowDAO();

    public FeedDao getFeedDAO();
}
