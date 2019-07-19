package fi.dy.masa.malilib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.forge.ForgeInputEventHandler;
import fi.dy.masa.malilib.event.forge.ForgeRenderEventHandler;
import fi.dy.masa.malilib.event.forge.ForgeTickEventHandler;

@Mod(modid = MaLiLibReference.MOD_ID, name = MaLiLibReference.MOD_NAME, version = MaLiLibReference.MOD_VERSION,
    certificateFingerprint = MaLiLibReference.FINGERPRINT,
    guiFactory = "fi.dy.masa.malilib.MaLiLibGuiFactory",
    updateJSON = "https://raw.githubusercontent.com/maruohon/malilib/forge_1.12.2/update.json",
    acceptedMinecraftVersions = "1.12.2")
public class MaLiLib
{
    @Mod.Instance(MaLiLibReference.MOD_ID)
    public static MaLiLib instance;

    public static final Logger logger = LogManager.getLogger(MaLiLibReference.MOD_ID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        InitializationHandler.getInstance().registerInitializationHandler(new MaLiLibInitHandler());

        MinecraftForge.EVENT_BUS.register(new ForgeInputEventHandler());
        MinecraftForge.EVENT_BUS.register(new ForgeRenderEventHandler());
        MinecraftForge.EVENT_BUS.register(new ForgeTickEventHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        ((InitializationHandler) InitializationHandler.getInstance()).onGameInitDone();
    }

    @Mod.EventHandler
    public void onFingerPrintViolation(FMLFingerprintViolationEvent event)
    {
        // Not running in a dev environment
        if (event.isDirectory() == false)
        {
            logger.warn("*********************************************************************************************");
            logger.warn("*****                                    WARNING                                        *****");
            logger.warn("*****                                                                                   *****");
            logger.warn("*****   The signature of the mod file '{}' does not match the expected fingerprint!     *****", event.getSource().getName());
            logger.warn("*****   This might mean that the mod file has been tampered with!                       *****");
            logger.warn("*****   If you did not download the mod {} directly from Curse/CurseForge,       *****", MaLiLibReference.MOD_NAME);
            logger.warn("*****   or using one of the well known launchers, and you did not                       *****");
            logger.warn("*****   modify the mod file at all yourself, then it's possible,                        *****");
            logger.warn("*****   that it may contain malware or other unwanted things!                           *****");
            logger.warn("*********************************************************************************************");
        }
    }
}
