package malilib.input;

public interface InputDispatcher
{
    /**
     * Registers a keyboard input handler, which will receive
     * the raw key presses.
     * @param handler
     */
    void registerKeyboardInputHandler(KeyboardInputHandler handler);

    /**
     * Un-registers a previously registered keyboard input handler
     * @param handler
     */
    void unregisterKeyboardInputHandler(KeyboardInputHandler handler);

    /**
     * Registers a mouse click input handler, which will receive
     * all mouse key presses.
     */
    void registerMouseClickHandler(MouseClickHandler handler);

    /**
     * Registers a mouse scroll input handler, which will receive
     * all mouse wheel events.
     */
    void registerMouseScrollHandler(MouseScrollHandler handler);

    /**
     * Registers a mouse move input handler, which will receive
     * mouse move notifications.
     */
    void registerMouseMoveHandler(MouseMoveHandler handler);

    /**
     * Un-registers a previously registered mouse click input handler
     */
    void unregisterMouseClickHandler(MouseClickHandler handler);

    /**
     * Un-registers a previously registered mouse click input handler
     */
    void unregisterMouseScrollHandler(MouseScrollHandler handler);

    /**
     * Un-registers a previously registered mouse click input handler
     */
    void unregisterMouseMoveHandler(MouseMoveHandler handler);
}
