package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowerService;
import edu.byu.cs.tweeter.client.model.service.FollowingService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter implements FollowerService.GetFollowersObserver, UserService.GetUserObserver {

    private static final String LOG_TAG = "FollowersPresenter";
    private static final int PAGE_SIZE = 10;


    private final View view;
    private final User targetUser;
    private final AuthToken authToken;

    private User lastFollowee;
    private boolean hasMorePages = true;
    private boolean isLoading = false;

    public FollowersPresenter(View view, User user, AuthToken authToken) {
        this.view = view;
        this.targetUser = user;
        this.authToken = authToken;
    }

    public interface View {
        void addItems(List<User> followees);
        void setLoading(boolean value);
        void navigateToUser(User user);
        void displayMessage(String message);
    }


    @Override
    public void handleSuccessFollower(List<User> users, boolean hasMorePages, User lastFollower) {
        view.setLoading(false);
        view.addItems(users);
        this.hasMorePages = hasMorePages;
        this.lastFollowee = lastFollower;
        isLoading = false;
    }

    @Override
    public void handleFailure(String message) {
//        Toast.makeText(getContext(), "Failed to get followers: " + message, Toast.LENGTH_LONG).show();
        view.displayMessage("Failed to get followers: " + message);
    }

    @Override
    public void handleException(Exception exception) {
//        Toast.makeText(getContext(), "Failed to get followers because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        view.displayMessage("Failed to get followers because of exception: " + exception.getMessage());
    }

    @Override
    public void handleSuccessUser(User user) {
        view.navigateToUser(user);
    }

    public void getTargetUser(String alias) {
        UserService.getUsers(Cache.getInstance().getCurrUserAuthToken(), alias, this);
    }

    public View getView() {
        return view;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public User getLastFollowee() {
        return lastFollowee;
    }

    public void setLastFollowee(User lastFollowee) {
        this.lastFollowee = lastFollowee;
    }

    public boolean isHasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            isLoading = true;
            view.setLoading(true);

            getFollowers(authToken, targetUser, lastFollowee);
        }
    }

    public void getFollowers(AuthToken authToken, User targetUser, User lastFollowee) {
        getFollowersService(this).getFollowers(authToken, targetUser, lastFollowee);
    }

    public FollowerService getFollowersService(FollowerService.GetFollowersObserver observer) {
        return new FollowerService(observer);
    }

}
