package fi.dy.masa.malilib.interfaces;

public interface ICompletionListener
{
    /**
     * Called when a task wants to inform a listener about the task being completed
     */
    void onTaskCompleted();

    /**
     * Called when a task wants to inform a listener about the task being aborted before completion
     */
    default void onTaskAborted() {}
}
