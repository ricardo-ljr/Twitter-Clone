package edu.byu.cs.tweeter.client.presenter;

import java.net.MalformedURLException;
import java.text.ParseException;

import edu.byu.cs.tweeter.client.model.service.MainService;
import edu.byu.cs.tweeter.client.model.service.PostStatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter implements UserService.LogoutObserver, PostStatusService.PostStatusObserver, MainService.FollowObserver, MainService.UnfollowObserver,
MainService.GetFollowersCountObserver, MainService.GetFollowingCountObserver, MainService.IsFollowerObserver{

    private MainPresenter.View view;

    public MainPresenter(MainPresenter.View view) {
        this.view = view;
    }

    @Override
    public void handleFailure(String message) {
        view.displayErrorMessage("Failed to: " + message);
    }

    @Override
    public void handleException(Exception exception) {
        view.displayInfoMessage("Failed because of exception: " + exception.getMessage());
    }

    public interface View extends ServiceView{

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

    public void isFollower() {
        new MainService().isFollower(this);
    }

    @Override
    public void handleSucessLogout() {
        view.logout();
    }

    @Override
    public void handleSuccessPostStatus(String message) {
        view.displayInfoMessage(message);
    }

    @Override
    public void handleSuccessFollow(User user) {
        view.displayInfoMessage("Adding " + user.getName() + "...");
        new MainService().follow(this, user);
    }

    @Override
    public void handleSuccessUnfollow(User user) {
        view.displayErrorMessage("Unfollowing " + user.getName() + "...");
        new MainService().unfollow(this, user);
    }

    @Override
    public void setFollowButton(boolean enabled) {
        view.setFollowButton(enabled);
    }

    @Override
    public void handleUpdateFollowButton(boolean removed) {
        view.updateFollowButton(removed);
    }

    @Override
    public void updateSelectedUserFollowingAndFollowers(User user) {
        new MainService().updateSelectedUserFollowingAndFollowers(this, this, user);
    }

    @Override
    public void handleSuccessGetFollowersCount(User user) {

    }

    @Override
    public void setFollowersCount(int count) {
        view.setFollowerCount(count);
    }

    @Override
    public void handleSuccessGetFollowingCount(User user) {

    }

    @Override
    public void setFollowingCount(int count) {
        view.setFollowingCount(count);
    }

    @Override
    public void handleSuccessIsFollow(User user) {

    }

    @Override
    public void setIsFollowerButton() {
        view.setIsFollowerButton();
    }

    @Override
    public void setIsNotFollowerButton() {
        view.setIsNotFollowerButton();
    }

}
