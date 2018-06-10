package fi.dy.masa.malilib.hotkeys;

import fi.dy.masa.malilib.config.IConfigBase;

public interface IHotkey extends IConfigBase
{
    /**
     * Returns the keybind used by this hotkey
     * @return
     */
    IKeybind getKeybind();
}
