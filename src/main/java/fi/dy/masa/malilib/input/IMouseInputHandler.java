package fi.dy.masa.malilib.input;

public interface IMouseInputHandler
{
    /**
     * Called on mouse events with the key or wheel value and whether the key was pressed or released.
     * @param eventButton
     * @param wheelDelta
     * @param eventButtonState
     * @return true if further processing of this mouse button event should be cancelled
     */
    boolean onMouseInput(int eventButton, int wheelDelta, boolean eventButtonState);

    /**
     * Called when the mouse is moved
     */
    default void onMouseMoved() {}
}
