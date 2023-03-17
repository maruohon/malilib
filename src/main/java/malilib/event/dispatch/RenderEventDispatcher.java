package malilib.event.dispatch;

import malilib.event.PostGameOverlayRenderer;
import malilib.event.PostItemTooltipRenderer;
import malilib.event.PostScreenRenderer;
import malilib.event.PostWorldRenderer;

public interface RenderEventDispatcher
{
    /**
     * Registers a renderer which will have its {@link PostGameOverlayRenderer#onPostGameOverlayRender(net.minecraft.client.util.math.MatrixStack)}
     * method called after the vanilla game overlay rendering is done.
     * @param renderer
     */
    void registerGameOverlayRenderer(PostGameOverlayRenderer renderer);

    /**
     * Registers a renderer which will have its {@link PostItemTooltipRenderer#onPostRenderItemTooltip(net.minecraft.item.ItemStack, int, int, net.minecraft.client.util.math.MatrixStack)}
     * method called after the vanilla ItemStack tooltip text has been rendered.
     * @param renderer
     */
    void registerTooltipPostRenderer(PostItemTooltipRenderer renderer);

    /**
     * Registers a renderer which will have its {@link PostScreenRenderer#onPostScreenRender(net.minecraft.client.util.math.MatrixStack, float)}
     * method called after the vanilla screen rendering method has been called.
     * @param renderer
     */
    void registerScreenPostRenderer(PostScreenRenderer renderer);

    /**
     * Registers a renderer which will have its {@link PostWorldRenderer#onPostWorldRender(net.minecraft.client.util.math.MatrixStack, net.minecraft.util.math.Matrix4f, float)}
     * method called after the vanilla world rendering is done.
     * @param renderer
     */
    void registerWorldPostRenderer(PostWorldRenderer renderer);
}
