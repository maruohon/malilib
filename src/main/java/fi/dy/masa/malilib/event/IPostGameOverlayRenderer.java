package fi.dy.masa.malilib.event;

public interface IPostGameOverlayRenderer
{
    /**
     * Called after the vanilla overlays have been rendered.
     * <br><br>
     * The classes implementing this method should be registered to {@link fi.dy.masa.malilib.event.dispatch.RenderEventDispatcher}
     * @param partialTicks
     */
    void onPostGameOverlayRender(float partialTicks);
}
