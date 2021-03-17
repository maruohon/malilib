package fi.dy.masa.malilib.input;

import java.util.List;

public interface HotkeyManager
{
    HotkeyManager INSTANCE = new HotkeyManagerImpl();

    /**
     * Registers a hotkey provider, which will want to register
     * some hotkeys whenever the key -> keybinds maps need to be rebuilt.<br>
     * Any malilib-based hotkeys should be registered via this method!
     */
    void registerHotkeyProvider(HotkeyProvider provider);

    /**
     * Un-registers a previously registered hotkey provider
     */
    void unregisterHotkeyProvider(HotkeyProvider provider);

    /**
     * Returns a list of all the currently registered hotkeys, grouped by categories
     */
    List<HotkeyCategory> getHotkeyCategories();

    /**
     * Causes the key -> keybinds map to be rebuilt for all registered hotkeys.
     */
    void updateUsedKeys();
}
