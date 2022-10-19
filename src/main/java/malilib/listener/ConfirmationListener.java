package malilib.listener;

public interface ConfirmationListener
{
    /**
     * Called when a task requiring confirmation is confirmed by the user
     * @return true on success, false on failure
     */
    boolean onActionConfirmed();

    /**
     * Called when a task requiring confirmation is cancelled by the user
     * @return true on success, false on failure
     */
    default boolean onActionCancelled()
    {
        return false;
    }
}
