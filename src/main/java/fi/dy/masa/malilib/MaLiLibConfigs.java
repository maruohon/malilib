package fi.dy.masa.malilib;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.category.BaseConfigOptionCategory;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.config.value.HudAlignment;
import fi.dy.masa.malilib.config.value.KeybindDisplayMode;
import fi.dy.masa.malilib.gui.widget.ConfigsSearchBarWidget.Scope;
import fi.dy.masa.malilib.input.CancelCondition;
import fi.dy.masa.malilib.input.Context;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBindSettings;

public class MaLiLibConfigs
{
    public static class Generic
    {
        public static final OptionListConfig<Scope> CONFIG_SEARCH_DEFAULT_SCOPE         = new OptionListConfig<>("configSearchDefaultScope", Scope.ALL_CATEGORIES, Scope.VALUES);
        public static final OptionListConfig<KeybindDisplayMode> KEYBIND_DISPLAY        = new OptionListConfig<>("keybindDisplay", KeybindDisplayMode.NONE, KeybindDisplayMode.VALUES);
        public static final OptionListConfig<HudAlignment> KEYBIND_DISPLAY_ALIGNMENT    = new OptionListConfig<>("keybindDisplayAlignment", HudAlignment.BOTTOM_RIGHT, HudAlignment.VALUES);

        public static final StringConfig DATA_DUMP_CSV_DELIMITER                = new StringConfig("dataDumpCsvDelimiter", ",");
        public static final IntegerConfig HOVER_TEXT_MAX_WIDTH                  = new IntegerConfig("hoverTextMaxWidth", 310, 16, 4096);
        public static final HotkeyConfig IGNORED_KEYS                           = new HotkeyConfig("ignoredKeys", "");
        public static final BooleanConfig KEYBIND_DISPLAY_CALLBACK_ONLY         = new BooleanConfig("keybindDisplayCallbackOnly", true);
        public static final BooleanConfig KEYBIND_DISPLAY_CANCEL_ONLY           = new BooleanConfig("keybindDisplayCancelOnly", true);
        public static final IntegerConfig KEYBIND_DISPLAY_DURATION              = new IntegerConfig("keybindDisplayDuration", 5000, 0, 120000);
        public static final HotkeyConfig OPEN_GUI_CONFIGS                       = new HotkeyConfig("openGuiConfigs", "A,C");
        public static final BooleanConfig OPTION_LIST_CONFIG_DROPDOWN           = new BooleanConfig("optionListConfigDropdown", false);
        public static final BooleanConfig REMEMBER_CONFIG_TAB_SCROLL_POSITIONS  = new BooleanConfig("rememberConfigTabScrollPositions", true);

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
                CONFIG_SEARCH_DEFAULT_SCOPE,
                DATA_DUMP_CSV_DELIMITER,
                HOVER_TEXT_MAX_WIDTH,
                IGNORED_KEYS,
                KEYBIND_DISPLAY,
                KEYBIND_DISPLAY_ALIGNMENT,
                KEYBIND_DISPLAY_CALLBACK_ONLY,
                KEYBIND_DISPLAY_CANCEL_ONLY,
                KEYBIND_DISPLAY_DURATION,
                OPEN_GUI_CONFIGS,
                OPTION_LIST_CONFIG_DROPDOWN,
                REMEMBER_CONFIG_TAB_SCROLL_POSITIONS
        );
    }

    public static class Info
    {
        public static final BooleanConfig DROP_DOWN_SEARCH_TIP          = new BooleanConfig("dropDownSearchTip", true);

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
                DROP_DOWN_SEARCH_TIP
        );
    }

    public static class Debug
    {
        public static final KeyBindSettings DBG_KS = KeyBindSettings.create(Context.GUI, KeyAction.PRESS, true, false, false, CancelCondition.NEVER, true);

        public static final BooleanConfig GUI_DEBUG                 = new BooleanConfig("guiDebug", false);
        public static final BooleanConfig GUI_DEBUG_ALL             = new BooleanConfig("guiDebugAll", true);
        public static final BooleanConfig GUI_DEBUG_INFO_ALWAYS     = new BooleanConfig("guiDebugInfoAlways", false);
        public static final HotkeyConfig GUI_DEBUG_KEY              = new HotkeyConfig("guiDebugKey", "LMENU", DBG_KS);
        public static final BooleanConfig INFO_OVERLAY_DEBUG        = new BooleanConfig("infoOverlayDebug", false);
        public static final BooleanConfig KEYBIND_DEBUG             = new BooleanConfig("keybindDebugging", false);
        public static final BooleanConfig KEYBIND_DEBUG_ACTIONBAR   = new BooleanConfig("keybindDebuggingIngame", true);

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
                GUI_DEBUG,
                GUI_DEBUG_ALL,
                GUI_DEBUG_INFO_ALWAYS,
                GUI_DEBUG_KEY,
                INFO_OVERLAY_DEBUG,
                KEYBIND_DEBUG,
                KEYBIND_DEBUG_ACTIONBAR
        );
    }

    public static final ImmutableList<ConfigOptionCategory> CATEGORIES = ImmutableList.of(
            BaseConfigOptionCategory.normal("Generic",  Generic.OPTIONS),
            BaseConfigOptionCategory.normal("Info",     Info.OPTIONS),
            BaseConfigOptionCategory.normal("Debug",    Debug.OPTIONS)
    );
}
