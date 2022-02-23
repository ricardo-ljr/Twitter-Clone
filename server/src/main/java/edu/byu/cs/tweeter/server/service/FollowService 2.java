package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.DynamoDBDAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.OLDFollowDAO;
import edu.byu.cs.tweeter.server.dao.implementation.AuthTableDAO;
import edu.byu.cs.tweeter.server.dao.implementation.FollowDAO;
import edu.byu.cs.tweeter.server.dao.implementation.UserDAO;
import edu.byu.cs.tweeter.server.util.FakeData;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    private final DynamoDBDAOFactoryInterface factory;

    public FollowService(DynamoDBDAOFactoryInterface factory) {
        this.factory = factory;
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link OLDFollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {

        FollowDAO followDAO = factory.getFollowDAO();
        UserDAO userDAO = factory.getUserDAO();
        AuthTableDAO authTableDAO = this.factory.getAuthTableDAO();

//        if (!authTableDAO.validateUser(request.getAuthToken(), request.getFollowerAlias())) {
//            throw new RuntimeException("User Session Timed Out");
//        }

        int followingCount;
        List<User> followees = new ArrayList<>();
        List<User> responseFollowees = new ArrayList<>(request.getLimit());

        ItemCollection<QueryOutcome> items = followDAO.getFollowing(request);

        Iterator<Item> itemIterator = items.iterator();

        while (itemIterator.hasNext()) {

            Item item = itemIterator.next();

            Item findUser = userDAO.getUser(new UserRequest(item.getString("Followee")));

            String firstName = findUser.getString("FirstName");
            String lastName = findUser.getString("LastName");
            String alias = findUser.getString("Alias");
            String imageUrl = findUser.getString("ImageUrl");

            User newUser = new User(firstName, lastName, alias, imageUrl);
            followees.add(newUser);
        }

        followingCount = followees.size();

        Item requestUser = userDAO.getUser(new UserRequest(request.getFollowerAlias()));

        String firstname = requestUser.getString("FirstName");
        String lastname = requestUser.getString("LastName");
        String alias = requestUser.getString("Alias");
        String imageUrl = requestUser.getString("ImageUrl");

        User rUser = new User(firstname, lastname, alias, imageUrl);

        userDAO.updateFollowingCount(new FollowingCountRequest(rUser, followingCount));

        boolean hasMorePages = false;

        if (request.getLimit() > 0) {
            if (followees != null) {
                int index = getFolloweesStartingIndex(request.getLastFolloweeAlias(), followees);

                for (int limitCounter = 0; index < followees.size() && limitCounter < request.getLimit(); index++, limitCounter++) {
                    responseFollowees.add(followees.get(index));
                }

                hasMorePages = index < followees.size();
            }
        }

        return new FollowingResponse(responseFollowees, hasMorePages);
    }

    public FollowerResponse getFollowers(FollowerRequest request) {

        FollowDAO followDAO = factory.getFollowDAO();
        UserDAO userDAO = factory.getUserDAO();
        AuthTableDAO authTableDAO = this.factory.getAuthTableDAO();

//        if (!authTableDAO.validateUser(request.getAuthToken(), request.getFollowerAlias())) {
//            throw new RuntimeException("User Session Timed Out");
//        }

        int followerCount;
        List<User> followers = new ArrayList<>();
        List<User> responseFollowers = new ArrayList<>(request.getLimit());

        ItemCollection<QueryOutcome> items = followDAO.getFollowers(request);

        Iterator<Item> itemIterator = items.iterator();

        while (itemIterator.hasNext()) {

            Item item = itemIterator.next();

            Item findUser = userDAO.getUser(new UserRequest(item.getString("Follower")));

            String firstName = findUser.getString("FirstName");
            String lastName = findUser.getString("LastName");
            String alias = findUser.getString("Alias");
            String imageUrl = findUser.getString("ImageUrl");

            User newUser = new User(firstName, lastName, alias, imageUrl);
            followers.add(newUser);
        }

        followerCount = followers.size();

        Item requestUser = userDAO.getUser(new UserRequest(request.getFollowerAlias()));

        String firstname = requestUser.getString("FirstName");
        String lastname = requestUser.getString("LastName");
        String alias = requestUser.getString("Alias");
        String imageUrl = requestUser.getString("ImageUrl");

        User rUser = new User(firstname, lastname, alias, imageUrl);

        userDAO.updateFollowerCount(new FollowerCountRequest(rUser, followerCount));

        boolean hasMorePages = false;

        if (request.getLimit() > 0) {
            if (followers != null) {
                int index = getFolloweesStartingIndex(request.getLastFollowerAlias(), followers);

                for (int limitCounter = 0; index < followers.size() && limitCounter < request.getLimit(); index++, limitCounter++) {
                    responseFollowers.add(followers.get(index));
                }

                hasMorePages = index < followers.size();
            }
        }

        return new FollowerResponse(responseFollowers, hasMorePages);
    }

    public FollowResponse follow(FollowRequest request) {

        FollowDAO followDAO = factory.getFollowDAO();

        try {
            followDAO.follow(request);
            return new FollowResponse(true);
        } catch (Exception e) {
            return new FollowResponse(false);
        }
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {

        FollowDAO followDAO = factory.getFollowDAO();

        DeleteItemResult deleteItemResult = followDAO.unfollow(request);

        if (deleteItemResult.getAttributes() == null) {
            return new UnfollowResponse(false);
        } else {
            return new UnfollowResponse(true);
        }
    }

    private int getFolloweesStartingIndex(String lastFolloweeAlias, List<User> allFollowees) {

        int index = 0;

        if (lastFolloweeAlias != null) {
            for (int i = 0; i < allFollowees.size(); i++) {
                if (lastFolloweeAlias.equals(allFollowees.get(i).getAlias())) {
                    index = i + 1;
                    break;
                }
            }
        }
        return index;
    }
}


