package malilib.event.dispatch;

import malilib.event.ClientTickHandler;

public interface TickEventDispatcher
{
    /**
     * Registers a client tick handler, which will have its {@link malilib.event.ClientTickHandler#onClientTick()} method
     * called at the end of the client world ticking phase.
     * @param handler
     */
    void registerClientTickHandler(ClientTickHandler handler);
}
