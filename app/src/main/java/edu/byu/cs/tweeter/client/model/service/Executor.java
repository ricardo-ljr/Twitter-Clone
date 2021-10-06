package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Executor<T> {
    
    // Public Constructor
    public Executor(T task) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute((Runnable) task);
    }

}
