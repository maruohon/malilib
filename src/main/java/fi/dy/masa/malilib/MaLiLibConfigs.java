package fi.dy.masa.malilib;

import java.util.Collections;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.category.BaseConfigOptionCategory;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.ColorConfig;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.config.value.KeybindDisplayMode;
import fi.dy.masa.malilib.gui.widget.list.search.ConfigsSearchBarWidget.Scope;
import fi.dy.masa.malilib.input.CancelCondition;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.util.ListUtils;

public class MaLiLibConfigs
{
    public static final int CONFIG_VERSION = 1;

    public static class Generic
    {
        public static final OptionListConfig<Scope> CONFIG_SEARCH_DEFAULT_SCOPE         = new OptionListConfig<>("configSearchDefaultScope", Scope.ALL_CATEGORIES, Scope.VALUES);
        public static final OptionListConfig<KeybindDisplayMode> KEYBIND_DISPLAY        = new OptionListConfig<>("keybindDisplay", KeybindDisplayMode.NONE, KeybindDisplayMode.VALUES);

        public static final IntegerConfig ACTION_BAR_MESSAGE_LIMIT              = new IntegerConfig("actionBarMessageLimit", 3, 1, 16);
        public static final BooleanConfig ACTION_PROMPT_FUZZY_SEARCH            = new BooleanConfig("actionPromptFuzzySearch", false);
        public static final BooleanConfig ACTION_PROMPT_REMEMBER_SEARCH         = new BooleanConfig("actionPromptRememberSearch", false);
        public static final BooleanConfig ACTION_PROMPT_SEARCH_DISPLAY_NAME     = new BooleanConfig("actionPromptSearchDisplayName", false);
        public static final BooleanConfig CONFIG_BACKUP_ANTI_DUPLICATE          = new BooleanConfig("configBackupAntiDuplicate", true);
        public static final IntegerConfig CONFIG_BACKUP_COUNT                   = new IntegerConfig("configBackupCount", 20, 0, 200);
        public static final BooleanConfig CONFIG_WIDGET_BACKGROUND              = new BooleanConfig("configWidgetBackground", true);
        public static final IntegerConfig CUSTOM_SCREEN_SCALE                   = new IntegerConfig("customScreenScale", 2, 0, 8);
        public static final StringConfig  DATA_DUMP_CSV_DELIMITER               = new StringConfig("dataDumpCsvDelimiter", ",");
        public static final BooleanConfig DROP_DOWN_SEARCH_TIP                  = new BooleanConfig("dropDownSearchTip", true);
        public static final BooleanConfig HIDE_ALL_COORDINATES                  = new BooleanConfig("hideAllCoordinates", false);
        public static final ColorConfig   HOVERED_LIST_ENTRY_COLOR              = new ColorConfig("hoveredListEntryColor", "#C0404040");
        public static final IntegerConfig HOVER_TEXT_MAX_WIDTH                  = new IntegerConfig("hoverTextMaxWidth", 310, 16, 4096);
        public static final BooleanConfig KEYBIND_DISPLAY_CALLBACK_ONLY         = new BooleanConfig("keybindDisplayCallbackOnly", true);
        public static final BooleanConfig KEYBIND_DISPLAY_CANCEL_ONLY           = new BooleanConfig("keybindDisplayCancelOnly", true);
        public static final IntegerConfig KEYBIND_DISPLAY_DURATION              = new IntegerConfig("keybindDisplayDuration", 5000, 0, 120000);
        public static final IntegerConfig MESSAGE_FADE_OUT_TIME                 = new IntegerConfig("messageFadeOutTime", 500, 0, 10000);
        public static final BooleanConfig OPTION_LIST_CONFIG_DROPDOWN           = new BooleanConfig("optionListConfigDropdown", false);
        public static final BooleanConfig PRESSED_KEYS_TOAST                    = new BooleanConfig("pressedKeysToast", false);
        public static final BooleanConfig REMEMBER_CONFIG_TAB_SCROLL_POSITIONS  = new BooleanConfig("rememberConfigTabScrollPositions", true);
        public static final BooleanConfig REMEMBER_FILE_BROWSER_SCROLL_POSITIONS= new BooleanConfig("rememberFileBrowserScrollPositions", true);
        public static final ColorConfig   SELECTED_LIST_ENTRY_COLOR             = new ColorConfig("selectedListEntryColor", "#FFFFFFFF");
        public static final BooleanConfig SERVER_MESSAGES                       = new BooleanConfig("serverMessages", true);
        public static final BooleanConfig SHOW_INTERNAL_CONFIG_NAME             = new BooleanConfig("showInternalConfigName", false);
        public static final BooleanConfig SORT_EXTENSION_MOD_OPTIONS            = new BooleanConfig("sortExtensionModOptions", false);

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
                ACTION_BAR_MESSAGE_LIMIT,
                ACTION_PROMPT_FUZZY_SEARCH,
                ACTION_PROMPT_REMEMBER_SEARCH,
                ACTION_PROMPT_SEARCH_DISPLAY_NAME,
                CONFIG_BACKUP_ANTI_DUPLICATE,
                CONFIG_BACKUP_COUNT,
                CONFIG_WIDGET_BACKGROUND,
                CONFIG_SEARCH_DEFAULT_SCOPE,
                CUSTOM_SCREEN_SCALE,
                DATA_DUMP_CSV_DELIMITER,
                DROP_DOWN_SEARCH_TIP,
                HIDE_ALL_COORDINATES,
                HOVER_TEXT_MAX_WIDTH,
                HOVERED_LIST_ENTRY_COLOR,
                KEYBIND_DISPLAY,
                KEYBIND_DISPLAY_CALLBACK_ONLY,
                KEYBIND_DISPLAY_CANCEL_ONLY,
                KEYBIND_DISPLAY_DURATION,
                MESSAGE_FADE_OUT_TIME,
                OPTION_LIST_CONFIG_DROPDOWN,
                PRESSED_KEYS_TOAST,
                REMEMBER_CONFIG_TAB_SCROLL_POSITIONS,
                REMEMBER_FILE_BROWSER_SCROLL_POSITIONS,
                SELECTED_LIST_ENTRY_COLOR,
                SERVER_MESSAGES,
                SHOW_INTERNAL_CONFIG_NAME,
                SORT_EXTENSION_MOD_OPTIONS
        );
    }

    public static class Hotkeys
    {
        public static final KeyBindSettings SCROLL_ADJUST = KeyBindSettings.builder().extra().cancel(CancelCondition.ON_SUCCESS).noOutput().build();

        public static final HotkeyConfig IGNORED_KEYS                           = new HotkeyConfig("ignoredKeys", "");
        public static final HotkeyConfig OPEN_ACTION_PROMPT_SCREEN              = new HotkeyConfig("openActionPromptScreen", "");
        public static final HotkeyConfig OPEN_CONFIG_SCREEN                     = new HotkeyConfig("openConfigScreen", "A,C");
        public static final HotkeyConfig SCROLL_VALUE_ADJUST_DECREASE           = new HotkeyConfig("scrollValueAdjustDecrease", "SCROLL_DOWN", SCROLL_ADJUST);
        public static final HotkeyConfig SCROLL_VALUE_ADJUST_INCREASE           = new HotkeyConfig("scrollValueAdjustIncrease", "SCROLL_UP", SCROLL_ADJUST);
        public static final HotkeyConfig SCROLL_VALUE_ADJUST_MODIFIER           = new HotkeyConfig("scrollValueAdjustModifier", "", KeyBindSettings.INGAME_MODIFIER_EMPTY);

        public static final ImmutableList<HotkeyConfig> FUNCTIONAL_HOTKEYS = ImmutableList.of(
                OPEN_ACTION_PROMPT_SCREEN,
                OPEN_CONFIG_SCREEN,
                SCROLL_VALUE_ADJUST_DECREASE,
                SCROLL_VALUE_ADJUST_INCREASE,
                SCROLL_VALUE_ADJUST_MODIFIER
        );

        public static final ImmutableList<HotkeyConfig> HOTKEYS = ListUtils.getAppendedList(FUNCTIONAL_HOTKEYS, Collections.singletonList(IGNORED_KEYS));
    }

    public static class Debug
    {
        public static final BooleanConfig DEBUG_MESSAGES            = new BooleanConfig("debugMessages", false);
        public static final BooleanConfig GUI_DEBUG                 = new BooleanConfig("guiDebug", false);
        public static final BooleanConfig GUI_DEBUG_ALL             = new BooleanConfig("guiDebugAll", true);
        public static final BooleanConfig GUI_DEBUG_INFO_ALWAYS     = new BooleanConfig("guiDebugInfoAlways", false);
        public static final HotkeyConfig  GUI_DEBUG_KEY             = new HotkeyConfig("guiDebugKey", "LMENU", KeyBindSettings.GUI_MODIFIER);
        public static final BooleanConfig INFO_OVERLAY_DEBUG        = new BooleanConfig("infoOverlayDebug", false);
        public static final BooleanConfig KEYBIND_DEBUG             = new BooleanConfig("keybindDebug", false);
        public static final BooleanConfig KEYBIND_DEBUG_ACTIONBAR   = new BooleanConfig("keybindDebugActionBar", false);
        public static final BooleanConfig KEYBIND_DEBUG_TOAST       = new BooleanConfig("keybindDebugToast", true);
        public static final BooleanConfig MESSAGE_KEY_TO_CHAT       = new BooleanConfig("messageKeyToChat", false);

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
                DEBUG_MESSAGES,
                GUI_DEBUG,
                GUI_DEBUG_ALL,
                GUI_DEBUG_INFO_ALWAYS,
                GUI_DEBUG_KEY,
                INFO_OVERLAY_DEBUG,
                KEYBIND_DEBUG,
                KEYBIND_DEBUG_ACTIONBAR,
                KEYBIND_DEBUG_TOAST,
                MESSAGE_KEY_TO_CHAT
        );

        public static final ImmutableList<HotkeyConfig> HOTKEYS = ImmutableList.of(GUI_DEBUG_KEY);
    }

    public static class Internal
    {
        public static final StringConfig ACTIVE_CONFIG_PROFILE          = new StringConfig("activeConfigProfile", "");
        public static final StringConfig ACTION_PROMPT_SEARCH_TEXT      = new StringConfig("actionPromptSearchText", "");
        public static final StringConfig ACTION_PROMPT_SELECTED_LIST    = new StringConfig("actionPromptSelectedList", "all");
        public static final StringConfig PREVIOUS_ACTION_WIDGET_SCREEN  = new StringConfig("previousActionWidgetScreen", "");

        public static final ImmutableList<ConfigOption<?>> OPTIONS = ImmutableList.of(
                ACTIVE_CONFIG_PROFILE,
                ACTION_PROMPT_SEARCH_TEXT,
                ACTION_PROMPT_SELECTED_LIST,
                PREVIOUS_ACTION_WIDGET_SCREEN
        );
    }

    public static final ImmutableList<ConfigOptionCategory> CATEGORIES = ImmutableList.of(
            BaseConfigOptionCategory.normal("Generic",  Generic.OPTIONS),
            BaseConfigOptionCategory.normal("Hotkeys",  Hotkeys.HOTKEYS),
            BaseConfigOptionCategory.normal("Debug",    Debug.OPTIONS),
            BaseConfigOptionCategory.normal("Internal", Internal.OPTIONS)
    );
}
