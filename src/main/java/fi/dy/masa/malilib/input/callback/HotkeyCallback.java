package fi.dy.masa.malilib.input.callback;

import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;

public interface HotkeyCallback
{
    /**
     * Called when a hotkey is triggered.
     * @param action the key action this callback is being called for (either press or release)
     * @param key the keybind that this callback is being called for,
     *            in case the same callback instance is used for multiple different hotkeys
     * @return the action result of executing the callback
     */
    ActionResult onKeyAction(KeyAction action, KeyBind key);
}
