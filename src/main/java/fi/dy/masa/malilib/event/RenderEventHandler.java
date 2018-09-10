package fi.dy.masa.malilib.event;

import java.util.ArrayList;
import java.util.List;
import fi.dy.masa.malilib.interfaces.IRenderDispatcher;
import fi.dy.masa.malilib.interfaces.IRenderer;

public class RenderEventHandler implements IRenderDispatcher
{
    private static final RenderEventHandler INSTANCE = new RenderEventHandler();

    private final List<IRenderer> overlayRenderers = new ArrayList<>();
    private final List<IRenderer> worldLastRenderers = new ArrayList<>();

    public static RenderEventHandler getInstance()
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
    public void registerWorldLastRenderer(IRenderer renderer)
    {
        if (this.worldLastRenderers.contains(renderer) == false)
        {
            this.worldLastRenderers.add(renderer);
        }
    }

    public void onRenderGameOverlayPost(float partialTicks)
    {
        if (this.overlayRenderers.isEmpty() == false)
        {
            for (IRenderer renderer : this.overlayRenderers)
            {
                renderer.onRenderGameOverlayPost(partialTicks);
            }
        }
    }

    public void onRenderWorldLast(float partialTicks)
    {
        if (this.worldLastRenderers.isEmpty() == false)
        {
            for (IRenderer renderer : this.worldLastRenderers)
            {
                renderer.onRenderWorldLast(partialTicks);
            }
        }
    }
}
