package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;

public class MainPresenter implements UserService.LogoutObserver {

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

}
