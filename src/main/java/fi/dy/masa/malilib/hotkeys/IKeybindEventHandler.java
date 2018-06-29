package fi.dy.masa.malilib.hotkeys;

public interface IKeybindEventHandler
{
    /**
     * Called when the keybind map is refreshed/recreated.
     * Classes implementing this interface should add all of their keybinds
     * using the {@link IKeybindManager#addKeybindToMap(IKeybind)} method when this method is called.
     * Assume any previously added keybinds have been cleared just before this method is called.
     * @param manager
     */
    void addKeysToMap(IKeybindManager manager);

    /**
     * Called when the event handler is registered.
     * Any mod that wants all their keybinds to appear in the master/combined list of all
     * keybinds, should add them here using the {@link IKeybindManager#addHotkeysForCategory(String, String, IHotkey[])} method).
     * @param manager
     */
    void addHotkeys(IKeybindManager manager);

    /**
     * Called on keyboard events with the key and whether the key was pressed or released.
     * @param eventKey
     * @param eventKeyState
     * @return true if further processing of this key event should be cancelled
     */
    boolean onKeyInput(int eventKey, boolean eventKeyState);

    /**
     * Called on mouse events with the key or wheel value and whether the key was pressed or released.
     * @param eventButton
     * @param dWheel
     * @param eventKeyState
     * @return true if further processing of this mouse button event should be cancelled
     */
    boolean onMouseInput(int eventButton, int dWheel, boolean eventButtonState);
}
