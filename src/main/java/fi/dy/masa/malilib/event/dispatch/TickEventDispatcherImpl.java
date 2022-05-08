package fi.dy.masa.malilib.event.dispatch;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.profiler.Profiler;
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
    public void onClientTick(MinecraftClient mc)
    {
        if (this.clientTickHandlers.isEmpty() == false)
        {
            Profiler profiler = mc.getProfiler();
            profiler.push("malilib_client_tick");

            for (ClientTickHandler handler : this.clientTickHandlers)
            {
                profiler.push(handler.getProfilerSectionSupplier());
                handler.onClientTick();
                profiler.pop();
            }

            profiler.pop();
        }
    }
}
