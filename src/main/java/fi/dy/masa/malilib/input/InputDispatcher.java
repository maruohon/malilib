package fi.dy.masa.malilib.input;

public interface InputDispatcher
{
    InputDispatcher INSTANCE = new InputDispatcherImpl();

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
     * Registers a mouse input handler, which will receive
     * the raw mouse key presses and mouse wheel changes, as
     * well as mouse move notifications.
     * @param handler
     */
    void registerMouseInputHandler(MouseInputHandler handler);

    /**
     * Un-registers a previously registered mouse input handler
     * @param handler
     */
    void unregisterMouseInputHandler(MouseInputHandler handler);
}
