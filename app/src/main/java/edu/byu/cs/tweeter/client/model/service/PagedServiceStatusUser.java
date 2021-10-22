package edu.byu.cs.tweeter.client.model.service;

import android.os.Message;

import java.net.MalformedURLException;
import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserverStatus;
import edu.byu.cs.tweeter.model.domain.Status;

public abstract class PagedServiceStatusUser<T extends ServiceObserver & ServiceObserverStatus, U> extends BackgroundTaskHandler<T> {

    public PagedServiceStatusUser(T observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(T observer, Message msg) throws MalformedURLException {
        List<U> last = (List<U>) msg.getData().getSerializable(BackgroundTaskHandler.ITEMS_KEY);
        boolean hasMorePages = msg.getData().getBoolean(BackgroundTaskHandler.MORE_PAGES_KEY);
        U lastItem = (last.size() > 0) ? last.get(last.size() - 1) : null;
        observer.handleSuccessStatus(last, hasMorePages, lastItem);
    }

}
