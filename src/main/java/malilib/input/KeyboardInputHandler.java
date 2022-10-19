package malilib.input;

import malilib.event.PrioritizedEventHandler;

public interface KeyboardInputHandler extends PrioritizedEventHandler
{
    /**
     * Called on keyboard events with the key and whether the key was pressed or released.
     * @param keyCode
     * @param scanCode
     * @param modifiers
     * @param keyState
     * @return true if further processing of this key event should be cancelled
     */
    boolean onKeyInput(int keyCode, int scanCode, int modifiers, boolean keyState);
}
