package fi.dy.masa.malilib.event.dispatch;

import fi.dy.masa.malilib.input.IKeyboardInputHandler;
import fi.dy.masa.malilib.input.IMouseInputHandler;

public interface IInputDispatcher
{
    /**
     * Registers a keyboard input handler, which will receive
     * the raw key presses.
     * @param handler
     */
    void registerKeyboardInputHandler(IKeyboardInputHandler handler);

    /**
     * Un-registers a previously registered keyboard input handler
     * @param handler
     */
    void unregisterKeyboardInputHandler(IKeyboardInputHandler handler);

    /**
     * Registers a mouse input handler, which will receive
     * the raw mouse key presses and mouse wheel changes, as
     * well as mouse move notifications.
     * @param handler
     */
    void registerMouseInputHandler(IMouseInputHandler handler);

    /**
     * Un-registers a previously registered mouse input handler
     * @param handler
     */
    void unregisterMouseInputHandler(IMouseInputHandler handler);
}
