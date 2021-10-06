package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedService {

    private static final int PAGE_SIZE = 10;

    public interface GetFeedObserver extends ServiceObserver {
        void handleSuccessFeed(List<Status> statuses, boolean hasMorePages, Status lastStatus) throws MalformedURLException;
    }

    public static void getFeed(GetFeedObserver observer, User user, Status lastStatus) {
        GetFeedTask getFeedTask = new GetFeedTask(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, lastStatus, new GetFeedHandler(observer));
        new Executor<>(getFeedTask);
    }

    /**
     * Message handler (i.e., observer) for GetFeedTask.
     */
    private static class GetFeedHandler extends BackgroundTaskHandler {

        private GetFeedObserver observer;

        public GetFeedHandler(GetFeedObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        protected String getFailedMessagePrefix() {
            return "Feed Service";
        }

        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Message msg) {
            List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetFeedTask.ITEMS_KEY);
            boolean hasMorePages = msg.getData().getBoolean(GetFeedTask.MORE_PAGES_KEY);
            Status lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;

            try {
                this.observer.handleSuccessFeed(statuses, hasMorePages, lastStatus);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                observer.handleException(e);
            }
        }
    }
}
