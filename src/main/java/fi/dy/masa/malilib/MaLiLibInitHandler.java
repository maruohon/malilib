package fi.dy.masa.malilib;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.dispatch.InputDispatcherImpl;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.input.callback.HotkeyCallback;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyAction;

public class MaLiLibInitHandler implements InitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        ConfigManager.INSTANCE.registerConfigHandler(new MaLiLibConfigs());
        InputDispatcherImpl.getKeyBindManager().registerKeyBindProvider(MaLiLibInputHandler.getInstance());

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
