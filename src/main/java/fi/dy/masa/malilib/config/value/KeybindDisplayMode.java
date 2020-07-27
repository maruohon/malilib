package fi.dy.masa.malilib.config.value;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.util.StringUtils;

public enum KeybindDisplayMode implements IConfigOptionListEntry<KeybindDisplayMode>
{
    NONE            ("none",                "malilib.gui.label.keybind_display.none"),
    KEYS            ("keys",                "malilib.gui.label.keybind_display.keys"),
    ACTIONS         ("actions",             "malilib.gui.label.keybind_display.actions"),
    KEYS_ACTIONS    ("keys_and_actions",    "malilib.gui.label.keybind_display.keys_and_actions");

    public static final ImmutableList<KeybindDisplayMode> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String translationKey;

    KeybindDisplayMode(String configString, String translationKey)
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
    public KeybindDisplayMode cycle(boolean forward)
    {
        return ConfigOptionListEntry.cycleValue(VALUES, this.ordinal(), forward);
    }

    @Override
    public KeybindDisplayMode fromString(String name)
    {
        return ConfigOptionListEntry.findValueByName(name, VALUES);
    }
}
