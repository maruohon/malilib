package fi.dy.masa.malilib.input;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.value.ConfigOptionListEntry;
import fi.dy.masa.malilib.config.value.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum KeyAction implements IConfigOptionListEntry<KeyAction>
{
    PRESS   ("press",   "malilib.label.key_action.press"),
    RELEASE ("release", "malilib.label.key_action.release"),
    BOTH    ("both",    "malilib.label.key_action.both");

    public static final ImmutableList<KeyAction> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String translationKey;

    KeyAction(String configString, String translationKey)
    {
        this.configString = configString;
        this.translationKey = translationKey;
    }

    @Override
    public String getStringValue()
    {
        return this.configString;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    @Override
    public KeyAction cycle(boolean forward)
    {
        return ConfigOptionListEntry.cycleValue(VALUES, this.ordinal(), forward);
    }

    @Override
    public KeyAction fromString(String name)
    {
        return ConfigOptionListEntry.findValueByName(name, VALUES);
    }
}
