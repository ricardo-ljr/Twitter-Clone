package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class PostStatusRequest {

    // TODO: Make a new status and post status?

    private Status status;

    private PostStatusRequest() {}

    public PostStatusRequest(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
