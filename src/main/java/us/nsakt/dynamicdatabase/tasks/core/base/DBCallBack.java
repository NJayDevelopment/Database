package us.nsakt.dynamicdatabase.tasks.core.base;

public interface DBCallback {

    public void call();

    public void call(Object... objects);
}
