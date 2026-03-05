package app.services;

import app.exceptions.ThreadServiceException;

import java.util.concurrent.*;

public class ThreadService {

    private final ExecutorService executor;

    public ThreadService(int poolSize) {
        this.executor = Executors.newFixedThreadPool(poolSize);
    }

    public void runAsync(Runnable task) {
        try {
            executor.submit(wrapRunnable(task));
        } catch (Exception e) {
            throw new ThreadServiceException("Error submitting runnable task", e);
        }
    }

    public <T> Future<T> callAsync(Callable<T> task) {
        try {
            return executor.submit(wrapCallable(task));
        } catch (Exception e) {
            throw new ThreadServiceException("Error submitting callable task", e);
        }
    }

    public <T> CompletableFuture<T> callAsyncCompletable(Callable<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new ThreadServiceException("Error in CompletableFuture task", e);
            }
        }, executor);
    }

    private Runnable wrapRunnable(Runnable task) {
        return () -> {
            try {
                task.run();
            } catch (Exception e) {
                throw new ThreadServiceException("Runnable execution failed", e);
            }
        };
    }

    private <T> Callable<T> wrapCallable(Callable<T> task) {
        return () -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new ThreadServiceException("Callable execution failed", e);
            }
        };
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            throw new ThreadServiceException("ThreadService shutdown interrupted", e);
        }
    }
}