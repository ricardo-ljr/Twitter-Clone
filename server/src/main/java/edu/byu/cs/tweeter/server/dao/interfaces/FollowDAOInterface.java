package edu.byu.cs.tweeter.server.dao.interfaces;

import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;

import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;

public interface FollowDAOInterface {

    ItemCollection<QueryOutcome> getFollowing(FollowingRequest request);
    ItemCollection<QueryOutcome> getFollowers(FollowerRequest request);

    PutItemOutcome follow(FollowRequest request);
    DeleteItemResult unfollow(UnfollowRequest request);



}
