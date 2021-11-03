package edu.byu.cs.tweeter.model.net.response;

import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UnfollowResponse extends Response{

    private AuthToken authToken;
    private User user;

    public UnfollowResponse() {
        super();
    }

    public UnfollowResponse(User user, AuthToken authToken) {
        super(true, null);
        this.user = user;
        this.authToken = authToken;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnfollowResponse that = (UnfollowResponse) o;
        return Objects.equals(authToken, that.authToken) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken, user);
    }
}
