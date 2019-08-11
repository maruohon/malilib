package fi.dy.masa.malilib.config.values;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum KeybindDisplayMode implements IConfigOptionListEntry
{
    NONE            ("none",                "malilib.gui.label.keybind_display.none"),
    KEYS            ("keys",                "malilib.gui.label.keybind_display.keys"),
    ACTIONS         ("actions",             "malilib.gui.label.keybind_display.actions"),
    KEYS_ACTIONS    ("keys_and_actions",    "malilib.gui.label.keybind_display.keys_and_actions");

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
    public IConfigOptionListEntry cycle(boolean forward)
    {
        int id = this.ordinal();

        if (forward)
        {
            if (++id >= values().length)
            {
                id = 0;
            }
        }
        else
        {
            if (--id < 0)
            {
                id = values().length - 1;
            }
        }

        return values()[id % values().length];
    }

    public KeybindDisplayMode fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static KeybindDisplayMode fromStringStatic(String name)
    {
        for (KeybindDisplayMode val : KeybindDisplayMode.values())
        {
            if (val.configString.equalsIgnoreCase(name))
            {
                return val;
            }
        }

        return KeybindDisplayMode.NONE;
    }
}
