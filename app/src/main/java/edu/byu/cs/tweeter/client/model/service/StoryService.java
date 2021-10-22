package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserverStatus;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryService {

    private static final int PAGE_SIZE = 10;

    public interface GetStoryObserver extends ServiceObserverStatus<Status> {}

    public static void getStory(GetStoryObserver observer, User user, Status lastStatus) {
        GetStoryTask getStoryTask = new GetStoryTask(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, lastStatus, new GetStoryHandler(observer));
        new Executor<>(getStoryTask);
    }

    /**
     * Message handler (i.e., observer) for GetStoryTask.
     */
    private static class GetStoryHandler extends PagedServiceStatusUser {

        private GetStoryObserver observer;

        public GetStoryHandler(GetStoryObserver observer) {
            super((ServiceObserver) observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return "Story Service";
        }
    }
}
