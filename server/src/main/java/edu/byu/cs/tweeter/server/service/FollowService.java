package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.util.FakeData;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        return getFollowingDAO().getFollowees(request);
    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowDAO getFollowingDAO() {
        return new FollowDAO();
    }

    public FollowerResponse getFollowers(FollowerRequest request) {
        return getFollowingDAO().getFollowers(request);
    }

    public FollowResponse follow(FollowRequest request) {
        return new FollowResponse(request.getUser(), request.getAuthToken());
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        return new UnfollowResponse(request.getUser(), request.getAuthToken());
    }

    public FollowingCountResponse followingCount(FollowingCountRequest request) {
        return new FollowingCountResponse(request.getUser(), request.getAuthToken(), request.getCount());
    }

    public FollowerCountResponse followerCount(FollowerCountRequest request) {
        return new FollowerCountResponse(request.getUser(), request.getAuthToken(), request.getCount());
    }

    FakeData getFakeData() {
        return new FakeData();
    }
}
