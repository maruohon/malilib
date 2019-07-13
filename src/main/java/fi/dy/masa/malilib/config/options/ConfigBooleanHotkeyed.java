package fi.dy.masa.malilib.config.options;

import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyCallbackToggleBooleanConfigWithMessage;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigBooleanHotkeyed extends ConfigBoolean implements IHotkey
{
    protected final IKeybind keybind;

    public ConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey, String comment)
    {
        this(name, defaultValue, defaultHotkey, comment, StringUtils.splitCamelCase(name));
    }

    public ConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey, String comment, String prettyName)
    {
        this(name, defaultValue, defaultHotkey, KeybindSettings.DEFAULT, comment, prettyName);
    }

    public ConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey, KeybindSettings settings, String comment, String prettyName)
    {
        super(name, defaultValue, comment, prettyName);

        this.keybind = KeybindMulti.fromStorageString(defaultHotkey, settings);
        this.keybind.setCallback(new KeyCallbackToggleBooleanConfigWithMessage(this));
    }

    @Override
    public IKeybind getKeybind()
    {
        return this.keybind;
    }
}
