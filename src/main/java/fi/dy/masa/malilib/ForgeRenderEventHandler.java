package fi.dy.masa.malilib;

import fi.dy.masa.malilib.event.RenderEventHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

class ForgeRenderEventHandler
{
    @SubscribeEvent
    public void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event)
    {
        if (event.getType() == ElementType.ALL)
        {
            ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderGameOverlayPost(event.getPartialTicks());
        }
    }

    @SubscribeEvent
    public void onRenderTooltipPost(RenderTooltipEvent.PostText event)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipLast(event.getStack(), event.getX(), event.getY());
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldLast(event.getPartialTicks());
    }
}
