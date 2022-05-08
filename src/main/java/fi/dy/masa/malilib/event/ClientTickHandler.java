package fi.dy.masa.malilib.event;

import fi.dy.masa.malilib.util.ProfilerSectionSupplierSupplier;

public interface ClientTickHandler extends ProfilerSectionSupplierSupplier
{
    /**
     * Called from the end of the client tick code (for world ticks, not the main game loop/rendering).
     * <br>br>
     * The classes implementing this method should be registered
     * to {@link fi.dy.masa.malilib.event.dispatch.TickEventDispatcherImpl}.
     * <br><br>
     * Note: The client world and the client player are checked
     * to not be null before this method is called.
     */
    void onClientTick();
}
