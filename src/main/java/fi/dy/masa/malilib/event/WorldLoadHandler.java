package fi.dy.masa.malilib.event;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.interfaces.IWorldLoadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

public class WorldLoadHandler
{
    private static final WorldLoadHandler INSTANCE = new WorldLoadHandler();

    private final List<IWorldLoadListener> worldLoadPreHandlers = new ArrayList<>();
    private final List<IWorldLoadListener> worldLoadPostHandlers = new ArrayList<>();

    public static WorldLoadHandler getInstance()
    {
        return INSTANCE;
    }

    public void registerWorldLoadPreHandler(IWorldLoadListener listener)
    {
        if (this.worldLoadPreHandlers.contains(listener) == false)
        {
            this.worldLoadPreHandlers.add(listener);
        }
    }

    public void registerWorldLoadPostHandler(IWorldLoadListener listener)
    {
        if (this.worldLoadPostHandlers.contains(listener) == false)
        {
            this.worldLoadPostHandlers.add(listener);
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onWorldLoadPre(@Nullable WorldClient world, Minecraft mc)
    {
        if (this.worldLoadPreHandlers.isEmpty() == false)
        {
            for (IWorldLoadListener listener : this.worldLoadPreHandlers)
            {
                listener.onWorldLoadPre(world, mc);
            }
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onWorldLoadPost(@Nullable WorldClient world, Minecraft mc)
    {
        if (this.worldLoadPostHandlers.isEmpty() == false)
        {
            for (IWorldLoadListener listener : this.worldLoadPostHandlers)
            {
                listener.onWorldLoadPost(world, mc);
            }
        }
    }
}
