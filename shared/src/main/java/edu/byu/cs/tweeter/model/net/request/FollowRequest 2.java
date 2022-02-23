package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowRequest {

    private AuthToken authToken;
    private User targetUser;
    private User currentUser;

    public FollowRequest() {}

    public FollowRequest(AuthToken authToken, User user) {
        this.authToken = authToken;
        this.targetUser = user;
    }

    public FollowRequest(AuthToken authToken, User targetUser, User currentUser) {
        this.authToken = authToken;
        this.targetUser = targetUser;
        this.currentUser = currentUser;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

}