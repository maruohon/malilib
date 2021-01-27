package fi.dy.masa.malilib;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.JsonModConfig;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.dispatch.KeyBindManager;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.ConfigTabRegistry;

public class MaLiLibInitHandler implements InitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        ConfigManager.INSTANCE.registerConfigHandler(new JsonModConfig(MaLiLibReference.MOD_ID, MaLiLibReference.MOD_NAME, MaLiLibConfigs.CATEGORIES, 1));
        KeyBindManager.INSTANCE.registerKeyBindProvider(MaLiLibKeyBindProvider.INSTANCE);

        ConfigTabRegistry.INSTANCE.registerConfigTabProvider(MaLiLibReference.MOD_ID, MaLiLibConfigScreen::getConfigTabs);

        MaLiLibConfigs.Generic.OPEN_GUI_CONFIGS.getKeyBind().setCallback((a, k) -> BaseScreen.openGui(MaLiLibConfigScreen.create()));
    }
}
