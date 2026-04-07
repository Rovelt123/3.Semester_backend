package app.services;

import app.exceptions.ThreadServiceException;

import java.util.concurrent.*;

public class ThreadService {

    private final ExecutorService executor;

    // ________________________________________________________

    public ThreadService(int poolSize) {
        this.executor = Executors.newFixedThreadPool(poolSize);
    }

    // ________________________________________________________

    public void runAsync(Runnable task) {
        try {
            executor.submit(task);
        } catch (Exception e) {
            throw new ThreadServiceException("Error submitting runnable task", e);
        }
    }

    // ________________________________________________________

    public <T> Future<T> callAsync(Callable<T> task) {
        try {
            return executor.submit(task);
        } catch (Exception e) {
            throw new ThreadServiceException("Error submitting callable task", e);
        }
    }

    // ________________________________________________________

    public <T> CompletableFuture<T> callAsyncCompletable(Callable<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new ThreadServiceException("Error in CompletableFuture task", e);
            }
        }, executor);
    }

    // ________________________________________________________

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