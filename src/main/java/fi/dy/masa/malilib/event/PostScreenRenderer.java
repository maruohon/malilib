package fi.dy.masa.malilib.event;

import net.minecraft.client.MinecraftClient;
import fi.dy.masa.malilib.util.ProfilerSectionSupplierSupplier;

public interface PostScreenRenderer extends ProfilerSectionSupplierSupplier
{
    /**
     * Called after vanilla screen rendering
     * <br><br>
     * The classes implementing this method should be registered
     * to {@link fi.dy.masa.malilib.event.dispatch.RenderEventDispatcher}.
     * <br><br>
     * Note: The client world and the client player are checked
     * to not be null before this method is called.
     */
    void onPostScreenRender(MinecraftClient mc, float partialTicks);
}
