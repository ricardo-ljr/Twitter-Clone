package edu.byu.cs.tweeter.client.model.service;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;

/**
 * Contains the business logic to support the login operation.
 */
public class UserService {

    /**
     * An observer interface to be implemented by observers who want to be notified when
     * asynchronous operations complete.
     */
    public interface GetUserObserver extends ServiceObserver {
        void handleSuccessUser(User user);
    }

    public static void getUsers(AuthToken authToken, String alias, GetUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(authToken,
                alias, new GetUserHandler(observer));
        new Executor<>(getUserTask);
    }

    /**
     * Message handler (i.e., observer) for GetUserTask.
     */
    private static class GetUserHandler extends BackgroundTaskHandler {

        private GetUserObserver observer;

        public GetUserHandler(GetUserObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        protected String getFailedMessagePrefix() {
            return "User Service";
        }

        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Message msg) {
            User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
            this.observer.handleSuccessUser(user);
        }
    }

    public interface RegisterObserver extends ServiceObserver {
        void handleSuccess(User user, AuthToken authToken);
    }

    // RegisterHandler

    private class RegisterHandler extends BackgroundTaskHandler {

        private UserService.RegisterObserver observer;

        public RegisterHandler(UserService.RegisterObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        protected String getFailedMessagePrefix() {
            return "Register Service";
        }

        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Message msg) {
            User registeredUser = (User) msg.getData().getSerializable(RegisterTask.USER_KEY);
            AuthToken authToken = (AuthToken) msg.getData().getSerializable(RegisterTask.AUTH_TOKEN_KEY);

            Cache.getInstance().setCurrUser(registeredUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            try {
                this.observer.handleSuccess(registeredUser, authToken);
            } catch (Exception e) {
                observer.handleExceptionObserver(e);
            }
        }
    }

    public void register(String alias, String password, String firstName, String lastName, ImageView imageToUpload, UserService.RegisterObserver observer) {
        // Convert image to byte array.
        Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();
        String imageBytesBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        // Send register request.
        RegisterTask registerTask = new RegisterTask(firstName, lastName, alias, password, imageBytesBase64, new RegisterHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(registerTask);
    }

    // Login
    public interface LoginObserver extends ServiceObserver {
        void handleSuccess(User user, AuthToken authToken);
    }

    public void login(String alias, String password, LoginObserver observer) {
        LoginTask loginTask = new LoginTask(alias, password, new LoginHandler(observer));
        new Executor<>(loginTask);
    }

    /**
     * Message handler (i.e., observer) for LoginTask
     */
    private class LoginHandler extends BackgroundTaskHandler {

        private LoginObserver observer;

        public LoginHandler(LoginObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        protected String getFailedMessagePrefix() {
            return "Login Service";
        }

        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Message msg) {
            User loggedInUser = (User) msg.getData().getSerializable(LoginTask.USER_KEY);
            AuthToken authToken = (AuthToken) msg.getData().getSerializable(LoginTask.AUTH_TOKEN_KEY);

            Cache.getInstance().setCurrUser(loggedInUser); // TODO: Do I still need this?
            Cache.getInstance().setCurrUserAuthToken(authToken);

            this.observer.handleSuccess(loggedInUser, authToken);
        }
    }

    // Logout
    public interface LogoutObserver extends ServiceObserver{
        void handleSucessLogout();
    }

    public void logout(UserService.LogoutObserver observer) {
        LogoutTask logoutTask = new LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new UserService.LogoutHandler(observer));
        new Executor<>(logoutTask);
    }

    // LogoutHandler

    private class LogoutHandler extends BackgroundTaskHandler {
        private UserService.LogoutObserver observer;

        public LogoutHandler(UserService.LogoutObserver observer) {
            super(observer);
            this.observer = observer;
        }

        @Override
        protected String getFailedMessagePrefix() {
            return "Logout Service";
        }

        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Message msg) {
            this.observer.handleSucessLogout();
        }
    }
}