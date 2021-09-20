package fi.dy.masa.malilib;

import fi.dy.masa.malilib.config.BaseModConfig;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.input.CustomHotkeyManager;
import fi.dy.masa.malilib.input.callback.AdjustableValueHotkeyCallback;
import fi.dy.masa.malilib.input.callback.HotkeyCallback;
import fi.dy.masa.malilib.network.message.MessagePacketHandler;
import fi.dy.masa.malilib.overlay.widget.ConfigStatusIndicatorContainerWidget;
import fi.dy.masa.malilib.registry.Registry;

public class MaLiLibInitHandler implements InitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        Registry.CONFIG_MANAGER.registerConfigHandler(BaseModConfig.createDefaultModConfig(MaLiLibReference.MOD_INFO, MaLiLibConfigs.CONFIG_VERSION, MaLiLibConfigs.CATEGORIES));
        Registry.CONFIG_TAB.registerConfigTabProvider(MaLiLibReference.MOD_INFO, MaLiLibConfigScreen::getConfigTabs);

        Registry.HOTKEY_MANAGER.registerHotkeyProvider(MaLiLibHotkeyProvider.INSTANCE);
        Registry.HOTKEY_MANAGER.registerHotkeyProvider(CustomHotkeyManager.INSTANCE);
        Registry.HOTKEY_MANAGER.registerHotkeyProvider(ConfigStatusIndicatorContainerWidget.getHotkeyProvider());

        MaLiLibConfigs.Hotkeys.OPEN_ACTION_PROMPT_SCREEN.getKeyBind().setCallback(HotkeyCallback.of(MaLiLibActions.OPEN_ACTION_PROMPT_SCREEN));
        MaLiLibConfigs.Hotkeys.OPEN_CONFIG_SCREEN.getKeyBind().setCallback(HotkeyCallback.of(MaLiLibActions.OPEN_CONFIG_SCREEN));
        MaLiLibConfigs.Hotkeys.SCROLL_VALUE_ADJUST_DECREASE.getKeyBind().setCallback(HotkeyCallback.of(AdjustableValueHotkeyCallback::scrollAdjustDecrease));
        MaLiLibConfigs.Hotkeys.SCROLL_VALUE_ADJUST_INCREASE.getKeyBind().setCallback(HotkeyCallback.of(AdjustableValueHotkeyCallback::scrollAdjustIncrease));

        MaLiLibConfigs.Generic.OPTION_LIST_CONFIG_DROPDOWN.addValueChangeListener(GuiUtils::reInitCurrentScreen);
        MaLiLibConfigs.Generic.CUSTOM_SCREEN_SCALE.addValueChangeListener(BaseScreen::applyCustomScreenScaleChange);
        MaLiLibConfigs.Generic.SERVER_MESSAGES.setValueChangeCallback((n, o) -> MessagePacketHandler.updateRegistration(n));
        MaLiLibConfigs.Generic.SERVER_MESSAGES.setValueLoadCallback(MessagePacketHandler::updateRegistration);

        Registry.RENDER_EVENT_DISPATCHER.registerGameOverlayRenderer(Registry.INFO_OVERLAY);
        Registry.RENDER_EVENT_DISPATCHER.registerScreenPostRenderer(Registry.INFO_OVERLAY);
        Registry.TICK_EVENT_DISPATCHER.registerClientTickHandler(Registry.INFO_OVERLAY);

        MaLiLibActions.init();
    }
}
