package malilib.event;

import malilib.util.ProfilerSectionSupplierSupplier;

public interface PostWorldRenderer extends ProfilerSectionSupplierSupplier
{
    /**
     * Called after vanilla world rendering
     * <br><br>
     * The classes implementing this method should be registered
     * to {@link malilib.event.dispatch.RenderEventDispatcher}.
     * <br><br>
     * Note: The client world and the client player are checked
     * to not be null before this method is called.
     */
    void onPostWorldRender(float tickDelta);
}
