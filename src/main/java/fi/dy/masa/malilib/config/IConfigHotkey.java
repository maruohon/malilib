package fi.dy.masa.malilib.config;

import fi.dy.masa.malilib.hotkeys.IHotkey;

public interface IConfigHotkey extends IHotkey
{
    /**
     * Returns the "pretty name" for this config.
     * This is used in the toggle message.
     * @return
     */
    String getPrettyName();

    String getStringValue();

    String getDefaultStringValue();

    void setValueFromString(String value);
}
