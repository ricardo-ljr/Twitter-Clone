package edu.byu.cs.tweeter.model.net.request;

public class UserRequest {

    private String alias;

    private UserRequest() {}

    public UserRequest(String username) {
        this.alias = username;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
