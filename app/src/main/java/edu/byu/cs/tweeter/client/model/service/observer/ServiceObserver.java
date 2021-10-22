package edu.byu.cs.tweeter.client.model.service.observer;

import edu.byu.cs.tweeter.client.model.service.FeedService;

public interface ServiceObserver {
    void handleFailureObserver(String message);
    void handleExceptionObserver(Exception exception);
}
