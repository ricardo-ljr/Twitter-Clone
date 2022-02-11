package edu.byu.cs.tweeter.model.net.request;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.domain.UserDTO;

public class FeedQueueMessage {

    public List<User> users;
    public Status status;

    public FeedQueueMessage(List<User> users, Status status) {
        this.users = users;
        this.status = status;
    }
}
