package edu.byu.cs.tweeter.client.presenter;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class Presenter<T> {

    private User user;
    private boolean hasMorePages = true;
    private Status lastStatus = null;
    private User lastFollower;
    private boolean isLoading = false;

    protected Presenter(User user, boolean hasMorePages, Status lastStatus, boolean isLoading) {
        this.user = user;
        this.hasMorePages = hasMorePages;
        this.lastStatus = lastStatus;
        this.isLoading = isLoading;
        this.lastFollower = lastFollower;
    }

    protected Presenter(User user) {
        this.user = user;
    }

    // View Interface
    public interface PagedView<U> extends ServiceView {
        void navigateToUser(User user);
        void setLoading(boolean value) throws MalformedURLException;
        void addItems(List<U> statuses);
    }


    protected boolean isHasMorePages() {
        return hasMorePages;
    }

    protected Status getLastStatus() {
        return lastStatus;
    }

    protected void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    protected void setLastStatus(Status lastStatus) {
        this.lastStatus = lastStatus;
    }

    protected void setIsLoading(boolean loading) {
        isLoading = loading;
    }

    protected boolean getIsLoading() {
        return isLoading;
    }

    protected User getUser() {
        return user;
    }

    // TODO: Ask if I can have public methods in my abstract class
    public User getLastFollower() {
        return lastFollower;
    }

    public void setLastFollower(User lastFollower) {
        this.lastFollower = lastFollower;
    }

    protected abstract boolean loadItems() throws MalformedURLException;

    protected abstract void handleSuccess(List<T> items, boolean hasMorePages, T last) throws MalformedURLException;

}
