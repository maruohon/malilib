package fi.dy.masa.malilib.config.value;

import com.google.common.collect.ImmutableList;

public class KeybindDisplayMode extends BaseOptionListConfigValue
{
    public static final KeybindDisplayMode NONE         = new KeybindDisplayMode("none",                "malilib.gui.label.keybind_display.none");
    public static final KeybindDisplayMode KEYS         = new KeybindDisplayMode("keys",                "malilib.gui.label.keybind_display.keys");
    public static final KeybindDisplayMode ACTIONS      = new KeybindDisplayMode("actions",             "malilib.gui.label.keybind_display.actions");
    public static final KeybindDisplayMode KEYS_ACTIONS = new KeybindDisplayMode("keys_and_actions",    "malilib.gui.label.keybind_display.keys_and_actions");

    public static final ImmutableList<KeybindDisplayMode> VALUES = ImmutableList.of(NONE, KEYS, ACTIONS, KEYS_ACTIONS);

    private KeybindDisplayMode(String name, String translationKey)
    {
        super(name, translationKey);
    }
}
