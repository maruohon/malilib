package fi.dy.masa.malilib;

import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.input.callback.AdjustableValueHotkeyCallback;
import fi.dy.masa.malilib.network.message.MessagePacketHandler;
import fi.dy.masa.malilib.overlay.message.MessageUtils;
import fi.dy.masa.malilib.overlay.widget.MessageRendererWidget;

public class MaLiLibConfigInit
{
    protected static void init()
    {
        MaLiLibConfigs.Hotkeys.OPEN_ACTION_PROMPT_SCREEN.createCallbackForAction(MaLiLibActions.OPEN_ACTION_PROMPT_SCREEN);
        MaLiLibConfigs.Hotkeys.OPEN_CONFIG_SCREEN.createCallbackForAction(MaLiLibActions.OPEN_CONFIG_SCREEN);
        MaLiLibConfigs.Hotkeys.SCROLL_VALUE_ADJUST_DECREASE.createCallbackForAction(AdjustableValueHotkeyCallback::scrollAdjustDecrease);
        MaLiLibConfigs.Hotkeys.SCROLL_VALUE_ADJUST_INCREASE.createCallbackForAction(AdjustableValueHotkeyCallback::scrollAdjustIncrease);

        MaLiLibConfigs.Generic.OPTION_LIST_CONFIG_USE_DROPDOWN.addValueChangeListener(GuiUtils::reInitCurrentScreen);
        MaLiLibConfigs.Generic.CUSTOM_SCREEN_SCALE.addValueChangeListener(BaseScreen::applyCustomScreenScaleChange);
        MaLiLibConfigs.Generic.SERVER_MESSAGES.setValueChangeCallback((n, o) -> MessagePacketHandler.updateRegistration(n));
        MaLiLibConfigs.Generic.SERVER_MESSAGES.setValueLoadCallback(MessagePacketHandler::updateRegistration);

        MaLiLibConfigs.Generic.CUSTOM_HOTBAR_MESSAGE_LIMIT.setValueChangeCallback((n, o) -> setCustomHotbarMessageLimit(n));
        MaLiLibConfigs.Generic.CUSTOM_HOTBAR_MESSAGE_LIMIT.setValueLoadCallback(MaLiLibConfigInit::setCustomHotbarMessageLimit);
    }

    private static void setCustomHotbarMessageLimit(int limit)
    {
        MessageRendererWidget widget = MessageUtils.findInfoWidget(MessageRendererWidget.class, null, MessageUtils.CUSTOM_ACTION_BAR_MARKER);

        if (widget != null)
        {
            widget.setMaxMessages(limit);
        }
    }
}
