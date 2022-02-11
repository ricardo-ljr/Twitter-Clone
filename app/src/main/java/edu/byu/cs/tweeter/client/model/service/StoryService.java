package edu.byu.cs.tweeter.client.model.service;

import android.os.Looper;

import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserverStatus;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryService {

    private static final int PAGE_SIZE = 10;

    private GetStoryObserver observer;

    public StoryService(GetStoryObserver observer) {
        if(observer == null) {
            throw new NullPointerException();
        }

        this.observer = observer;
    }

    public interface GetStoryObserver extends ServiceObserverStatus<Status>, ServiceObserver {}

    public void getStory(GetStoryObserver observer, User user, Status lastStatus) {
        GetStoryTask getStoryTask = getGetStoryTask(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastStatus);
        new Executor<>(getStoryTask);
    }

    public GetStoryTask getGetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus) {
        return new GetStoryTask(authToken,
                targetUser, limit, lastStatus, new GetStoryHandler(Looper.getMainLooper(), observer));
    }

    /**
     * Message handler (i.e., observer) for GetStoryTask.
     */
    public static class GetStoryHandler extends PagedServiceStatusUser<GetStoryObserver, Status> {

        private GetStoryObserver observer;

        public GetStoryHandler(Looper looper, GetStoryObserver observer) {
            super(looper, observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return "Story Service";
        }
    }
}
