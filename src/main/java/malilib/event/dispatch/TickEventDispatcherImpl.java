package malilib.event.dispatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import malilib.event.ClientTickHandler;
import malilib.util.game.wrap.GameWrap;

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
            GameWrap.profilerPush("malilib_client_tick");

            for (ClientTickHandler handler : this.clientTickHandlers)
            {
                GameWrap.profilerPush(handler.getProfilerSectionSupplier());
                handler.onClientTick();
                GameWrap.profilerPop();
            }

            GameWrap.profilerPop();
        }
    }
}
