package edu.byu.cs.tweeter.model.net.response;

import java.util.List;
import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.Status;

public class FeedResponse extends PagedResponse{

    private List<Status> statuses;

    public FeedResponse() {
        super();
    }

    public FeedResponse(String message) {
        super(false,message,false );
    }

    public FeedResponse(List<Status> status, boolean hasMorePages) {
        super(true, hasMorePages);
        this.statuses = status;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> status) {
        this.statuses = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedResponse that = (FeedResponse) o;
        return Objects.equals(statuses, that.statuses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statuses);
    }

}
