package fi.dy.masa.malilib.event.dispatch;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import fi.dy.masa.malilib.event.IClientTickHandler;

public class TickEventDispatcher implements ITickEventDispatcher
{
    public static final ITickEventDispatcher INSTANCE = new TickEventDispatcher();

    private final List<IClientTickHandler> clientTickHandlers = new ArrayList<>();

    @Override
    public void registerClientTickHandler(IClientTickHandler handler)
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
            for (IClientTickHandler handler : this.clientTickHandlers)
            {
                handler.onClientTick(mc);
            }
        }
    }
}
