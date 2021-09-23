package edu.byu.cs.tweeter.client.model.service;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.client.view.main.following.FollowingFragment;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Contains the business logic to support the login operation.
 */
public class UserService {

    /**
     * An observer interface to be implemented by observers who want to be notified when
     * asynchronous operations complete.
     */
    public interface GetUserObserver {
        void handleSuccess(User user);
        void handleFailure(String message);
        void handleException(Exception exception);
    }


//    /**
//     * Makes an asynchronous login request.
//     *
//     * @param username the user's name.
//     * @param password the user's password.
//     */
//    public void login(String username, String password) {
//        LoginTask loginTask = getLoginTask(username, password);
//        BackgroundTaskUtils.runTask(loginTask);
//    }

    public void getUser(AuthToken authToken, String alias, GetUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(authToken,
                alias, new GetUserHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getUserTask);
//        Toast.makeText(getContext(), "Getting user's profile...", Toast.LENGTH_LONG).show();
    }

//    /**
//     * Returns an instance of {@link LoginTask}. Allows mocking of the LoginTask class for
//     * testing purposes. All usages of LoginTask should get their instance from this method to
//     * allow for proper mocking.
//     *
//     * @return the instance.
//     */
//    LoginTask getLoginTask(String username, String password) {
//        return new LoginTask(username, password, new MessageHandler(Looper.getMainLooper(), observer));
//    }

    /**
     * Message handler (i.e., observer) for GetUserTask.
     */
    private class GetUserHandler extends Handler {

        private GetUserObserver observer;

        public GetUserHandler(GetUserObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetUserTask.SUCCESS_KEY);
            if (success) {
                User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
                observer.handleSuccess(user);
            } else if (msg.getData().containsKey(GetUserTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetUserTask.MESSAGE_KEY);
//                Toast.makeText(getContext(), "Failed to get user's profile: " + message, Toast.LENGTH_LONG).show();
                observer.handleFailure(message);
            } else if (msg.getData().containsKey(GetUserTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetUserTask.EXCEPTION_KEY);
//                Toast.makeText(getContext(), "Failed to get user's profile because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                observer.handleException(ex);
            }
        }
    }

//    /**
//     * Background task that logs in a user (i.e., starts a session).
//     */
//    private static class LoginTask extends BackgroundTask {
//
//        private static final String LOG_TAG = "LoginTask";
//
//        public static final String USER_KEY = "user";
//        public static final String AUTH_TOKEN_KEY = "auth-token";
//
//        /**
//         * The user's username (or "alias" or "handle"). E.g., "@susan".
//         */
//        private String username;
//        /**
//         * The user's password.
//         */
//        private String password;
//
//        public LoginTask(String username, String password, Handler messageHandler) {
//            super(messageHandler);
//
//            this.username = username;
//            this.password = password;
//        }
//
//        @Override
//        protected void runTask() {
//            try {
//                Pair<User, AuthToken> loginResult = doLogin();
//
//                User loggedInUser = loginResult.getFirst();
//                AuthToken authToken = loginResult.getSecond();
//
//                BackgroundTaskUtils.loadImage(loggedInUser);
//
//                sendSuccessMessage(loggedInUser, authToken);
//
//            } catch (Exception ex) {
//                Log.e(LOG_TAG, ex.getMessage(), ex);
//                sendExceptionMessage(ex);
//            }
//        }
//
//        // This method is public so it can be accessed by test cases
//        public FakeData getFakeData() {
//            return new FakeData();
//        }
//
//        // This method is public so it can be accessed by test cases
//        public Pair<User, AuthToken> doLogin() {
//            User loggedInUser = getFakeData().getFirstUser();
//            AuthToken authToken = getFakeData().getAuthToken();
//            return new Pair<>(loggedInUser, authToken);
//        }
//
//        private void sendSuccessMessage(User loggedInUser, AuthToken authToken) {
//            sendSuccessMessage(new BundleLoader() {
//                @Override
//                public void load(Bundle msgBundle) {
//                    msgBundle.putSerializable(USER_KEY, loggedInUser);
//                    msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);
//                }
//            });
//        }
//    }
}