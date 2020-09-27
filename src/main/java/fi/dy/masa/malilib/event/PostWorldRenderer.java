package fi.dy.masa.malilib.event;

import net.minecraft.client.Minecraft;
import fi.dy.masa.malilib.util.ProfilerSectionSupplierSupplier;

public interface PostWorldRenderer extends ProfilerSectionSupplierSupplier
{
    /**
     * Called after vanilla world rendering
     * <br><br>
     * The classes implementing this method should be registered
     * to {@link fi.dy.masa.malilib.event.dispatch.RenderEventDispatcherImpl}.
     * <br><br>
     * Note: The client world and the client player are checked
     * to not be null before this method is called.
     */
    void onPostWorldRender(Minecraft mc, float partialTicks);
}
