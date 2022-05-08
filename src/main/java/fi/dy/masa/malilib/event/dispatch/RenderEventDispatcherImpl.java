package fi.dy.masa.malilib.event.dispatch;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.profiler.Profiler;
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
    public void onRenderGameOverlayPost(MinecraftClient mc)
    {
        if (this.overlayRenderers.isEmpty() == false)
        {
            Profiler profiler = mc.getProfiler();
            profiler.push("malilib_game_overlay_last");

            for (PostGameOverlayRenderer renderer : this.overlayRenderers)
            {
                profiler.push(renderer.getProfilerSectionSupplier());
                renderer.onPostGameOverlayRender();
                profiler.pop();
            }

            profiler.pop();
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onRenderScreenPost(MinecraftClient mc, float partialTicks)
    {
        if (this.screenPostRenderers.isEmpty() == false)
        {
            Profiler profiler = mc.getProfiler();
            profiler.push("malilib_screen_post");

            for (PostScreenRenderer renderer : this.screenPostRenderers)
            {
                profiler.push(renderer.getProfilerSectionSupplier());
                renderer.onPostScreenRender(mc, partialTicks);
                profiler.pop();
            }

            profiler.pop();
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onRenderTooltipPost(ItemStack stack, int x, int y, MinecraftClient mc)
    {
        if (this.tooltipLastRenderers.isEmpty() == false)
        {
            Profiler profiler = mc.getProfiler();
            profiler.push("malilib_tooltip_last");

            for (PostItemTooltipRenderer renderer : this.tooltipLastRenderers)
            {
                profiler.push(renderer.getProfilerSectionSupplier());
                renderer.onPostRenderItemTooltip(stack, x, y, mc);
                profiler.pop();
            }

            profiler.pop();
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onRenderWorldLast(MinecraftClient mc, float partialTicks)
    {
        Profiler profiler = mc.getProfiler();
        profiler.push("malilib_world_last");

        profiler.push("overlays");
        OverlayRendererContainer.INSTANCE.render(mc, partialTicks);
        profiler.pop();

        if (this.worldLastRenderers.isEmpty() == false)
        {

            for (PostWorldRenderer renderer : this.worldLastRenderers)
            {
                profiler.push(renderer.getProfilerSectionSupplier());
                renderer.onPostWorldRender(mc, partialTicks);
                profiler.pop();
            }

        }

        profiler.pop();
    }
}
