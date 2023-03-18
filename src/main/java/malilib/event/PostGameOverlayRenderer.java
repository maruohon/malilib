package malilib.event;

import malilib.render.RenderContext;
import malilib.util.ProfilerSectionSupplierSupplier;

public interface PostGameOverlayRenderer extends ProfilerSectionSupplierSupplier
{
    /**
     * Called after the vanilla overlays have been rendered.
     * <br><br>
     * The classes implementing this method should be registered
     * to {@link malilib.event.dispatch.RenderEventDispatcher}.
     * <br><br>
     * Note: The client world and the client player are checked
     * to not be null before this method is called.
     */
    void onPostGameOverlayRender(RenderContext ctx);
}
