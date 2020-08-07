package fi.dy.masa.malilib.event.dispatch;

import java.util.List;
import fi.dy.masa.malilib.input.KeyBindCategory;
import fi.dy.masa.malilib.input.KeyBindProvider;

public interface KeyBindManager
{
    KeyBindManager INSTANCE = new KeyBindManagerImpl();
    /**
     * 
     * Registers a keybind provider, which will want to register
     * some keybinds whenever the key -> keybinds maps need to be rebuilt.
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
     * Causes the key -> keybinds map to be rebuilt for all registered hotkeys.
     */
    void updateUsedKeys();
}
