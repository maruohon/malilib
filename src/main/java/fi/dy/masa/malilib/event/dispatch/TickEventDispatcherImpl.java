package fi.dy.masa.malilib.event.dispatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import fi.dy.masa.malilib.event.ClientTickHandler;
import fi.dy.masa.malilib.util.game.wrap.GameUtils;

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
            this.clientTickHandlers.sort(Comparator.comparing(ClientTickHandler::getPriority));
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onClientTick()
    {
        if (this.clientTickHandlers.isEmpty() == false)
        {
            GameUtils.profilerPush("malilib_client_tick");

            for (ClientTickHandler handler : this.clientTickHandlers)
            {
                GameUtils.profilerPush(handler.getProfilerSectionSupplier());
                handler.onClientTick();
                GameUtils.profilerPop();
            }

            GameUtils.profilerPop();
        }
    }
}
