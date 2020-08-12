package fi.dy.masa.malilib.event;

public interface PostWorldRenderer
{
    /**
     * Called after vanilla world rendering
     * <br><br>
     * The classes implementing this method should be registered to {@link fi.dy.masa.malilib.event.dispatch.RenderEventDispatcherImpl}
     * @param partialTicks
     */
    void onPostWorldRender(float partialTicks);
}
