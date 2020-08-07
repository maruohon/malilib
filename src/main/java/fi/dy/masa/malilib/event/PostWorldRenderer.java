package fi.dy.masa.malilib.event;

import fi.dy.masa.malilib.event.dispatch.RenderEventDispatcherImpl;

public interface PostWorldRenderer
{
    /**
     * Called after vanilla world rendering
     * <br><br>
     * The classes implementing this method should be registered to {@link RenderEventDispatcherImpl}
     * @param partialTicks
     */
    void onPostWorldRender(float partialTicks);
}
