package fi.dy.masa.malilib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MaLiLibReference.MOD_ID)
public class MaLiLib
{
    public static final Logger logger = LogManager.getLogger(MaLiLibReference.MOD_ID);

    public MaLiLib()

    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);

        MinecraftForge.EVENT_BUS.register(new ForgeInputEventHandler());
        MinecraftForge.EVENT_BUS.register(new ForgeRenderEventHandler());
        MinecraftForge.EVENT_BUS.register(new ForgeTickEventHandler());
        MinecraftForge.EVENT_BUS.register(new ForgeWorldEventHandler());

        InitializationHandler.getInstance().registerInitializationHandler(new MaLiLibInitHandler());
    }

    private void onClientSetup(final FMLClientSetupEvent event)

    {
        ((InitializationHandler) InitializationHandler.getInstance()).onGameInitDone();
    }
}