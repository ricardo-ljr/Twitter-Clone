package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;

import java.sql.Timestamp;
import java.util.Iterator;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.DynamoDBDAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.implementation.AuthTableDAO;
import edu.byu.cs.tweeter.server.dao.implementation.UserDAO;
import edu.byu.cs.tweeter.server.util.FakeData;

public class UserService {

    private final DynamoDBDAOFactoryInterface factory;
    private MD5Hashing hashing = new MD5Hashing();

    public UserService(DynamoDBDAOFactoryInterface factory) {
        this.factory = factory;
    }

    public LoginResponse login(LoginRequest request) {
        UserDAO dao = this.factory.getUserDAO();
        AuthTableDAO authTableDAO = this.factory.getAuthTableDAO();
        ItemCollection<QueryOutcome> items = dao.login(request);

        Iterator<Item> itemIterator = items.iterator();
        Item item = itemIterator.next();

        if (item != null) {
                User newUser = new User(item.getString("FirstName"), item.getString("LastName"), request.getUsername(), item.getString("ImageUrl"));
                newUser.setPassword(item.getString("Password"));

                if (newUser.getPassword().equals(hashing.hashPassword(request.getPassword()))) {

//                    AuthToken token = authTableDAO.addToken(request.getUsername());
                    AuthToken authToken = new AuthToken();
//                    authTableDAO.createSession(authToken);
                    return new LoginResponse(newUser, authToken);
                } else {
                    return new LoginResponse("Password does not match");
                }
            } else {
                return new LoginResponse("User does not exist");
            }

    }

    public RegisterResponse register(RegisterRequest request) throws DataAccessException {

        AuthTableDAO authTableDAO = this.factory.getAuthTableDAO();
        UserDAO userDAO = this.factory.getUserDAO();

        Item outcome = userDAO.register(request);

        // TODO: Return User - make sure dynamodb isnt interacting
        if (outcome != null) {
            User newUser = new User(outcome.getString("FirstName"),
                    outcome.getString("LastName"),
                    outcome.getString("Alias"),
                    outcome.getString("ImageUrl"));


//            AuthToken authToken = authTableDAO.addToken(outcome.getString("Alias"));
            AuthToken authToken = new AuthToken();
            String date = new Timestamp(System.currentTimeMillis()).toString();

//            authTableDAO.createSession(authToken, date);

            RegisterResponse response = new RegisterResponse(newUser, authToken);
            return response;

        } else {
            RegisterResponse failedResponse = new RegisterResponse(false, "Failed to register new user");

            return failedResponse;
        }
    }

    public LogoutResponse logout(LogoutRequest request) {
        // TODO: Finish logout
        return new LogoutResponse(true, "logout successfull");
    }

    public UserResponse getUser(UserRequest request) {

        UserDAO userDAO = this.factory.getUserDAO();

        Item outcome = userDAO.getUser(request);


        User newUser = new User(outcome.getString("FirstName"),
                outcome.getString("LastName"),
                outcome.getString("Alias"),
                outcome.getString("ImageUrl"));

        return new UserResponse(newUser);
    }

    public FollowerCountResponse getFollowerCount(FollowerCountRequest request) {
        UserDAO userDAO = factory.getUserDAO();
        return new FollowerCountResponse(userDAO.getFollowersCount(request));
    }

    public FollowingCountResponse getFollowingCount(FollowingCountRequest request) {
        UserDAO userDAO = factory.getUserDAO();
        return new FollowingCountResponse(userDAO.getFollowingCount(request));
    }

}
