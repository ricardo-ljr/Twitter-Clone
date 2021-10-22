package edu.byu.cs.tweeter.client.presenter;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class Presenter<T> {

    private PagedView view;

    private User user;
    private boolean hasMorePages = true;
    private Status lastStatus = null;
    private User lastFollower;
    private boolean isLoading = false;

    protected Presenter(PagedView view, User user) {
        this.view = view;
        this.user = user;
    }

    // View Interface
    public interface PagedView<U> extends ServiceView {
        void navigateToUser(User user);
        void setLoading(boolean value) throws MalformedURLException;
        void addItems(List<U> statuses);
    }

    protected PagedView getView() {
        return view;
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

    protected User getLastFollower() {
        return lastFollower;
    }

    protected void setLastFollower(User lastFollower) {
        this.lastFollower = lastFollower;
    }

    protected boolean loadItems() throws MalformedURLException {
        if (!getIsLoading() && isHasMorePages()) {
            setIsLoading(true);
            getView().setLoading(true);
            return true;
        } else {
            return false;
        }
    }

    // Generic abstract method
    protected abstract void handleSuccess(List<T> items, boolean hasMorePages, T last) throws MalformedURLException;

    public void handleFailureObserver(String message) {
        view.clearInfoMessage();
        view.displayErrorMessage(message);
    }

    public void handleExceptionObserver(Exception e) {
        view.clearInfoMessage();
        view.displayErrorMessage(e.getMessage());
    }

    public void handleSuccessUser(User user) {
        view.navigateToUser(user);
    }

}
