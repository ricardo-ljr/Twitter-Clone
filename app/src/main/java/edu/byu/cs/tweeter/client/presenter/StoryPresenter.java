package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StoryService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter implements StoryService.GetStoryObserver, UserService.GetUserObserver {

    @Override
    public void handleSuccessStory(List<Status> statuses, boolean hasMorePages, Status lastStatus) {
        this.hasMorePages = hasMorePages;
        view.setLoading(false);
        view.addItems(statuses);
        this.lastStatus = lastStatus;
        isLoading = false;
    }

    @Override
    public void handleFailure(String message) {
        view.displayErrorMessage("Failed to get story: " + message);
    }

    @Override
    public void handleException(Exception e) {
        view.displayErrorMessage("Failed to get story because of exception: " + e.getMessage());
    }

    @Override
    public void handleSuccessUser(User user) {
        view.navigateToUser(user);
    }

    private StoryPresenter.View view;
    private boolean isLoading = false;
    private boolean hasMorePages = true;
    private User user;
    private Status lastStatus = null;

    public interface View extends ServiceView{
        void navigateToUser(User user);
        void setLoading(boolean value);
        void addItems(List<Status> statuses);
    }

    public StoryPresenter(View view, User user) {
        this.view = view;
        this.user = user;
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            isLoading = true;
            view.setLoading(true);

            new StoryService().getStory(this, user, lastStatus);
        }
    }

    public void getUsers(String alias) {
        UserService.getUsers(Cache.getInstance().getCurrUserAuthToken(), alias, this);
    }
}
