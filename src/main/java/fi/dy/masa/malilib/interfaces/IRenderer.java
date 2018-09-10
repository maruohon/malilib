package fi.dy.masa.malilib.interfaces;

public interface IRenderer
{
    /**
     * Called after the vanilla overlays have been rendered
     * @param partialTicks
     */
    void onRenderGameOverlayPost(float partialTicks);

    /**
     * Called after vanilla world rendering
     * @param partialTicks
     */
    void onRenderWorldLast(float partialTicks);
}
