package fi.dy.masa.malilib.event.dispatch;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import fi.dy.masa.malilib.event.PostGameOverlayRenderer;
import fi.dy.masa.malilib.event.PostItemTooltipRenderer;
import fi.dy.masa.malilib.event.PostScreenRenderer;
import fi.dy.masa.malilib.event.PostWorldRenderer;
import fi.dy.masa.malilib.render.overlay.OverlayRendererContainer;
import fi.dy.masa.malilib.util.game.wrap.GameUtils;

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
    public void onRenderGameOverlayPost()
    {
        if (this.overlayRenderers.isEmpty() == false)
        {
            GameUtils.profilerPush("malilib_game_overlay_post");

            for (PostGameOverlayRenderer renderer : this.overlayRenderers)
            {
                GameUtils.profilerPush(renderer.getProfilerSectionSupplier());
                renderer.onPostGameOverlayRender();
                GameUtils.profilerPop();
            }

            GameUtils.profilerPop();
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onRenderScreenPost(float tickDelta)
    {
        if (this.screenPostRenderers.isEmpty() == false)
        {
            GameUtils.profilerPush("malilib_screen_post");

            for (PostScreenRenderer renderer : this.screenPostRenderers)
            {
                GameUtils.profilerPush(renderer.getProfilerSectionSupplier());
                renderer.onPostScreenRender(tickDelta);
                GameUtils.profilerPop();
            }

            GameUtils.profilerPop();
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onRenderTooltipPost(ItemStack stack, int x, int y)
    {
        if (this.tooltipLastRenderers.isEmpty() == false)
        {
            GameUtils.profilerPush("malilib_tooltip_post");

            for (PostItemTooltipRenderer renderer : this.tooltipLastRenderers)
            {
                GameUtils.profilerPush(renderer.getProfilerSectionSupplier());
                renderer.onPostRenderItemTooltip(stack, x, y);
                GameUtils.profilerPop();
            }

            GameUtils.profilerPop();
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onRenderWorldLast(float tickDelta)
    {
        GameUtils.profilerPush("malilib_world_post");

        GameUtils.profilerPush("overlays");
        OverlayRendererContainer.INSTANCE.render(tickDelta);
        GameUtils.profilerPop();

        if (this.worldLastRenderers.isEmpty() == false)
        {

            for (PostWorldRenderer renderer : this.worldLastRenderers)
            {
                GameUtils.profilerPush(renderer.getProfilerSectionSupplier());
                renderer.onPostWorldRender(tickDelta);
                GameUtils.profilerPop();
            }

        }

        GameUtils.profilerPop();
    }
}
