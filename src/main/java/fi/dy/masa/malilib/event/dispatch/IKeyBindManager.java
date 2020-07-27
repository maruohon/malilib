package fi.dy.masa.malilib.event.dispatch;

import java.util.List;
import fi.dy.masa.malilib.input.IKeyBindProvider;
import fi.dy.masa.malilib.input.KeyBindCategory;

public interface IKeyBindManager
{
    /**
     * Registers a keybind provider, which will want to register
     * some keybinds whenever the key to keybind maps need to be rebuilt.
     * @param provider
     */
    void registerKeyBindProvider(IKeyBindProvider provider);

    /**
     * Un-registers a previously registered keybind provider
     * @param provider
     */
    void unregisterKeyBindProvider(IKeyBindProvider provider);

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
