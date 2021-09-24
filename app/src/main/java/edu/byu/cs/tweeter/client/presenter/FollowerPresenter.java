package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.FollowersService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresenter implements FollowersService.GetFollowersObserver {

    private static final String LOG_TAG = "FollowersPresenter";
    private static final int PAGE_SIZE = 10;

    private final View view;
    private final User targetUser;
    private final AuthToken authToken;

    private User lastFollowee;
    private boolean hasMorePages = true;
    private boolean isLoading = false;

    public interface View {
        void addItems(List<User> newUsers);
        void navigateToUser(User user);
        void displayErrorMessage(String message);
        void setLoading(boolean user);
        void clearErrorMessage();

        void displayInfoMessage(String message);
        void clearInfoMessage();
    }

    public FollowerPresenter(View view, User targetUser, AuthToken authToken) {
        this.view = view;
        this.targetUser = targetUser;
        this.authToken = authToken;
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
            setLoading(true);
            view.setLoading(true);

            getFollowers(authToken, targetUser, PAGE_SIZE, lastFollowee);
        }
    }

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollowee) {
        getFollowersService(this).getFollowers(authToken, targetUser, limit, lastFollowee);
    }

    public FollowersService getFollowersService(FollowersService.GetFollowersObserver observer) {
        return new FollowersService(observer);
    }


    @Override
    public void handleSuccess(List<User> users, boolean hasMorePages, User lastFollowee) {
        setLastFollowee((users.size() > 0) ? users.get(users.size() - 1) : null);
        setHasMorePages(hasMorePages);

        view.setLoading(false);
        view.addItems(users);
        setLoading(false);
    }

    @Override
    public void handleFailure(String message) {
        String errorMessage = "Failed to retrieve followees: " + message;
        Log.e(LOG_TAG, errorMessage);

        view.setLoading(false);
        view.displayErrorMessage(errorMessage);
        setLoading(false);
    }

    @Override
    public void handleException(Exception exception) {
        String errorMessage = "Failed to retrieve followees because of exception: " + exception.getMessage();
        Log.e(LOG_TAG, errorMessage, exception);

        view.setLoading(false);
        view.displayErrorMessage(errorMessage);
        setLoading(false);
    }
}
