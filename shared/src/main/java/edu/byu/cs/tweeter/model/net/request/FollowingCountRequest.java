package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingCountRequest {

    private AuthToken authToken;
    private User user;
    private int count;

    private FollowingCountRequest() {}

    public FollowingCountRequest(AuthToken authToken, User user, int count) {
        this.authToken = authToken;
        this.user = user;
        this.count = count;
    }

    public FollowingCountRequest(AuthToken authToken, User user) {
        this.authToken = authToken;
        this.user = user;
    }

    public FollowingCountRequest(User user, int count) {
        this.user = user;
        this.count = count;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


}
