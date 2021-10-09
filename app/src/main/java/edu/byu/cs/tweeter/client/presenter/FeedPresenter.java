package edu.byu.cs.tweeter.client.presenter;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FeedService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenterStatus implements FeedService.GetFeedObserver, UserService.GetUserObserver {

    public interface View extends PagedView<Status>{ }

    public FeedPresenter(View view, User user) {
        super(view, user);
    }

    @Override
    public void handleSuccessFeed(List<Status> statuses, boolean hasMorePages, Status lastStatus) throws MalformedURLException {
        handleSuccess(statuses, hasMorePages, lastStatus);
    }

    // Overriding previous loadMoreItems with abstract loaditems() method
    public void loadMoreItems() throws MalformedURLException {
        if(loadItems()) {
            getFeed(this, getUser(), getLastStatus());
        }
    }

    public void getFeed(FeedService.GetFeedObserver observer, User targetUser, Status lastStatus) {
        getFeedService(this).getFeed(observer,targetUser, lastStatus);
    }

    public FeedService getFeedService(FeedService.GetFeedObserver observer) {
        return new FeedService(observer);
    }

    public void getUsers(String alias) {
        UserService.getUsers(Cache.getInstance().getCurrUserAuthToken(), alias, this);
    }

}
