package fi.dy.masa.malilib.event.dispatch;

import java.util.List;
import fi.dy.masa.malilib.input.KeyBindProvider;
import fi.dy.masa.malilib.input.KeyBindCategory;

public interface KeyBindManager
{
    /**
     * Registers a keybind provider, which will want to register
     * some keybinds whenever the key to keybind maps need to be rebuilt.
     * @param provider
     */
    void registerKeyBindProvider(KeyBindProvider provider);

    /**
     * Un-registers a previously registered keybind provider
     * @param provider
     */
    void unregisterKeyBindProvider(KeyBindProvider provider);

    /**
     * Returns a list of all the currently registered keybinds, grouped by categories
     * @return
     */
    List<KeyBindCategory> getKeyBindCategories();

    /**
     * Forces the keys -> keybinds map to be rebuilt for all registered hotkeys.
     */
    void updateUsedKeys();
}
