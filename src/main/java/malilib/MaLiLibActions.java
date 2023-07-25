package malilib;

import malilib.action.Action;
import malilib.action.util.ActionUtils;
import malilib.action.NamedAction;
import malilib.action.ParameterizedAction;
import malilib.action.builtin.UtilityActions;
import malilib.config.util.ConfigUtils;
import malilib.gui.action.ActionPromptScreen;
import malilib.gui.action.ActionWidgetScreen;
import malilib.gui.edit.CustomIconListScreen;
import malilib.gui.edit.MessageRedirectListScreen;
import malilib.input.callback.AdjustableValueHotkeyCallback;
import malilib.listener.EventListener;
import malilib.overlay.message.MessageUtils;

public class MaLiLibActions
{
    public static final NamedAction OPEN_ACTION_PROMPT_SCREEN       = register("openActionPromptScreen", ActionPromptScreen::openActionPromptScreen);
    public static final NamedAction OPEN_CONFIG_SCREEN              = register("openConfigScreen", MaLiLibConfigScreen::open);
    public static final NamedAction SCROLL_VALUE_ADJUST_DECREASE    = register("scrollValueAdjustDecrease", AdjustableValueHotkeyCallback::scrollAdjustDecrease);
    public static final NamedAction SCROLL_VALUE_ADJUST_INCREASE    = register("scrollValueAdjustIncrease", AdjustableValueHotkeyCallback::scrollAdjustIncrease);

    public static void init()
    {
        register("addMessage",                      MessageUtils::addMessageAction);
        register("addToast",                        MessageUtils::addToastAction);
        register("createActionWidgetScreen",        ActionWidgetScreen::openCreateActionWidgetScreen);
        register("loadAllConfigsFromFile",          ConfigUtils::loadAllConfigsFromFileAction);
        register("openActionWidgetScreen",          ActionWidgetScreen::openActionWidgetScreen);
        register("openCustomIconsListScreen",       CustomIconListScreen::openCustomIconListScreenAction);
        register("openMessageRedirectsListScreen",  MessageRedirectListScreen::openMessageRedirectListScreenAction);
        register("openPreviousActionWidgetScreen",  ActionWidgetScreen::openPreviousActionWidgetScreen);
        register("switchConfigProfile",             ConfigUtils::switchConfigProfile);

        register("cycleGameMode",                   UtilityActions::cycleGameMode);
        register("copyScreenshotToClipboard",       UtilityActions::copyScreenshotToClipboard);
        register("dropHeldStack",                   UtilityActions::dropHeldStack);
        register("dropOneItem",                     UtilityActions::dropOneItem);
        register("listAllBaseActions",              UtilityActions::listAllBaseActions);
        register("listAllConfigs",                  UtilityActions::listAllConfigs);
        register("listAllConfigCategories",         UtilityActions::listAllConfigCategories);
        register("runCommand",                      UtilityActions::runVanillaCommand);
        register("sendChatMessage",                 UtilityActions::sendChatMessage);
        register("setPlayerFractionalXZ",           UtilityActions::setPlayerFractionalXZ);
        register("setPlayerPitch",                  UtilityActions::setPlayerPitch);
        register("setPlayerYaw",                    UtilityActions::setPlayerYaw);
        register("setSelectedHotbarSlot",           UtilityActions::setSelectedHotbarSlot);
        register("takeScreenshot",                  UtilityActions::takeScreenshot);
        register("toggleChunkBorders",              UtilityActions::toggleChunkBorders);
        register("toggleF3Screen",                  UtilityActions::toggleF3Screen);
        register("toggleF3ScreenLagometer",         UtilityActions::toggleF3ScreenLagometer);
        register("toggleF3ScreenProfilerPieChart",  UtilityActions::toggleF3ScreenProfilerPieChart);

        ActionUtils.registerBooleanConfigActions(MaLiLibConfigs.Generic.OPTIONS);
        ActionUtils.registerBooleanConfigActions(MaLiLibConfigs.Debug.OPTIONS);
    }

    private static NamedAction register(String name, EventListener action)
    {
        return ActionUtils.register(MaLiLibReference.MOD_INFO, name, action);
    }

    private static NamedAction register(String name, Action action)
    {
        return ActionUtils.register(MaLiLibReference.MOD_INFO, name, action);
    }

    private static NamedAction register(String name, ParameterizedAction action)
    {
        return ActionUtils.register(MaLiLibReference.MOD_INFO, name, action);
    }
}
