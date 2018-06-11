package fi.dy.masa.malilib.hotkeys;

public interface IKeybindManager
{
    /**
     * Adds the provided IKeybind to the map of keys -> keybinds,
     * which is used in the input event handler to distribute the events/actions
     * from a key event to any keybinds using that key.
     * @param keybind
     */
    void addKeybindToMap(IKeybind keybind);
}
