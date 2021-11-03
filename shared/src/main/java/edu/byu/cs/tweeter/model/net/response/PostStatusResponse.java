package edu.byu.cs.tweeter.model.net.response;

import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.Status;

public class PostStatusResponse extends Response{

    private Status status;

    public PostStatusResponse(String message) {
        super(false, message);
    }

    public PostStatusResponse(Status status) {
        super(true, null);
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostStatusResponse that = (PostStatusResponse) o;
        return Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }
}
