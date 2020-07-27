package fi.dy.masa.malilib.event;

public interface IPostWorldRenderer
{
    /**
     * Called after vanilla world rendering
     * <br><br>
     * The classes implementing this method should be registered to {@link fi.dy.masa.malilib.event.dispatch.RenderEventDispatcher}
     * @param partialTicks
     */
    void onPostWorldRender(float partialTicks);
}
