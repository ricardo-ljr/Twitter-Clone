package edu.byu.cs.tweeter.client.presenter;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowerService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenterUser implements FollowerService.GetFollowersObserver, UserService.GetUserObserver {

    private final AuthToken authToken;

    public FollowersPresenter(View view, User user, AuthToken authToken) {
        super(view, user);
        this.authToken = authToken;
    }

    public interface View extends PagedView<User>{}

    @Override
    public void handleSuccessFollower(List<User> users, boolean hasMorePages, User lastFollower) throws MalformedURLException {
        handleSuccess(users, hasMorePages, lastFollower);
    }

    // TODO: Ask about how to implement this with the observer
    public void getUsers(String alias) {
        UserService.getUsers(Cache.getInstance().getCurrUserAuthToken(), alias, this);
    }

    public void loadMoreItems() throws MalformedURLException {
        if (loadItems()) {
            getFollowers(authToken, getUser(), getLastFollower());
        }
    }

    public void getFollowers(AuthToken authToken, User targetUser, User lastFollowee) {
        getFollowersService(this).getFollowers(authToken, targetUser, lastFollowee);
    }

    public FollowerService getFollowersService(FollowerService.GetFollowersObserver observer) {
        return new FollowerService(observer);
    }

}
