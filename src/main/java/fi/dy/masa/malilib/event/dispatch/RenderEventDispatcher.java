package fi.dy.masa.malilib.event.dispatch;

import fi.dy.masa.malilib.event.PostGameOverlayRenderer;
import fi.dy.masa.malilib.event.PostItemTooltipRenderer;
import fi.dy.masa.malilib.event.PostScreenRenderer;
import fi.dy.masa.malilib.event.PostWorldRenderer;

public interface RenderEventDispatcher
{
    RenderEventDispatcher INSTANCE = new RenderEventDispatcherImpl();

    /**
     * Registers a renderer which will have its {@link PostGameOverlayRenderer#onPostGameOverlayRender(net.minecraft.client.Minecraft, float)}
     * method called after the vanilla game overlay rendering is done.
     * @param renderer
     */
    void registerGameOverlayRenderer(PostGameOverlayRenderer renderer);

    /**
     * Registers a renderer which will have its {@link PostItemTooltipRenderer#onPostRenderItemTooltip(net.minecraft.item.ItemStack, int, int, net.minecraft.client.Minecraft)}
     * method called after the vanilla ItemStack tooltip text has been rendered.
     * @param renderer
     */
    void registerTooltipPostRenderer(PostItemTooltipRenderer renderer);

    /**
     * Registers a renderer which will have its {@link PostScreenRenderer#onPostScreenRender(net.minecraft.client.Minecraft, float)}
     * method called after the vanilla screen rendering method has been called.
     * @param renderer
     */
    void registerScreenPostRenderer(PostScreenRenderer renderer);

    /**
     * Registers a renderer which will have its {@link PostWorldRenderer#onPostWorldRender(net.minecraft.client.Minecraft, float)}
     * method called after the vanilla world rendering is done.
     * @param renderer
     */
    void registerWorldPostRenderer(PostWorldRenderer renderer);
}
