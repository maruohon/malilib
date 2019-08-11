package fi.dy.masa.malilib;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.config.options.IConfigBase;
import fi.dy.masa.malilib.config.values.HudAlignment;
import fi.dy.masa.malilib.config.values.KeybindDisplayMode;

public class MaLiLibConfigs implements IConfigHandler
{
    public static class Generic
    {
        public static final ConfigHotkey        IGNORED_KEYS                = new ConfigHotkey("ignoredKeys", "", "Any keys set here will be completely ignored");
        public static final ConfigOptionList    KEYBIND_DISPLAY             = new ConfigOptionList("keybindDisplay", KeybindDisplayMode.NONE, "Whether or not to display a small indicator/toast\nfor pressed keys and triggered hotkeys/actions");
        public static final ConfigOptionList    KEYBIND_DISPLAY_ALIGNMENT   = new ConfigOptionList("keybindDisplayAlignment", HudAlignment.BOTTOM_RIGHT, "The alignment for the keybind display toast messages");
        public static final ConfigBoolean       KEYBIND_DISPLAY_CANCEL_ONLY = new ConfigBoolean("keybindDisplayCancelOnly", true, "If enabled, then only keybinds that are set to cancel\nfurther processing will be displayed on the\nkeybind display toast. This prevents \"modifier\" keys from\nspamming the display.");
        public static final ConfigInteger       KEYBIND_DISPLAY_DURATION    = new ConfigInteger("keybindDisplayDuration", 5000, 0, 120000, "The duration (in milliseconds) the keybind display toasts are shown");
        public static final ConfigHotkey        OPEN_GUI_CONFIGS            = new ConfigHotkey("openGuiConfigs", "A,C", "Open the in-game malilib config GUI");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                IGNORED_KEYS,
                KEYBIND_DISPLAY,
                KEYBIND_DISPLAY_ALIGNMENT,
                KEYBIND_DISPLAY_CANCEL_ONLY,
                KEYBIND_DISPLAY_DURATION,
                OPEN_GUI_CONFIGS
        );
    }

    public static class Debug
    {
        public static final ConfigBoolean KEYBIND_DEBUG             = new ConfigBoolean("keybindDebugging", false, "When enabled, key presses and held keys are\nprinted to the game console (and the action bar, if enabled)");
        public static final ConfigBoolean KEYBIND_DEBUG_ACTIONBAR   = new ConfigBoolean("keybindDebuggingIngame", true, "If enabled, then the messages from 'keybindDebugging'\nare also printed to the in-game action bar");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                KEYBIND_DEBUG,
                KEYBIND_DEBUG_ACTIONBAR
        );
    }

    @Override
    public String getModName()
    {
        return MaLiLibReference.MOD_NAME;
    }

    @Override
    public String getConfigFileName()
    {
        return MaLiLibReference.MOD_ID + ".json";
    }

    @Override
    public Map<String, List<? extends IConfigBase>> getConfigsPerCategories()
    {
        Map<String, List<? extends IConfigBase>> map = new LinkedHashMap<>();

        map.put("Generic",  Generic.OPTIONS);
        map.put("Debug",    Debug.OPTIONS);

        return map;
    }

    @Override
    public boolean shouldSaveCategoryToFile(String category)
    {
        return category.equals("Debug") == false;
    }
}
