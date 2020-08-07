package fi.dy.masa.malilib.input.callback;

import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;

public interface HotkeyCallback
{
    /**
     * Called when a hotkey action happens.
     * @param action
     * @param key
     * @return true if further processing of the just pressed key should be cancelled
     */
    boolean onKeyAction(KeyAction action, KeyBind key);
}
