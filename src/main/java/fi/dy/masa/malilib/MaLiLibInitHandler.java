package fi.dy.masa.malilib;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.dispatch.KeyBindManager;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.ConfigTabRegistry;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.callback.HotkeyCallback;

public class MaLiLibInitHandler implements InitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        ConfigManager.INSTANCE.registerConfigHandler(new MaLiLibConfigs());
        ConfigTabRegistry.INSTANCE.registerConfigTabProvider(MaLiLibReference.MOD_ID, MaLiLibConfigScreen::getConfigTabs);
        KeyBindManager.INSTANCE.registerKeyBindProvider(MaLiLibKeyBindProvider.INSTANCE);

        MaLiLibConfigs.Generic.OPEN_GUI_CONFIGS.getKeyBind().setCallback(new CallbackOpenConfigGui());
    }

    private static class CallbackOpenConfigGui implements HotkeyCallback
    {
        @Override
        public boolean onKeyAction(KeyAction action, KeyBind key)
        {
            BaseScreen.openGui(new MaLiLibConfigScreen());
            return true;
        }
    }
}
