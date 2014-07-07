package us.nsakt.dynamicdatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * QueryExecutor to make queries run asyncly with a thread pool
 */
public class QueryExecutor {
    // ExecutorService
    private static ExecutorService executorService;

    /**
     * Gets the executor service
     *
     * @return ExecutorService
     */
    public static ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * Creates a new ExecutorService with a fixed thread pool with thread count of 10 threads
     */
    public static void createExecutorService() {
        executorService = Executors.newFixedThreadPool(10);
    }

    /**
     * Shuts the ExecutorService down
     *
     * @param force forces the shutdown
     */
    public static void destroyExecutorService(boolean force) {
        if (force) {
            executorService.shutdownNow();
            return;
        } else executorService.shutdown();
    }

    /**
     * Class representing a Runnable Query to the Database
     */
    public static abstract class Query implements Runnable {
        /**
         * Executes the query
         */
        public abstract void execute();
    }

    /**
     * Class representing a Runnable Query to Mongo
     */
    public static abstract class MongoQuery extends Query {
        @Override
        public void execute() {
            // If we are on the Main thread, lets send our Runnable onto another thread
            if (!Config.Mongo.usingAsyncMorphia && Thread.currentThread() == DynamicDatabasePlugin.getInstance().getMainThread()) {
                QueryExecutor.getExecutorService().submit(this);
            } else {
                // We are in some non-main thread, it should be okay to execute our Runnable here
                // OR we're using @skipperguy12's AsyncMorphia, where all methods should remain the same but will execute asyncly
                run();
            }
        }
    }
}
