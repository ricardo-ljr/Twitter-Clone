package edu.byu.cs.tweeter.server.dao.interfaces;

import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.UserDTO;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;

public interface FeedDAOInterface {

    ItemCollection<QueryOutcome> getFeedItems(FeedRequest request);
    FeedResponse getFeed(String alias, String lastStatus, int limit);
    void putFeed(Status status, List<User> followers);
}
