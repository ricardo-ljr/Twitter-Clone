package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.User;

public class MainService {

    // Follow

    public interface FollowObserver {
        void handleSuccessFollow(User user);
        void handleFailureFollow(String message);
        void handleExceptionFollow(Exception e);
        void setFollowButton(boolean enabled);
        void handleUpdateFollowButton(boolean removed);
        void updateSelectedUserFollowingAndFollowers(User user);
    }

    private User targetUser;

    public void follow(FollowObserver observer, User targetUser) {
        this.targetUser = targetUser;
        FollowTask followTask = new FollowTask(Cache.getInstance().getCurrUserAuthToken(),
                targetUser, new FollowHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followTask);
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

    private class FollowHandler extends Handler {
        private FollowObserver observer;

        public FollowHandler(FollowObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(FollowTask.SUCCESS_KEY);
            if (success) {
                observer.updateSelectedUserFollowingAndFollowers(targetUser);
                observer.handleUpdateFollowButton(false);
            } else if (msg.getData().containsKey(FollowTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(FollowTask.MESSAGE_KEY);
                observer.handleFailureFollow(message);
//                Toast.makeText(MainActivity.this, "Failed to follow: " + message, Toast.LENGTH_LONG).show();
            } else if (msg.getData().containsKey(FollowTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(FollowTask.EXCEPTION_KEY);
                observer.handleExceptionFollow(ex);
//                Toast.makeText(MainActivity.this, "Failed to follow because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
            observer.setFollowButton(true);
        }
    }

    // Unfollow

    public interface UnfollowObserver {
        void handleSuccessUnfollow(User user);
        void handleFailureUnfollow(String message);
        void handleExceptionUnfollow(Exception e);
        void setFollowButton(boolean enabled);
        void handleUpdateFollowButton(boolean removed);
        void updateSelectedUserFollowingAndFollowers(User user);
    }

    public void unfollow(UnfollowObserver observer, User targetUser) {
        this.targetUser = targetUser;
        UnfollowTask unfollowTask = new UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                targetUser, new UnfollowHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(unfollowTask);
    }

    // UnfollowHandler

    private class UnfollowHandler extends Handler {

        private UnfollowObserver observer;

        public UnfollowHandler(UnfollowObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(UnfollowTask.SUCCESS_KEY);
            if (success) {
                observer.updateSelectedUserFollowingAndFollowers(targetUser);
                observer.handleUpdateFollowButton(true);
            } else if (msg.getData().containsKey(UnfollowTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(UnfollowTask.MESSAGE_KEY);
                observer.handleFailureUnfollow(message);
//                Toast.makeText(MainActivity.this, "Failed to unfollow: " + message, Toast.LENGTH_LONG).show();
            } else if (msg.getData().containsKey(UnfollowTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(UnfollowTask.EXCEPTION_KEY);
                observer.handleExceptionUnfollow(ex);
//                Toast.makeText(MainActivity.this, "Failed to unfollow because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
            observer.setFollowButton(true);
        }
    }

    // GetFollowersCount

    public interface GetFollowersCountObserver {
        void handleSuccessGetFollowersCount(User user);
        void handleFailureGetFollowersCount(String message);
        void handleExceptionGetFollowersCount(Exception e);
        void setFollowersCount(int count);
    }

    // GetFollowersCountHandler

    private class GetFollowersCountHandler extends Handler {

        private GetFollowersCountObserver observer;

        public GetFollowersCountHandler(GetFollowersCountObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowersCountTask.SUCCESS_KEY);
            if (success) {
                int count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
                observer.setFollowersCount(count);
            } else if (msg.getData().containsKey(GetFollowersCountTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowersCountTask.MESSAGE_KEY);
                observer.handleFailureGetFollowersCount(message);
//                Toast.makeText(MainActivity.this, "Failed to get followers count: " + message, Toast.LENGTH_LONG).show();
            } else if (msg.getData().containsKey(GetFollowersCountTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersCountTask.EXCEPTION_KEY);
                observer.handleExceptionGetFollowersCount(ex);
//                Toast.makeText(MainActivity.this, "Failed to get followers count because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    // GetFollowingCount

    public interface GetFollowingCountObserver {
        void handleSuccessGetFollowingCount(User user);
        void handleFailureGetFollowingCount(String message);
        void handleExceptionGetFollowingCount(Exception e);
        void setFollowingCount(int count);
    }

    // GetFollowingCountHandler

    private class GetFollowingCountHandler extends Handler {

        private GetFollowingCountObserver observer;

        public GetFollowingCountHandler(GetFollowingCountObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowingCountTask.SUCCESS_KEY);
            if (success) {
                int count = msg.getData().getInt(GetFollowingCountTask.COUNT_KEY);
                observer.setFollowingCount(count);
            } else if (msg.getData().containsKey(GetFollowingCountTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowingCountTask.MESSAGE_KEY);
                observer.handleFailureGetFollowingCount(message);
//                Toast.makeText(MainActivity.this, "Failed to get following count: " + message, Toast.LENGTH_LONG).show();
            } else if (msg.getData().containsKey(GetFollowingCountTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowingCountTask.EXCEPTION_KEY);
                observer.handleExceptionGetFollowingCount(ex);
//                Toast.makeText(MainActivity.this, "Failed to get following count because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    // IsFollower

    public interface IsFollowerObserver {
        void handleSuccessIsFollow(User user);
        void handleFailureIsFollow(String message);
        void handleExceptionIsFollow(Exception e);
        void setIsFollowerButton();
        void setIsNotFollowerButton();
    }

    public void isFollower(IsFollowerObserver observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), targetUser, new IsFollowerHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(isFollowerTask);
    }

    // IsFollowerHandler

    private class IsFollowerHandler extends Handler {

        private IsFollowerObserver observer;

        public IsFollowerHandler(IsFollowerObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(IsFollowerTask.SUCCESS_KEY);
            if (success) {
                boolean isFollower = msg.getData().getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);

                // If logged in user if a follower of the selected user, display the follow button as "following"
                if (isFollower) {
//                    followButton.setText(R.string.following);
//                    followButton.setBackgroundColor(getResources().getColor(R.color.white));
//                    followButton.setTextColor(getResources().getColor(R.color.lightGray));
                    observer.setIsFollowerButton();
                } else {
//                    followButton.setText(R.string.follow);
//                    followButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    observer.setIsNotFollowerButton();
                }
            } else if (msg.getData().containsKey(IsFollowerTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(IsFollowerTask.MESSAGE_KEY);
                observer.handleFailureIsFollow(message);
//                Toast.makeText(MainActivity.this, "Failed to determine following relationship: " + message, Toast.LENGTH_LONG).show();
            } else if (msg.getData().containsKey(IsFollowerTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(IsFollowerTask.EXCEPTION_KEY);
                observer.handleExceptionIsFollow(ex);
//                Toast.makeText(MainActivity.this, "Failed to determine following relationship because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
