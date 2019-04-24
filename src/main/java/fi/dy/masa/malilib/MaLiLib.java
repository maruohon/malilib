package fi.dy.masa.malilib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import fi.dy.masa.malilib.reference.MaLiLibReference;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;

public class MaLiLib implements ModInitializer
{
    public static final Logger logger = LogManager.getLogger(MaLiLibReference.MOD_ID);

    @Override
    public void onInitialize()
    {
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
    }

    private static class InitHandler implements IInitializationHandler
    {
        @Override
        public void registerModHandlers()
        {
            ConfigManager.getInstance().registerConfigHandler(MaLiLibReference.MOD_ID, new MaLiLibConfigs());
            InputEventHandler.getInstance().registerKeybindProvider(MaLiLibInputHandler.getInstance());

            MaLiLibConfigs.Generic.OPEN_GUI_CONFIGS.getKeybind().setCallback(new CallbackOpenConfigGui());
        }

        private static class CallbackOpenConfigGui implements IHotkeyCallback
        {
            @Override
            public boolean onKeyAction(KeyAction action, IKeybind key)
            {
                MinecraftClient.getInstance().openScreen(new MaLiLibConfigGui());
                return true;
            }
        }
    }
}
