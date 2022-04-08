package fi.dy.masa.malilib;

import fi.dy.masa.malilib.config.BaseModConfig;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.input.CustomHotkeyManager;
import fi.dy.masa.malilib.network.message.ConfigOverridePacketHandler;
import fi.dy.masa.malilib.overlay.widget.ConfigStatusIndicatorContainerWidget;
import fi.dy.masa.malilib.registry.Registry;

public class MaLiLibInitHandler implements InitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        Registry.CONFIG_MANAGER.registerConfigHandler(BaseModConfig.createDefaultModConfig(MaLiLibReference.MOD_INFO, MaLiLibConfigs.CONFIG_VERSION, MaLiLibConfigs.CATEGORIES));
        Registry.CONFIG_SCREEN.registerConfigScreenFactory(MaLiLibReference.MOD_INFO, MaLiLibConfigScreen::create);
        Registry.CONFIG_TAB.registerConfigTabProvider(MaLiLibReference.MOD_INFO, MaLiLibConfigScreen::getConfigTabs);

        Registry.HOTKEY_MANAGER.registerHotkeyProvider(MaLiLibHotkeyProvider.INSTANCE);
        Registry.HOTKEY_MANAGER.registerHotkeyProvider(CustomHotkeyManager.INSTANCE);
        Registry.HOTKEY_MANAGER.registerHotkeyProvider(ConfigStatusIndicatorContainerWidget.getHotkeyProvider());

        Registry.RENDER_EVENT_DISPATCHER.registerGameOverlayRenderer(Registry.INFO_OVERLAY);
        Registry.RENDER_EVENT_DISPATCHER.registerScreenPostRenderer(Registry.INFO_OVERLAY);
        Registry.TICK_EVENT_DISPATCHER.registerClientTickHandler(Registry.INFO_OVERLAY);

        MaLiLibConfigInit.init();
        MaLiLibActions.init();

        ConfigOverridePacketHandler.updateRegistration(true);
    }
}
