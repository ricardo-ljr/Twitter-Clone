package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersService {

    private final GetFollowersObserver observer;

    /**
     * An observer interface to be implemented by observers who want to be notified when
     * asynchronous operations complete.
     */
    public interface GetFollowersObserver {
        void handleSuccess(List<User> users, boolean hasMorePages, User lastFolowee);
        void handleFailure(String message);
        void handleException(Exception exception);
    }

    public FollowersService(GetFollowersObserver observer) {
        // An assertion would be better, but Android doesn't support Java assertions
        if(observer == null) {
            throw new NullPointerException();
        }

        this.observer = observer;
    }

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollowee) {
        GetFollowersTask getFollowersTask = getGetFollowersTask(authToken, targetUser, limit, lastFollowee);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowersTask);
    }

    public GetFollowersTask getGetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollowee) {
        return new GetFollowersTask(authToken, targetUser, limit, lastFollowee,
                new GetFollowersHandler(Looper.getMainLooper(), observer));
    }


    /**
     * Message handler (i.e., observer) for GetFollowersTask.
     */
    public static class GetFollowersHandler extends Handler {

        private final GetFollowersObserver observer;

        public GetFollowersHandler(Looper looper, GetFollowersObserver observer) {
            super(looper);
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message message) {
            Bundle bundle = message.getData();
            boolean success = message.getData().getBoolean(GetFollowersTask.SUCCESS_KEY);
            if (success) {
                List<User> followers = (List<User>) message.getData().getSerializable(GetFollowersTask.FOLLOWERS_KEY);
                boolean hasMorePages = bundle.getBoolean(GetFollowersTask.MORE_PAGES_KEY);
                User lastFollowee = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;
                observer.handleSuccess(followers, hasMorePages, lastFollowee);
            } else if (message.getData().containsKey(GetFollowersTask.MESSAGE_KEY)) {
                String errorMessage = bundle.getString(GetFollowersTask.MESSAGE_KEY);
                observer.handleFailure(errorMessage);
            } else if (bundle.containsKey(GetFollowersTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) bundle.getSerializable(GetFollowersTask.EXCEPTION_KEY);
                observer.handleException(ex);
            }
        }
    }

}
