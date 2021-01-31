package fi.dy.masa.malilib.input.callback;

import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;

public interface HotkeyCallback
{
    /**
     * Called when a hotkey action happens.
     * @param action
     * @param key
     * @return true if the action was successful. NOTE: This used to be directly whether or not further processing should be cancelled! This changed in malilib 1.0.
     */
    boolean onKeyAction(KeyAction action, KeyBind key);
}
