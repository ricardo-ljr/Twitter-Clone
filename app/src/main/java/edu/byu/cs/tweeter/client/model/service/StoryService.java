package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryService {

    private static final int PAGE_SIZE = 10;

    public interface GetStoryObserver extends ServiceObserver {
        void handleSuccessStory(List<Status> statuses, boolean hasMorePages, Status lastStatus);
    }

    public static void getStory(GetStoryObserver observer, User user, Status lastStatus) {
        GetStoryTask getStoryTask = new GetStoryTask(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, lastStatus, new GetStoryHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getStoryTask);
    }

    /**
     * Message handler (i.e., observer) for GetStoryTask.
     */
    private static class GetStoryHandler extends BackgroundTaskHandler {

        private GetStoryObserver observer;

        public GetStoryHandler(GetStoryObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        protected String getFailedMessagePrefix() {
            return "Story Service";
        }

        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Message msg) {
            List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetStoryTask.ITEMS_KEY);
            boolean hasMorePages = msg.getData().getBoolean(GetStoryTask.MORE_PAGES_KEY);

            Status lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;

            this.observer.handleSuccessStory(statuses, hasMorePages, lastStatus);
        }
    }
}
