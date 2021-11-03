package edu.byu.cs.tweeter.model.net.response;

import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerCountResponse extends Response{

    private AuthToken authToken;
    private User user;
    private int count;

    public FollowerCountResponse() {
        super();
    }

    public FollowerCountResponse(User user, AuthToken authToken, int count) {
        super(true, null);
        this.user = user;
        this.authToken = authToken;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowerCountResponse that = (FollowerCountResponse) o;
        return count == that.count && Objects.equals(authToken, that.authToken) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken, user, count);
    }
}
