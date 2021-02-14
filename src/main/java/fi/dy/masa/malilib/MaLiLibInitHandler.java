package fi.dy.masa.malilib;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.JsonModConfig;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.dispatch.KeyBindManager;
import fi.dy.masa.malilib.event.dispatch.RenderEventDispatcher;
import fi.dy.masa.malilib.event.dispatch.TickEventDispatcher;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.ConfigTabRegistry;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.message.InfoOverlay;

public class MaLiLibInitHandler implements InitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        ConfigManager.INSTANCE.registerConfigHandler(new JsonModConfig(MaLiLibReference.MOD_ID, MaLiLibReference.MOD_NAME, MaLiLibConfigs.CATEGORIES, 1));
        KeyBindManager.INSTANCE.registerKeyBindProvider(MaLiLibKeyBindProvider.INSTANCE);

        ConfigTabRegistry.INSTANCE.registerConfigTabProvider(MaLiLibReference.MOD_ID, MaLiLibConfigScreen::getConfigTabs);

        MaLiLibConfigs.Generic.OPEN_GUI_CONFIGS.getKeyBind().setCallback((a, k) -> BaseScreen.openGui(MaLiLibConfigScreen.create()));
        MaLiLibConfigs.Generic.OPTION_LIST_CONFIG_DROPDOWN.setValueChangeCallback((n, o) -> GuiUtils.reInitCurrentScreen());

        RenderEventDispatcher.INSTANCE.registerGameOverlayRenderer((mc, pt) -> InfoOverlay.INSTANCE.render());
        TickEventDispatcher.INSTANCE.registerClientTickHandler(InfoOverlay.INSTANCE::tick);
    }
}
