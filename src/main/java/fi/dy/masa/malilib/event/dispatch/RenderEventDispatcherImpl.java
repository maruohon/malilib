package fi.dy.masa.malilib.event.dispatch;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import fi.dy.masa.malilib.event.PostGameOverlayRenderer;
import fi.dy.masa.malilib.event.PostItemTooltipRenderer;
import fi.dy.masa.malilib.event.PostScreenRenderer;
import fi.dy.masa.malilib.event.PostWorldRenderer;
import fi.dy.masa.malilib.render.overlay.OverlayRendererContainer;

public class RenderEventDispatcherImpl implements RenderEventDispatcher
{
    private final List<PostGameOverlayRenderer> overlayRenderers = new ArrayList<>();
    private final List<PostScreenRenderer> screenPostRenderers = new ArrayList<>();
    private final List<PostItemTooltipRenderer> tooltipLastRenderers = new ArrayList<>();
    private final List<PostWorldRenderer> worldLastRenderers = new ArrayList<>();

    @Override
    public void registerGameOverlayRenderer(PostGameOverlayRenderer renderer)
    {
        if (this.overlayRenderers.contains(renderer) == false)
        {
            this.overlayRenderers.add(renderer);
        }
    }

    @Override
    public void registerScreenPostRenderer(PostScreenRenderer renderer)
    {
        if (this.screenPostRenderers.contains(renderer) == false)
        {
            this.screenPostRenderers.add(renderer);
        }
    }

    @Override
    public void registerTooltipPostRenderer(PostItemTooltipRenderer renderer)
    {
        if (this.tooltipLastRenderers.contains(renderer) == false)
        {
            this.tooltipLastRenderers.add(renderer);
        }
    }

    @Override
    public void registerWorldPostRenderer(PostWorldRenderer renderer)
    {
        if (this.worldLastRenderers.contains(renderer) == false)
        {
            this.worldLastRenderers.add(renderer);
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onRenderGameOverlayPost(Minecraft mc, float partialTicks)
    {
        if (this.overlayRenderers.isEmpty() == false)
        {
            mc.profiler.startSection("malilib_game_overlay_last");

            for (PostGameOverlayRenderer renderer : this.overlayRenderers)
            {
                mc.profiler.func_194340_a(renderer.getProfilerSectionSupplier());
                renderer.onPostGameOverlayRender(mc, partialTicks);
                mc.profiler.endSection();
            }

            mc.profiler.endSection();
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onRenderScreenPost(Minecraft mc, float partialTicks)
    {
        if (this.screenPostRenderers.isEmpty() == false)
        {
            mc.profiler.startSection("malilib_screen_post");

            for (PostScreenRenderer renderer : this.screenPostRenderers)
            {
                mc.profiler.func_194340_a(renderer.getProfilerSectionSupplier());
                renderer.onPostScreenRender(mc, partialTicks);
                mc.profiler.endSection();
            }

            mc.profiler.endSection();
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onRenderTooltipPost(ItemStack stack, int x, int y, Minecraft mc)
    {
        if (this.tooltipLastRenderers.isEmpty() == false)
        {
            mc.profiler.startSection("malilib_tooltip_last");

            for (PostItemTooltipRenderer renderer : this.tooltipLastRenderers)
            {
                mc.profiler.func_194340_a(renderer.getProfilerSectionSupplier());
                renderer.onPostRenderItemTooltip(stack, x, y, mc);
                mc.profiler.endSection();
            }

            mc.profiler.endSection();
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onRenderWorldLast(Minecraft mc, float partialTicks)
    {
        mc.profiler.startSection("malilib_world_last");

        mc.profiler.startSection("overlays");
        OverlayRendererContainer.INSTANCE.render(mc, partialTicks);
        mc.profiler.endSection();

        if (this.worldLastRenderers.isEmpty() == false)
        {

            for (PostWorldRenderer renderer : this.worldLastRenderers)
            {
                mc.profiler.func_194340_a(renderer.getProfilerSectionSupplier());
                renderer.onPostWorldRender(mc, partialTicks);
                mc.profiler.endSection();
            }

        }

        mc.profiler.endSection();
    }
}
