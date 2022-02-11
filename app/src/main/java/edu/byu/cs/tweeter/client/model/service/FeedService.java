package edu.byu.cs.tweeter.client.model.service;

import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserverStatus;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedService{

    private static final int PAGE_SIZE = 10;

    private final GetFeedObserver observer;

    public FeedService(GetFeedObserver observer) {
        // An assertion would be better, but Android doesn't support Java assertions
        if(observer == null) {
            throw new NullPointerException();
        }

        this.observer = observer;
    }

    public interface GetFeedObserver extends ServiceObserverStatus<Status> {}

    public static void getFeed(GetFeedObserver observer, User user, Status lastStatus) {
        GetFeedTask getFeedTask = new GetFeedTask(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, lastStatus, new GetFeedHandler(Looper.getMainLooper(), observer));
        new Executor<>(getFeedTask);
    }

    /**
     * Message handler (i.e., observer) for GetFeedTask.
     */
    private static class GetFeedHandler extends PagedServiceStatusUser {

        private GetFeedObserver observer;

        public GetFeedHandler(Looper looper, GetFeedObserver observer) {
            super(looper,(ServiceObserver) observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return "Feed Service";
        }

    }
}