package fi.dy.masa.malilib.event.dispatch;

import fi.dy.masa.malilib.event.ClientTickHandler;

public interface TickEventDispatcher
{
    /**
     * Registers a client tick handler, which will have its {@link ClientTickHandler#onClientTick()} method
     * called at the end of the client world ticking phase.
     * @param handler
     */
    void registerClientTickHandler(ClientTickHandler handler);
}
