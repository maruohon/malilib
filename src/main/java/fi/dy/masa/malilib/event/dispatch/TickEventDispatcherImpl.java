package fi.dy.masa.malilib.event.dispatch;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import fi.dy.masa.malilib.event.ClientTickHandler;

public class TickEventDispatcherImpl implements TickEventDispatcher
{
    protected final List<ClientTickHandler> clientTickHandlers = new ArrayList<>();

    public TickEventDispatcherImpl()
    {
    }

    @Override
    public void registerClientTickHandler(ClientTickHandler handler)
    {
        if (this.clientTickHandlers.contains(handler) == false)
        {
            this.clientTickHandlers.add(handler);
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onClientTick(Minecraft mc)
    {
        if (this.clientTickHandlers.isEmpty() == false)
        {
            mc.profiler.startSection("malilib_client_tick");

            for (ClientTickHandler handler : this.clientTickHandlers)
            {
                mc.profiler.func_194340_a(handler.getProfilerSectionSupplier());
                handler.onClientTick(mc);
                mc.profiler.endSection();
            }

            mc.profiler.endSection();
        }
    }
}
