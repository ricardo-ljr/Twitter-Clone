package edu.byu.cs.tweeter.client.model.service.observer;

import java.net.MalformedURLException;
import java.util.List;

public interface ServiceObserverStatus<T> {
    void handleSuccessStatus(List<T> statuses, boolean hasMorePages, T lastStatus) throws MalformedURLException;
}
