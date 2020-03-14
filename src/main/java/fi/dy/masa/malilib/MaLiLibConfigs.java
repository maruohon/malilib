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
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;

public class MaLiLibConfigs implements IConfigHandler
{
    public static class Generic
    {
        public static final ConfigOptionList<KeybindDisplayMode> KEYBIND_DISPLAY  = new ConfigOptionList<KeybindDisplayMode>("keybindDisplay", KeybindDisplayMode.NONE, "Whether or not to display a small indicator/toast\nfor pressed keys and triggered hotkeys/actions");
        public static final ConfigOptionList<HudAlignment> KEYBIND_DISPLAY_ALIGNMENT = new ConfigOptionList<HudAlignment>("keybindDisplayAlignment", HudAlignment.BOTTOM_RIGHT, "The alignment for the keybind display toast messages");

        public static final ConfigHotkey        IGNORED_KEYS                    = new ConfigHotkey("ignoredKeys", "", "Any keys set here will be completely ignored");
        public static final ConfigBoolean       KEYBIND_DISPLAY_CALLBACK_ONLY   = new ConfigBoolean("keybindDisplayCallbackOnly", true, "If enabled, then only keybinds that have a defined action callback\nwill be displayed on the keybind display toast.");
        public static final ConfigBoolean       KEYBIND_DISPLAY_CANCEL_ONLY     = new ConfigBoolean("keybindDisplayCancelOnly", true, "If enabled, then only keybinds that are set to cancel\nfurther processing will be displayed on the\nkeybind display toast. This prevents \"modifier\" keys from\nspamming the display.");
        public static final ConfigInteger       KEYBIND_DISPLAY_DURATION        = new ConfigInteger("keybindDisplayDuration", 5000, 0, 120000, "The duration (in milliseconds) the keybind display toasts are shown");
        public static final ConfigHotkey        OPEN_GUI_CONFIGS                = new ConfigHotkey("openGuiConfigs", "A,C", "Open the in-game malilib config GUI");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                IGNORED_KEYS,
                KEYBIND_DISPLAY,
                KEYBIND_DISPLAY_ALIGNMENT,
                KEYBIND_DISPLAY_CALLBACK_ONLY,
                KEYBIND_DISPLAY_CANCEL_ONLY,
                KEYBIND_DISPLAY_DURATION,
                OPEN_GUI_CONFIGS
        );
    }

    public static class Debug
    {
        public static final KeybindSettings DBG_KS = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, true, false, false, false, true);

        public static final ConfigBoolean GUI_DEBUG                 = new ConfigBoolean("guiDebug", false, "When enabled, all GUI widgets will draw outlines and\nwhen hovered their position and dimension and widget class name");
        public static final ConfigBoolean GUI_DEBUG_ALL             = new ConfigBoolean("guiDebugAll", true, "If true, then all widgets will render the debug outline,\notherwise only the hovered widget will render it");
        public static final ConfigBoolean GUI_DEBUG_INFO_ALWAYS     = new ConfigBoolean("guiDebugInfoAlways", false, "When enabled, the debug position info is always rendered,\neven without hovering the widgets");
        public static final ConfigHotkey  GUI_DEBUG_KEY             = new ConfigHotkey("guiDebugKey", "LMENU", DBG_KS, "If this is set, then the GUI debug only renders\nwhile this key is held down");
        public static final ConfigBoolean KEYBIND_DEBUG             = new ConfigBoolean("keybindDebugging", false, "When enabled, key presses and held keys are\nprinted to the game console (and the action bar, if enabled)");
        public static final ConfigBoolean KEYBIND_DEBUG_ACTIONBAR   = new ConfigBoolean("keybindDebuggingIngame", true, "If enabled, then the messages from 'keybindDebugging'\nare also printed to the in-game action bar");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                GUI_DEBUG,
                GUI_DEBUG_ALL,
                GUI_DEBUG_INFO_ALWAYS,
                GUI_DEBUG_KEY,
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
