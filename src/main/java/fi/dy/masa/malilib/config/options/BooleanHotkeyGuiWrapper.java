package fi.dy.masa.malilib.config.options;

import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.hotkeys.IHotkey;

/**
 * @Deprecated
 * This is just a temporary hack solution to get booleans and hotkeys to the same line in the config GUIs.
 * DO NOT USE this for actual config values!! This will not get serialized and deserialized properly!!
 * This is only intended as a wrapper type in the config GUIs for now,
 * until the proper malilib rewrite from 1.12.2 is ready to be ported!
 */
@Deprecated
public class BooleanHotkeyGuiWrapper extends ConfigBoolean
{
    protected final IConfigBoolean booleanConfig;
    protected final IHotkey hotkey;

    public BooleanHotkeyGuiWrapper(String name, IConfigBoolean booleanConfig, IHotkey hotkey)
    {
        super(name, booleanConfig.getDefaultBooleanValue(), booleanConfig.getComment());
        this.booleanConfig = booleanConfig;
        this.hotkey = hotkey;
    }

    @Override
    public boolean getBooleanValue()
    {
        return this.booleanConfig.getBooleanValue();
    }

    @Override
    public void setBooleanValue(boolean value)
    {
        this.booleanConfig.setBooleanValue(value);
    }

    @Override
    public boolean isModified()
    {
        // Note: calling isModified() for the IHotkey here directly would not work
        // with multi-type configs like the FeatureToggle in Tweakeroo!
        // Thus we need to get the IKeybind and call it for that specifically.
        return this.booleanConfig.isModified() ||
               this.hotkey.getKeybind().isModified();
    }

    @Override
    public void resetToDefault()
    {
        this.booleanConfig.resetToDefault();
        this.hotkey.getKeybind().resetToDefault();
    }

    public IConfigBoolean getBooleanConfig()
    {
        return this.booleanConfig;
    }

    public IHotkey getHotkey()
    {
        return this.hotkey;
    }
}
