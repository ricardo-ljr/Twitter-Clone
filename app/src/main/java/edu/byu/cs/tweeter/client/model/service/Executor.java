package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Executor<T> {
    
    // Public Constructor
    public Executor(T task) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute((Runnable) task);
    }

    public Executor(T taskOne, T taskTwo) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute((Runnable) taskOne);
        executor.execute((Runnable) taskTwo);
    }

}
