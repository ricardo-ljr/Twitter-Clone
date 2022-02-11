package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.implementation.AuthTableDAO;
import edu.byu.cs.tweeter.server.dao.implementation.FeedDao;
import edu.byu.cs.tweeter.server.dao.implementation.FollowDAO;
import edu.byu.cs.tweeter.server.dao.implementation.S3DAO;
import edu.byu.cs.tweeter.server.dao.implementation.StoryDAO;
import edu.byu.cs.tweeter.server.dao.implementation.UserDAO;

public class DynamoDBDAOFactory implements DynamoDBDAOFactoryInterface{

    @Override
    public UserDAO getUserDAO() {
        return new UserDAO();
    }

    @Override
    public AuthTableDAO getAuthTableDAO() {
        return new AuthTableDAO();
    }

    @Override
    public S3DAO getS3DAO() {
        return new S3DAO();
    }

    @Override
    public StoryDAO getStoryDAO() {
        return new StoryDAO();
    }

    @Override
    public FollowDAO getFollowDAO() {
        return new FollowDAO();
    }

    @Override
    public FeedDao getFeedDAO() {
        return new FeedDao();
    }

}
