package fi.dy.masa.malilib.hotkeys;

public interface IKeyboardInputHandler
{
    /**
     * Called on keyboard events with the keyCode and scanCode and modifiers, and whether the key was pressed or released.
     * 
     * @param keyCode
     * @param scanCode
     * @param modifiers
     * @param eventKeyState
     * @return
     */
    default boolean onKeyInput(int keyCode, int scanCode, int modifiers, boolean eventKeyState)
    {
        return false;
    }
}
