package malilib.event.dispatch;

import java.util.ArrayList;
import java.util.List;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.world.item.ItemStack;

import malilib.event.PostGameOverlayRenderer;
import malilib.event.PostItemTooltipRenderer;
import malilib.event.PostScreenRenderer;
import malilib.event.PostWorldRenderer;
import malilib.render.RenderContext;
import malilib.render.overlay.OverlayRendererContainer;
import malilib.util.game.wrap.GameUtils;

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
    public void onRenderGameOverlayPost(PoseStack matrices)
    {
        if (this.overlayRenderers.isEmpty() == false)
        {
            GameUtils.profilerPush("malilib_game_overlay_post");
            RenderContext ctx = new RenderContext(matrices);

            for (PostGameOverlayRenderer renderer : this.overlayRenderers)
            {
                GameUtils.profilerPush(renderer.getProfilerSectionSupplier());
                renderer.onPostGameOverlayRender(ctx);
                GameUtils.profilerPop();
            }

            GameUtils.profilerPop();
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onRenderScreenPost(PoseStack matrices, float tickDelta)
    {
        if (this.screenPostRenderers.isEmpty() == false)
        {
            GameUtils.profilerPush("malilib_screen_post");
            RenderContext ctx = new RenderContext(matrices);

            for (PostScreenRenderer renderer : this.screenPostRenderers)
            {
                GameUtils.profilerPush(renderer.getProfilerSectionSupplier());
                renderer.onPostScreenRender(ctx, tickDelta);
                GameUtils.profilerPop();
            }

            GameUtils.profilerPop();
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onRenderTooltipPost(ItemStack stack, int x, int y, PoseStack matrices)
    {
        if (this.tooltipLastRenderers.isEmpty() == false)
        {
            GameUtils.profilerPush("malilib_tooltip_post");
            RenderContext ctx = new RenderContext(matrices);

            for (PostItemTooltipRenderer renderer : this.tooltipLastRenderers)
            {
                GameUtils.profilerPush(renderer.getProfilerSectionSupplier());
                renderer.onPostRenderItemTooltip(stack, x, y, ctx);
                GameUtils.profilerPop();
            }

            GameUtils.profilerPop();
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onRenderWorldLast(PoseStack matrices, Matrix4f projMatrix, float tickDelta)
    {
        GameUtils.profilerPush("malilib_world_post");

        GameUtils.profilerPush("overlays");
        OverlayRendererContainer.INSTANCE.render(matrices, projMatrix, tickDelta);
        GameUtils.profilerPop();

        if (this.worldLastRenderers.isEmpty() == false)
        {
            RenderContext ctx = new RenderContext(matrices, projMatrix);

            for (PostWorldRenderer renderer : this.worldLastRenderers)
            {
                GameUtils.profilerPush(renderer.getProfilerSectionSupplier());
                renderer.onPostWorldRender(ctx, tickDelta);
                GameUtils.profilerPop();
            }
        }

        GameUtils.profilerPop();
    }
}
