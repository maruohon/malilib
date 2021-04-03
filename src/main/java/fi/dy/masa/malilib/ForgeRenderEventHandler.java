package fi.dy.masa.malilib;

import fi.dy.masa.malilib.event.RenderEventHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

class ForgeRenderEventHandler
{
    @SubscribeEvent
    public void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event)
    {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL)
        {
            ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderGameOverlayPost(net.minecraft.client.Minecraft.getInstance(), event.getPartialTicks(), event.getMatrixStack());
        }
    }

    @SubscribeEvent
    public void onRenderTooltipPost(RenderTooltipEvent.Pre event)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipLast(event.getStack(), event.getX(), event.getY());
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldLast(event.getMatrixStack(), net.minecraft.client.Minecraft.getInstance(), event.getPartialTicks());
    }
}
