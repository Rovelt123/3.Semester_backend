package app.services;

import app.exceptions.ThreadServiceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class ThreadServiceTest {

    private ThreadService threadService = new ThreadService(4);

    // ________________________________________________________

    @AfterEach
    void tearDown() {
        threadService.shutdown();
    }

    // ________________________________________________________

    @Test
    void runAsync() throws InterruptedException {
        final boolean[] ran = {false};

        threadService.runAsync(() ->{
            ran[0] = true;
        });

        Thread.sleep(100);

        assertTrue(ran[0], "Runnable should have executed");
    }

    // ________________________________________________________

    @Test
    void callAsyncInt() throws Exception {
        Future<Integer> future = threadService.callAsync(() ->{
            int test = 5;
            int test2 = 1;
            return test + test2;
        });

        int result = future.get();
        assertEquals(6, result);
    }

    // ________________________________________________________

    @Test
    void callAsyncString() throws Exception {
        Future<String> future = threadService.callAsync(() ->{
            String test = "Denne";
            String test2 = " test virker!";
            return test + test2;
        });

        String result = future.get();
        assertEquals("Denne test virker!", result);
    }

    // ________________________________________________________

    @Test
    void callAsyncCompletable() throws Exception {
        CompletableFuture<String> future = threadService.callAsyncCompletable(() -> "Async Result");

        String result = future.get();
        assertEquals("Async Result", result);
    }

    // ________________________________________________________

    @Test
    void shutdown() {
        assertDoesNotThrow(() -> threadService.shutdown());
    }

    // ________________________________________________________

    @Test
    void runAsyncException() throws InterruptedException {
        final boolean[] ran = {false};

        threadService.runAsync(() -> {
            ran[0] = true;
            throw new RuntimeException("Fail!");
        });

        Thread.sleep(200);

        assertTrue(ran[0]);
    }

    // ________________________________________________________

    @Test
    void callAsyncException() {
        Future<String> future = threadService.callAsync(() -> {
            throw new RuntimeException("Callable fail");
        });

        ExecutionException ex = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(ThreadServiceException.class, ex.getCause());
    }
}