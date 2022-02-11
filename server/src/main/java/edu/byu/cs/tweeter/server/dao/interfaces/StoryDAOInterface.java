package edu.byu.cs.tweeter.server.dao.interfaces;

import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;

public interface StoryDAOInterface {

    void postStatus(Status status);

    ItemCollection<QueryOutcome> getStories(StoryRequest request);

}
