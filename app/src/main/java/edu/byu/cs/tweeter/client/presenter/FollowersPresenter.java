package edu.byu.cs.tweeter.client.presenter;

import android.widget.Toast;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.FollowerService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter implements FollowerService.GetFollowersObserver {

    private static final String LOG_TAG = "FollowersPresenter";
    private static final int PAGE_SIZE = 10;


    @Override
    public void handleSuccess(List<User> users, boolean hasMorePages, User lastFollower) {
        view.setLoading(false);
        view.addItems(users);
        this.hasMorePages = hasMorePages;
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

    private final View view;
    private final User targetUser;
    private final AuthToken authToken;

    private User lastFollowee;
    private boolean hasMorePages = true;
    private boolean isLoading = false;

    public interface View {
        void addItems(List<User> followees);
        void setLoading(boolean value);
        void navigateToUser(User user);
        void displayMessage(String message);
    }

    public FollowersPresenter(View view, User user, AuthToken token) {
        this.view = view;
        this.targetUser = user;
        this.authToken = token;
    }

    public View getView() {
        return view;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public AuthToken getAuthToken() {
        return authToken;
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

            getFollowers(authToken, targetUser, PAGE_SIZE, lastFollowee);
        }
    }

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollowee) {
        getFollowersService(this).getFollowers(authToken, targetUser, limit, lastFollowee);
    }

    public FollowerService getFollowersService(FollowerService.GetFollowersObserver observer) {
        return new FollowerService(observer);
    }



}
