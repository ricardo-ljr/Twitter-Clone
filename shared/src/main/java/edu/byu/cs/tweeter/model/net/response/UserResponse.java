package edu.byu.cs.tweeter.model.net.response;

import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.User;

public class UserResponse extends Response{

    private User user;

    public UserResponse(String message) {
        super(false, message);
    }

    public UserResponse(User user) {
        super(true, null);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserResponse that = (UserResponse) o;
        return Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
