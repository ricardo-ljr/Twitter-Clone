package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.UserDTO;
import edu.byu.cs.tweeter.model.net.request.FeedQueueMessage;
import edu.byu.cs.tweeter.server.dao.implementation.FeedDao;
import edu.byu.cs.tweeter.server.dao.implementation.UserDAO;
import edu.byu.cs.tweeter.server.util.JsonSerializer;

public class FeedQueueProcessor implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage msg : event.getRecords()) {

            FeedQueueMessage feed = JsonSerializer.deserialize(msg.getBody(), FeedQueueMessage.class);
            List<User> followers = feed.users;
            Status status = feed.status;

            new FeedDao().putFeed(status, followers);
        }
        return null;
    }
}
