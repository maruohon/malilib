package fi.dy.masa.malilib.interfaces;

public interface IRenderDispatcher
{
    /**
     * Registers a renderer which will have its {@link IRenderer.onRenderGameOverlayPost}
     * method called after the vanilla rendering is done
     * @param renderer
     */
    void registerGameOverlayRenderer(IRenderer renderer);

    /**
     * Registers a renderer which will have its {@link IRenderer.onRenderTooltipLast}
     * method called after the vanilla tooltip text has been rendered.
     * @param renderer
     */
    void registerTooltipLastRenderer(IRenderer renderer);

    /**
     * Registers a renderer which will have its {@link IRenderer.onRenderWorldLast}
     * method called after the vanilla rendering is done
     * @param renderer
     */
    void registerWorldLastRenderer(IRenderer renderer);
}
