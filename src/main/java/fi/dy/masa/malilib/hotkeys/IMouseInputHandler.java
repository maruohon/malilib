package fi.dy.masa.malilib.hotkeys;

public interface IMouseInputHandler
{
    /**
     * Called on mouse button events with the key and whether the key was pressed or released.
     * @param mouseX
     * @param mouseY
     * @param eventButton
     * @param eventButtonState
     * @return true if further processing of this mouse button event should be cancelled
     */
    default boolean onMouseClick(int mouseX, int mouseY, int eventButton, boolean eventButtonState)
    {
        return false;
    }

    /**
     * Called when the mouse wheel is scrolled
     * @param mouseX
     * @param mouseY
     * @param amount
     * @return
     */
    default boolean onMouseScroll(int mouseX, int mouseY, double amount)
    {
        return false;
    }

    /**
     * Called when the mouse is moved
     * @param mouseX
     * @param mouseY
     */
    default void onMouseMove(int mouseX, int mouseY) {}
}
