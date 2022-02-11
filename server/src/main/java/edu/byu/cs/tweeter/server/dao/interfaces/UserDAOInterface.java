package edu.byu.cs.tweeter.server.dao.interfaces;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;

import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.server.dao.DataAccessException;

public interface UserDAOInterface {


    public Item register(RegisterRequest request) throws DataAccessException;

    public ItemCollection<QueryOutcome> login(LoginRequest request);

    public Item getUser(UserRequest request);

    public Integer getFollowersCount(FollowerCountRequest request);
    public Integer getFollowingCount(FollowingCountRequest request);
    public Integer updateFollowerCount(FollowerCountRequest request);
    public Integer updateFollowingCount(FollowingCountRequest request);

}
