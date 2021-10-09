package edu.byu.cs.tweeter.client.presenter;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StoryService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenterStatus implements StoryService.GetStoryObserver, UserService.GetUserObserver {

    @Override
    public void handleSuccessStory(List<Status> statuses, boolean hasMorePages, Status lastStatus) throws MalformedURLException {
        handleSuccess(statuses, hasMorePages, lastStatus);
    }

    public interface View extends PagedView<Status>{}

    public StoryPresenter(View view, User user) {
        super(view, user);
    }

    public void loadMoreItems() throws MalformedURLException {
        if (loadItems()) {
            new StoryService().getStory(this, getUser(), getLastStatus());
        }
    }

    public void getUsers(String alias) {
        UserService.getUsers(Cache.getInstance().getCurrUserAuthToken(), alias, this);
    }
}
