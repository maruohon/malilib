package fi.dy.masa.malilib.input;

import java.util.List;

public interface HotkeyProvider
{
    /**
     * Returns a list of all hotkeys that should be registered.
     * This is called when the master hotkey list in malilib is being rebuilt,
     * Any hotkeys not on the returned list will not function!
     */
    List<? extends Hotkey> getAllHotkeys();

    /**
     * Returns a list of all the hotkeys, grouped in categories.
     * This is mostly just used for the keybind overlap info hover text.
     */
    List<HotkeyCategory> getHotkeysByCategories();
}
