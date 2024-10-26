package fi.dy.masa.malilib.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.profiler.Profilers;
import org.joml.Matrix4f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import fi.dy.masa.malilib.interfaces.IRenderDispatcher;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.util.InfoUtils;

public class RenderEventHandler implements IRenderDispatcher
{
    private static final RenderEventHandler INSTANCE = new RenderEventHandler();

    private final List<IRenderer> overlayRenderers = new ArrayList<>();
    private final List<IRenderer> tooltipLastRenderers = new ArrayList<>();
    private final List<IRenderer> worldLastRenderers = new ArrayList<>();

    public static IRenderDispatcher getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void registerGameOverlayRenderer(IRenderer renderer)
    {
        if (this.overlayRenderers.contains(renderer) == false)
        {
            this.overlayRenderers.add(renderer);
        }
    }

    @Override
    public void registerTooltipLastRenderer(IRenderer renderer)
    {
        if (this.tooltipLastRenderers.contains(renderer) == false)
        {
            this.tooltipLastRenderers.add(renderer);
        }
    }

    @Override
    public void registerWorldLastRenderer(IRenderer renderer)
    {
        if (this.worldLastRenderers.contains(renderer) == false)
        {
            this.worldLastRenderers.add(renderer);
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onRenderGameOverlayPost(DrawContext drawContext)
    {
        Profilers.get().push("malilib_rendergameoverlaypost");

        if (this.overlayRenderers.isEmpty() == false)
        {
            for (IRenderer renderer : this.overlayRenderers)
            {
                Profilers.get().push(renderer.getProfilerSectionSupplier());
                renderer.onRenderGameOverlayPost(drawContext);
                Profilers.get().pop();
            }
        }

        Profilers.get().push("malilib_ingamemessages");
        InfoUtils.renderInGameMessages(drawContext);
        Profilers.get().pop();

        Profilers.get().pop();
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onRenderTooltipLast(DrawContext drawContext, ItemStack stack, int x, int y)
    {
        if (this.tooltipLastRenderers.isEmpty() == false)
        {
            for (IRenderer renderer : this.tooltipLastRenderers)
            {
                renderer.onRenderTooltipLast(drawContext ,stack, x, y);
            }
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     * --> Changed to Matrix4f for all Downstream Mods.
     */
    public void onRenderWorldLast(Matrix4f matrix4f, Matrix4f projMatrix, MinecraftClient mc)
    {
        if (this.worldLastRenderers.isEmpty() == false)
        {
            Profilers.get().swap("malilib_renderworldlast");

            Framebuffer fb = MinecraftClient.isFabulousGraphicsOrBetter() ? mc.worldRenderer.getTranslucentFramebuffer() : null;

            if (fb != null)
            {
                fb.beginWrite(false);
            }

            for (IRenderer renderer : this.worldLastRenderers)
            {
                Profilers.get().push(renderer.getProfilerSectionSupplier());
                renderer.onRenderWorldLast(matrix4f, projMatrix);
                Profilers.get().pop();
            }

            if (fb != null)
            {
                mc.getFramebuffer().beginWrite(false);
            }
        }
    }
}
