package fi.dy.masa.malilib.input;

public interface IKeyboardInputHandler
{
    /**
     * Called on keyboard events with the key and whether the key was pressed or released.
     * @param keyCode
     * @param scanCode
     * @param modifiers
     * @param eventKeyState
     * @return true if further processing of this key event should be cancelled
     */
    boolean onKeyInput(int keyCode, int scanCode, int modifiers, boolean eventKeyState);
}
