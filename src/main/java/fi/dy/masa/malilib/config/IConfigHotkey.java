package fi.dy.masa.malilib.config;

import fi.dy.masa.malilib.hotkeys.IKeybind;

public interface IConfigHotkey
{
    /**
     * Returns the "pretty name" for this config.
     * This is used in the toggle message.
     * @return
     */
    String getPrettyName();

    IKeybind getKeybind();

    String getStringValue();

    String getDefaultStringValue();

    void setValueFromString(String value);
}
