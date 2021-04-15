package fi.dy.masa.malilib;

import fi.dy.masa.malilib.config.BaseModConfig;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.dispatch.RenderEventDispatcher;
import fi.dy.masa.malilib.event.dispatch.TickEventDispatcher;
import fi.dy.masa.malilib.gui.config.ConfigTabRegistry;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.input.HotkeyManager;
import fi.dy.masa.malilib.input.callback.HotkeyCallback;
import fi.dy.masa.malilib.network.message.MessagePacketHandler;
import fi.dy.masa.malilib.overlay.InfoOverlay;

public class MaLiLibInitHandler implements InitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        ConfigManager.INSTANCE.registerConfigHandler(BaseModConfig.createDefaultModConfig(MaLiLibReference.MOD_INFO, MaLiLibConfigs.CONFIG_VERSION, MaLiLibConfigs.CATEGORIES));
        ConfigTabRegistry.INSTANCE.registerConfigTabProvider(MaLiLibReference.MOD_INFO, MaLiLibConfigScreen::getConfigTabs);

        HotkeyManager.INSTANCE.registerHotkeyProvider(MaLiLibHotkeyProvider.INSTANCE);

        MaLiLibConfigs.Hotkeys.OPEN_CONFIG_SCREEN.getKeyBind().setCallback(HotkeyCallback.of(MaLiLibActions.OPEN_CONFIG_SCREEN));
        MaLiLibConfigs.Hotkeys.SCROLL_VALUE_ADJUST_DECREASE.getKeyBind().setCallback(HotkeyCallback.of(MaLiLibActions.SCROLL_VALUE_ADJUST_DECREASE.getAction()));
        MaLiLibConfigs.Hotkeys.SCROLL_VALUE_ADJUST_INCREASE.getKeyBind().setCallback(HotkeyCallback.of(MaLiLibActions.SCROLL_VALUE_ADJUST_INCREASE.getAction()));

        MaLiLibConfigs.Generic.OPTION_LIST_CONFIG_DROPDOWN.addValueChangeListener(GuiUtils::reInitCurrentScreen);
        MaLiLibConfigs.Generic.SERVER_MESSAGES.setValueChangeCallback((n, o) -> MessagePacketHandler.updateRegistration(n));
        MaLiLibConfigs.Generic.SERVER_MESSAGES.setValueLoadCallback(MessagePacketHandler::updateRegistration);

        RenderEventDispatcher.INSTANCE.registerGameOverlayRenderer(InfoOverlay.INSTANCE);
        RenderEventDispatcher.INSTANCE.registerScreenPostRenderer(InfoOverlay.INSTANCE);
        TickEventDispatcher.INSTANCE.registerClientTickHandler(InfoOverlay.INSTANCE);
    }
}
