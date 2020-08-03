package fi.dy.masa.malilib;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.IInitializationHandler;
import fi.dy.masa.malilib.event.dispatch.InputEventDispatcher;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.input.IHotkeyCallback;
import fi.dy.masa.malilib.input.IKeyBind;
import fi.dy.masa.malilib.input.KeyAction;

public class MaLiLibInitHandler implements IInitializationHandler
{
    @Override
    public void registerModHandlers()
    {
        ConfigManager.INSTANCE.registerConfigHandler(new MaLiLibConfigs());
        InputEventDispatcher.getKeyBindManager().registerKeyBindProvider(MaLiLibInputHandler.getInstance());

        MaLiLibConfigs.Generic.OPEN_GUI_CONFIGS.getKeyBind().setCallback(new CallbackOpenConfigGui());
    }

    private static class CallbackOpenConfigGui implements IHotkeyCallback
    {
        @Override
        public boolean onKeyAction(KeyAction action, IKeyBind key)
        {
            BaseScreen.openGui(new MaLiLibConfigGui());
            return true;
        }
    }
}
