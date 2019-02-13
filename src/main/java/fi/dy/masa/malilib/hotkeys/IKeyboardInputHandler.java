package fi.dy.masa.malilib.hotkeys;

public interface IKeyboardInputHandler
{
    /**
     * Called on keyboard events with the key and whether the key was pressed or released.
     * 
     * @deprecated since malilib 0.10.0. Use {@link onKeyInput(int keyCode, int scanCode, int modifiers, boolean eventKeyState)}
     * @param eventKey
     * @param eventKeyState
     * @return true if further processing of this key event should be cancelled
     */
    @Deprecated
    default boolean onKeyInput(int eventKey, boolean eventKeyState)
    {
        return false;
    }

    /**
     * Called on keyboard events with the keyCode and scanCode and modifiers, and whether the key was pressed or released.
     * @since malilib 0.10.0
     * 
     * @param keyCode
     * @param scanCode
     * @param modifiers
     * @param eventKeyState
     * @return
     */
    default boolean onKeyInput(int keyCode, int scanCode, int modifiers, boolean eventKeyState)
    {
        return this.onKeyInput(keyCode, eventKeyState);
    }
}
