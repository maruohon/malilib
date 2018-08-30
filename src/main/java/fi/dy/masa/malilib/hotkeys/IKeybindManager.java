package fi.dy.masa.malilib.hotkeys;

import java.util.List;

public interface IKeybindManager
{
    /**
     * Adds the provided IKeybind to the map of keys -> keybinds,
     * which is used in the input event handler to distribute the events/actions
     * from a key event to any keybinds using that key.
     * @param keybind
     */
    void addKeybindToMap(IKeybind keybind);

    /**
     * Adds hotkeys for the given mod and category to the master list.
     * These are used to show a combined view of all registered keybinds
     * from all mods using this library.
     * @param modName
     * @param keyCategory
     * @param hotkeys
     */
    void addHotkeysForCategory(String modName, String keyCategory, List<? extends IHotkey> hotkeys);
}
