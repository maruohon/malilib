package fi.dy.masa.malilib;

import fi.dy.masa.malilib.config.BaseModConfig;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.input.HotkeyManager;
import fi.dy.masa.malilib.event.dispatch.RenderEventDispatcher;
import fi.dy.masa.malilib.event.dispatch.TickEventDispatcher;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.ConfigTabRegistry;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.overlay.InfoOverlay;

public class MaLiLibInitHandler implements InitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        ConfigManager.INSTANCE.registerConfigHandler(BaseModConfig.createDefaultModConfig(MaLiLibReference.MOD_INFO, 1, MaLiLibConfigs.CATEGORIES));
        ConfigTabRegistry.INSTANCE.registerConfigTabProvider(MaLiLibReference.MOD_INFO, MaLiLibConfigScreen::getConfigTabs);

        HotkeyManager.INSTANCE.registerHotkeyProvider(MaLiLibHotkeyProvider.INSTANCE);

        MaLiLibConfigs.Generic.OPEN_GUI_CONFIGS.getKeyBind().setCallback((a, k) -> BaseScreen.openScreen(MaLiLibConfigScreen.create(null)));
        MaLiLibConfigs.Generic.OPTION_LIST_CONFIG_DROPDOWN.setValueChangeCallback((n, o) -> GuiUtils.reInitCurrentScreen());

        RenderEventDispatcher.INSTANCE.registerGameOverlayRenderer(InfoOverlay.INSTANCE);
        TickEventDispatcher.INSTANCE.registerClientTickHandler(InfoOverlay.INSTANCE);
    }
}
