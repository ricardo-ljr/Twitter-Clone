package edu.byu.cs.tweeter.model.net.response;

import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;

public class FollowingCountResponse extends Response{

    private AuthToken authToken;
    private User user;
    private int count;

    public FollowingCountResponse() {
        super();
    }

    public FollowingCountResponse(User user, AuthToken authToken, int count) {
        super(true, null);
        this.user = user;
        this.authToken = authToken;
        this.count = count;
    }

    public FollowingCountResponse(Integer followingCount) {
        super(true, null);
        this.count = followingCount;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowingCountResponse that = (FollowingCountResponse) o;
        return count == that.count && Objects.equals(authToken, that.authToken) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken, user, count);
    }
}
