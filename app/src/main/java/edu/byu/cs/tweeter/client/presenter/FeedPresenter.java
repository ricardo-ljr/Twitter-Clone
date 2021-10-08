package edu.byu.cs.tweeter.client.presenter;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FeedService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter implements FeedService.GetFeedObserver, UserService.GetUserObserver {

    @Override
    public void handleSuccessFeed(List<Status> statuses, boolean hasMorePages, Status lastStatus) throws MalformedURLException {
        view.setLoading(false);
        view.addItems(statuses);
        this.hasMorePages = hasMorePages;
        this.lastStatus = lastStatus;
        isLoading = false;
    }

    @Override
    public void handleFailure(String message) {}

    @Override
    public void handleException(Exception e) {}

    @Override
    public void handleSuccessUser(User user) {
        view.navigateToUser(user);
    }

    private View view;
    private boolean isLoading = false;
    private boolean hasMorePages = true;
    private User user;
    private Status lastStatus = null;

    public interface View extends ServiceView {
        void navigateToUser(User user);
        void setLoading(boolean value) throws MalformedURLException;
        void addItems(List<Status> statuses);
    }

    public FeedPresenter(View view, User user) {
        this.view = view;
        this.user = user;
    }

    public void loadMoreItems() throws MalformedURLException {
        if (!isLoading && hasMorePages) {
            isLoading = true;
            view.setLoading(true);

            new FeedService().getFeed(this, user, lastStatus);
        }
    }

    public void getUsers(String alias) {
        UserService.getUsers(Cache.getInstance().getCurrUserAuthToken(), alias, this);
    }

}
