package edu.byu.cs.tweeter.client.presenter;

import android.widget.Toast;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.FollowerService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter implements FollowerService.GetFollowersObserver, UserService.GetUserObserver {

    private static final String LOG_TAG = "FollowersPresenter";
    private static final int PAGE_SIZE = 10;


    @Override
    public void handleSuccessFollower(List<User> users, boolean hasMorePages, User lastFollower) {
        view.setLoading(false);
        view.addItems(users);
        this.hasMorePages = hasMorePages;
        isLoading = false;

    }

    @Override
    public void handleFailureFollower(String message) {
//        Toast.makeText(getContext(), "Failed to get followers: " + message, Toast.LENGTH_LONG).show();
        view.displayMessage("Failed to get followers: " + message);
    }

    @Override
    public void handleExceptionFollower(Exception exception) {
//        Toast.makeText(getContext(), "Failed to get followers because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        view.displayMessage("Failed to get followers because of exception: " + exception.getMessage());
    }

    private final View view;
    private final User targetUser;

    private User lastFollowee;
    private boolean hasMorePages = true;
    private boolean isLoading = false;

    @Override
    public void handleSuccessUser(User user) {
        view.navigateToUser(user);
    }

    @Override
    public void handleFailureUser(String message) {

    }

    @Override
    public void handleExceptionUser(Exception exception) {

    }

    public interface View {
        void addItems(List<User> followees);
        void setLoading(boolean value);
        void navigateToUser(User user);
        void displayMessage(String message);
    }

    public FollowersPresenter(View view, User user) {
        this.view = view;
        this.targetUser = user;
    }

    public void getUsers(String alias) {
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

            new FollowerService().getFollowers(this, targetUser, lastFollowee);
        }
    }

}