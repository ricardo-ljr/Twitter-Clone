package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class MainService {

    // Follow

    public interface FollowObserver extends ServiceObserver {
        void handleSuccessFollow(User user);
        void setFollowButton(boolean enabled);
        void handleUpdateFollowButton(boolean removed);
        void updateSelectedUserFollowingAndFollowers(User user);
    }

    private User targetUser;

    public void follow(FollowObserver observer, User targetUser) {
        this.targetUser = targetUser;
        FollowTask followTask = new FollowTask(Cache.getInstance().getCurrUserAuthToken(),
                targetUser, new FollowHandler(observer));
        new Executor<>(followTask);
    }

    public void updateSelectedUserFollowingAndFollowers(GetFollowersCountObserver followersCountObserver, GetFollowingCountObserver followingCountObserver, User targetUser) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        // Get count of most recently selected user's followers.
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
                targetUser, new GetFollowersCountHandler(followersCountObserver));
        executor.execute(followersCountTask);

        // Get count of most recently selected user's followees (who they are following)
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(),
                targetUser, new GetFollowingCountHandler(followingCountObserver));
        executor.execute(followingCountTask);
    }

    // FollowHandler

    private class FollowHandler extends BackgroundTaskHandler {
        private FollowObserver observer;

        public FollowHandler(FollowObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        protected String getFailedMessagePrefix() {
            return "Follow Service";
        }

        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Message msg) {
            this.observer.updateSelectedUserFollowingAndFollowers(targetUser);
            this.observer.handleUpdateFollowButton(false);

            this.observer.setFollowButton(true);
        }
    }

    // Unfollow

    public interface UnfollowObserver extends ServiceObserver {
        void handleSuccessUnfollow(User user);
        void setFollowButton(boolean enabled);
        void handleUpdateFollowButton(boolean removed);
        void updateSelectedUserFollowingAndFollowers(User user);
    }

    public void unfollow(UnfollowObserver observer, User targetUser) {
        this.targetUser = targetUser;
        UnfollowTask unfollowTask = new UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                targetUser, new UnfollowHandler(observer));
        new Executor<>(unfollowTask);
    }

    // UnfollowHandler

    private class UnfollowHandler extends BackgroundTaskHandler {

        private UnfollowObserver observer;

        public UnfollowHandler(UnfollowObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        protected String getFailedMessagePrefix() {
            return "Unfollow Service";
        }

        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Message msg) {
            this.observer.updateSelectedUserFollowingAndFollowers(targetUser);
            this.observer.handleUpdateFollowButton(true);

            this.observer.setFollowButton(true);
        }
    }

    // GetFollowersCount

    public interface GetFollowersCountObserver extends ServiceObserver {
        void handleSuccessGetFollowersCount(User user);
        void setFollowersCount(int count);
    }

    // GetFollowersCountHandler

    private class GetFollowersCountHandler extends BackgroundTaskHandler {

        private GetFollowersCountObserver observer;

        public GetFollowersCountHandler(GetFollowersCountObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        protected String getFailedMessagePrefix() {
            return "GetFollowersCount Service";
        }

        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Message msg) {
            int count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
            this.observer.setFollowersCount(count);
        }
    }

    // GetFollowingCount

    public interface GetFollowingCountObserver extends ServiceObserver {
        void handleSuccessGetFollowingCount(User user);
        void setFollowingCount(int count);
    }

    // GetFollowingCountHandler

    private class GetFollowingCountHandler extends BackgroundTaskHandler {

        private GetFollowingCountObserver observer;

        public GetFollowingCountHandler(GetFollowingCountObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        protected String getFailedMessagePrefix() {
            return "GetFollowingCount Service";
        }

        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Message msg) {
            int count = msg.getData().getInt(GetFollowingCountTask.COUNT_KEY);
            this.observer.setFollowingCount(count);
        }
    }

    // IsFollower

    public interface IsFollowerObserver extends ServiceObserver {
        void handleSuccessIsFollow(User user);
        void setIsFollowerButton();
        void setIsNotFollowerButton();
    }

    public void isFollower(IsFollowerObserver observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), targetUser, new IsFollowerHandler(observer));
        new Executor<>(isFollowerTask);
    }

    // IsFollowerHandler

    private class IsFollowerHandler extends BackgroundTaskHandler {

        private IsFollowerObserver observer;

        public IsFollowerHandler(IsFollowerObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        protected String getFailedMessagePrefix() {
            return null;
        }

        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Message msg) {
            boolean isFollower = msg.getData().getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);

            if (isFollower) {
                this.observer.setIsFollowerButton();
            } else {
                this.observer.setIsNotFollowerButton();
            }
        }
    }
}
