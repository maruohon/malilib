package fi.dy.masa.malilib.event.dispatch;

import fi.dy.masa.malilib.event.IClientTickHandler;

public interface ITickEventDispatcher
{
    /**
     * Registers a client tick handler, which will have its {@link IClientTickHandler#onClientTick(net.minecraft.client.Minecraft)} method
     * called at the end of the client world ticking phase.
     * @param handler
     */
    void registerClientTickHandler(IClientTickHandler handler);
}
