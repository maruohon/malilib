package fi.dy.masa.malilib.event;

import fi.dy.masa.malilib.event.dispatch.RenderEventDispatcherImpl;

public interface PostGameOverlayRenderer
{
    /**
     * Called after the vanilla overlays have been rendered.
     * <br><br>
     * The classes implementing this method should be registered to {@link RenderEventDispatcherImpl}
     * @param partialTicks
     */
    void onPostGameOverlayRender(float partialTicks);
}
