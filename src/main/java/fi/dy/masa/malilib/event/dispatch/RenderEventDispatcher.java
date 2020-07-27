package fi.dy.masa.malilib.event.dispatch;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import fi.dy.masa.malilib.event.IPostGameOverlayRenderer;
import fi.dy.masa.malilib.event.IPostItemTooltipRenderer;
import fi.dy.masa.malilib.event.IPostWorldRenderer;
import fi.dy.masa.malilib.message.MessageUtils;
import fi.dy.masa.malilib.render.ToastRenderer;

public class RenderEventDispatcher implements IRenderEventDispatcher
{
    public static final IRenderEventDispatcher INSTANCE = new RenderEventDispatcher();

    private final List<IPostGameOverlayRenderer> overlayRenderers = new ArrayList<>();
    private final List<IPostItemTooltipRenderer> tooltipLastRenderers = new ArrayList<>();
    private final List<IPostWorldRenderer> worldLastRenderers = new ArrayList<>();

    @Override
    public void registerGameOverlayRenderer(IPostGameOverlayRenderer renderer)
    {
        if (this.overlayRenderers.contains(renderer) == false)
        {
            this.overlayRenderers.add(renderer);
        }
    }

    @Override
    public void registerTooltipPostRenderer(IPostItemTooltipRenderer renderer)
    {
        if (this.tooltipLastRenderers.contains(renderer) == false)
        {
            this.tooltipLastRenderers.add(renderer);
        }
    }

    @Override
    public void registerWorldPostRenderer(IPostWorldRenderer renderer)
    {
        if (this.worldLastRenderers.contains(renderer) == false)
        {
            this.worldLastRenderers.add(renderer);
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onRenderGameOverlayPost(float partialTicks)
    {
        if (this.overlayRenderers.isEmpty() == false)
        {
            for (IPostGameOverlayRenderer renderer : this.overlayRenderers)
            {
                renderer.onPostGameOverlayRender(partialTicks);
            }
        }

        MessageUtils.renderInGameMessages();
        ToastRenderer.INSTANCE.render();
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onRenderTooltipPost(ItemStack stack, int x, int y)
    {
        if (this.tooltipLastRenderers.isEmpty() == false)
        {
            for (IPostItemTooltipRenderer renderer : this.tooltipLastRenderers)
            {
                renderer.onPostRenderItemTooltip(stack, x, y);
            }
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onRenderWorldPost(float partialTicks)
    {
        if (this.worldLastRenderers.isEmpty() == false)
        {
            for (IPostWorldRenderer renderer : this.worldLastRenderers)
            {
                renderer.onPostWorldRender(partialTicks);
            }
        }
    }
}
