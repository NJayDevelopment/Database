package net.njay.dynamicdatabase.util;

/**
 * Exception Handler for our multi-threaded architecture.
 *
 * @author Austin Mayes
 */
public final class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
    }

}
