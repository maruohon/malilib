package fi.dy.masa.malilib.listener;

public interface ConfirmationListener
{
    /**
     * Called when a task requiring confirmation is confirmed by the user
     * @return
     */
    boolean onActionConfirmed();

    /**
     * Called when a task requiring confirmation is cancelled by the user
     * @return
     */
    default boolean onActionCancelled()
    {
        return false;
    }
}
