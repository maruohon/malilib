package fi.dy.masa.malilib.hotkeys;

import java.util.List;

public interface IKeybindManager
{
    /**
     * Registers a keybind provider, which will want to register
     * some keybinds whenever the key to keybind maps need to be rebuilt.
     * @param provider
     */
    void registerKeybindProvider(IKeybindProvider provider);

    /**
     * Un-registers a previously registered keybind provider
     * @param provider
     */
    void unregisterKeybindProvider(IKeybindProvider provider);

    /**
     * Returns a list of all the currently registered keybinds, grouped by categories
     * @return
     */
    List<KeybindCategory> getKeybindCategories();

    /**
     * Forces the keys -> keybinds map to be rebuilt for all registered hotkeys.
     */
    void updateUsedKeys();
}
