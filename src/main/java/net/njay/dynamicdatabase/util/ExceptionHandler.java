package net.njay.dynamicdatabase.util;

public final class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("Handled by global Exception handler");
        e.printStackTrace();
    }

}
