package fi.dy.masa.malilib.event.dispatch;

import fi.dy.masa.malilib.event.IPostGameOverlayRenderer;
import fi.dy.masa.malilib.event.IPostItemTooltipRenderer;
import fi.dy.masa.malilib.event.IPostWorldRenderer;

public interface IRenderEventDispatcher
{
    /**
     * Registers a renderer which will have its {@link IPostGameOverlayRenderer#onPostGameOverlayRender(float)}
     * method called after the vanilla game overlay rendering is done.
     * @param renderer
     */
    void registerGameOverlayRenderer(IPostGameOverlayRenderer renderer);

    /**
     * Registers a renderer which will have its {@link IPostItemTooltipRenderer#onPostRenderItemTooltip(net.minecraft.item.ItemStack, int, int)}
     * method called after the vanilla ItemStack tooltip text has been rendered.
     * @param renderer
     */
    void registerTooltipPostRenderer(IPostItemTooltipRenderer renderer);

    /**
     * Registers a renderer which will have its {@link IPostWorldRenderer#onPostWorldRender(float)}
     * method called after the vanilla world rendering is done.
     * @param renderer
     */
    void registerWorldPostRenderer(IPostWorldRenderer renderer);
}
