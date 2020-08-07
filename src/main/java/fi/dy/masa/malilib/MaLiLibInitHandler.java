package fi.dy.masa.malilib;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.dispatch.KeyBindManager;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.callback.HotkeyCallback;

public class MaLiLibInitHandler implements InitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        ConfigManager.INSTANCE.registerConfigHandler(new MaLiLibConfigs());
        KeyBindManager.INSTANCE.registerKeyBindProvider(MaLiLibInputHandler.getInstance());

        MaLiLibConfigs.Generic.OPEN_GUI_CONFIGS.getKeyBind().setCallback(new CallbackOpenConfigGui());
    }

    private static class CallbackOpenConfigGui implements HotkeyCallback
    {
        @Override
        public boolean onKeyAction(KeyAction action, KeyBind key)
        {
            BaseScreen.openGui(new MaLiLibConfigGui());
            return true;
        }
    }
}
