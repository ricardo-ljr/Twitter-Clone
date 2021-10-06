package edu.byu.cs.tweeter.client.model.service;


import android.os.Message;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerService {

    private static final int PAGE_SIZE = 10;

    private final GetFollowersObserver observer;

    public interface GetFollowersObserver extends ServiceObserver {
        void handleSuccessFollower(List<User> users, boolean hasMorePages, User lastFollower);
    }

    public FollowerService(GetFollowersObserver observer) {
        // An assertion would be better, but Android doesn't support Java assertions
        if(observer == null) {
            throw new NullPointerException();
        }

        this.observer = observer;
    }

    public void getFollowers(AuthToken authToken, User targetUser, User lastFollowee) {
        GetFollowersTask getFollowersTask = getGetFollowerTask(authToken, targetUser, PAGE_SIZE, lastFollowee);
        new Executor<>(getFollowersTask);
    }

    public GetFollowersTask getGetFollowerTask(AuthToken authToken, User targetUser, int limit, User lastFollowee) {
        return new GetFollowersTask(authToken, targetUser, limit, lastFollowee,
                new GetFollowersHandler(observer));
    }

    /**
     * Message handler (i.e., observer) for GetFollowersTask.
     */
    private class GetFollowersHandler extends BackgroundTaskHandler {

        private GetFollowersObserver observer;

        public GetFollowersHandler(GetFollowersObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        protected String getFailedMessagePrefix() {
            return "Follower Service";
        }

        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Message msg) {

            List<User> followers = (List<User>) msg.getData().getSerializable(GetFollowersTask.ITEMS_KEY);
            boolean hasMorePages = msg.getData().getBoolean(GetFollowersTask.MORE_PAGES_KEY);
            User lastFollower = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;

            this.observer.handleSuccessFollower(followers, hasMorePages, lastFollower);

        }

    }

}
