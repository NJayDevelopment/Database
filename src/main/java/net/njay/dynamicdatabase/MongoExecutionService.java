package net.njay.dynamicdatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * An ExecutorService manager for mongo operations.
 *
 * @author skipperguy12
 */
public class MongoExecutionService {
    private static ExecutorService executorService;

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static void createExecutorService() {
        executorService = Executors.newFixedThreadPool(10);
    }

    public static void destroyExecutorService(boolean force) {
        if (force) {
            executorService.shutdownNow();
            return;
        } else executorService.shutdown();
    }
}
