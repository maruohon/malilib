package fi.dy.masa.malilib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.rift.listener.client.OverlayRenderer;
import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import net.minecraft.client.Minecraft;

public class MaLiLib implements InitializationListener, OverlayRenderer
{
    public static final Logger logger = LogManager.getLogger(MaLiLibReference.MOD_ID);

    @Override
    public void onInitialization()
    {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.malilib.json");

        InitializationHandler.getInstance().registerInitializationHandler(new MaLiLibInitHandler());
    }

    @Override
    public void renderOverlay()
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderGameOverlayPost(Minecraft.getInstance().getRenderPartialTicks());
    }
}
