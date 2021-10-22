package edu.byu.cs.tweeter.client.presenter;


import android.util.Log;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowingService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.main.following.FollowingFragment;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenterUser implements FollowingService.GetFollowingObserver, UserService.GetUserObserver {

    private static final String LOG_TAG = "FollowingPresenter";
    private static final int PAGE_SIZE = 10;

    private final AuthToken authToken;

    /**
     * Creates an instance.
     *
     * @param view      the view for which this class is the presenter.
     * @param targetUser      the user that is currently logged in.
     * @param authToken the auth token for the current session.
     */
    public FollowingPresenter(FollowingFragment view, User targetUser, AuthToken authToken) {
        super(view, targetUser);
        this.authToken = authToken;
    }

    public interface View extends PagedView<User>{ }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void loadMoreItems() throws MalformedURLException {
        if (loadItems()) {
            getFollowing(authToken, getUser(), PAGE_SIZE, getLastFollower());
        }
    }

    public void getTargetUser(String alias) {
        UserService.getUsers(Cache.getInstance().getCurrUserAuthToken(), alias, this);
    }

    /**
     * Requests the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned for a previous request. This is an asynchronous
     * operation.
     *
     * @param authToken    the session auth token.
     * @param targetUser   the user for whom followees are being retrieved.
     * @param limit        the maximum number of followees to return.
     * @param lastFollowee the last followee returned in the previous request (can be null).
     */
    public void getFollowing(AuthToken authToken, User targetUser, int limit, User lastFollowee) {
        getFollowingService(this).getFollowing(authToken, targetUser, limit, lastFollowee);
    }

    /**
     * Returns an instance of {@link FollowingService}. Allows mocking of the FollowService class
     * for testing purposes. All usages of FollowService should get their FollowService
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    public FollowingService getFollowingService(FollowingService.GetFollowingObserver observer) {
        return new FollowingService(observer);
    }

    @Override
    public void handleSuccessStatus(List<User> users, boolean hasMorePages, User lastFollowee) throws MalformedURLException {
        handleSuccess(users, hasMorePages, lastFollowee);
    }

}
