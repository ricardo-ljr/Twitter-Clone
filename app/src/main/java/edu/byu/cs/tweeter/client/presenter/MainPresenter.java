package edu.byu.cs.tweeter.client.presenter;

import java.net.MalformedURLException;
import java.text.ParseException;

import edu.byu.cs.tweeter.client.model.service.PostStatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;

public class MainPresenter implements UserService.LogoutObserver, PostStatusService.PostStatusObserver {

    @Override
    public void handleSucessLogout() {
        view.logout();
    }

    @Override
    public void handleFailureLogout(String message) {
        view.displayErrorMessage("Failed to logout: " + message);
    }

    @Override
    public void handleExceptionLogout(Exception e) {
        view.displayErrorMessage("Failed to logout because of exception: " + e.getMessage());
    }

    private MainPresenter.View view;

    public MainPresenter(MainPresenter.View view) {
        this.view = view;
    }

    @Override
    public void handleSuccessPostStatus(String message) {
        view.displayInfoMessage(message);
    }

    @Override
    public void handleFailurePostStatus(String message) {
        view.displayErrorMessage("Failed to post status: " + message);
    }

    @Override
    public void handleExceptionPostStatus(Exception e) {
        view.displayErrorMessage("Failed to post status because of exception: " + e.getMessage());
    }

    public interface View {
        void displayErrorMessage(String message);
        void displayInfoMessage(String message);

        void logout();

        void updateFollowButton(boolean removed);
        void setFollowButton(boolean enabled);

        void setFollowerCount(int count);
        void setFollowingCount(int count);

        void setIsFollowerButton();
        void setIsNotFollowerButton();
    }

    public void logout() {
        view.displayInfoMessage("Logging Out...");
        new UserService().logout(this);
    }

    public void postStatus(String post) throws ParseException, MalformedURLException {
        view.displayInfoMessage("Posting Status...");
        new PostStatusService().postStatus(post, this);
    }

}
