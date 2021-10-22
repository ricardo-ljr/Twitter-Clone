package edu.byu.cs.tweeter.client.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.net.MalformedURLException;

import edu.byu.cs.tweeter.client.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;

public abstract class BackgroundTaskHandler<T extends ServiceObserver> extends Handler {

    private final T observer;

    public static final String MORE_PAGES_KEY = "more-pages";
    public static final String ITEMS_KEY = "items";

    public BackgroundTaskHandler(T observer) {
        this.observer = observer;
    }

    public BackgroundTaskHandler(Looper looper, T observer) {
        super(looper);
        this.observer = observer;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(BackgroundTask.SUCCESS_KEY);
        if (success) {
            try {
                handleSuccessMessage(observer, msg);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else if (msg.getData().containsKey(BackgroundTask.MESSAGE_KEY)) {
            String message = getFailedMessagePrefix() + ": " + msg.getData().getString(GetFollowersCountTask.MESSAGE_KEY);
            observer.handleFailureObserver(message);
        } else if (msg.getData().containsKey(BackgroundTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(BackgroundTask.EXCEPTION_KEY);
            String message = getFailedMessagePrefix() + " because of exception: " + ex.getMessage();
            observer.handleFailureObserver(message);
        }
    }

    protected abstract String getFailedMessagePrefix();

    // Generic Observer handleSuccessMessage
    protected abstract void handleSuccessMessage(T observer, Message msg) throws MalformedURLException;
}
