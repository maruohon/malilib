package fi.dy.masa.malilib.event.dispatch;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.IClientWorldChangeHandler;

public class ClientWorldChangeEventDispatcher implements IClientWorldChangeEventDispatcher
{
    public static final IClientWorldChangeEventDispatcher INSTANCE = new ClientWorldChangeEventDispatcher();

    private final List<IClientWorldChangeHandler> worldChangeHandlers = new ArrayList<>();

    @Override
    public void registerClientWorldChangeHandler(IClientWorldChangeHandler listener)
    {
        if (this.worldChangeHandlers.contains(listener) == false)
        {
            this.worldChangeHandlers.add(listener);
        }
    }

    @Override
    public void unregisterClientWorldChangeHandler(IClientWorldChangeHandler listener)
    {
        this.worldChangeHandlers.remove(listener);
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onWorldLoadPre(@Nullable WorldClient worldBefore, @Nullable WorldClient worldAfter, Minecraft mc)
    {
        if (this.worldChangeHandlers.isEmpty() == false)
        {
            for (IClientWorldChangeHandler listener : this.worldChangeHandlers)
            {
                listener.onPreClientWorldChange(worldBefore, worldAfter, mc);
            }
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onWorldLoadPost(@Nullable WorldClient worldBefore, @Nullable WorldClient worldAfter, Minecraft mc)
    {
        // Save all the configs when exiting a world
        if (worldAfter == null)
        {
            ((ConfigManager) ConfigManager.INSTANCE).saveAllConfigs();
        }
        // (Re-)Load all the configs from file when entering a world
        else if (worldBefore == null)
        {
            ((ConfigManager) ConfigManager.INSTANCE).loadAllConfigs();
            InputEventDispatcher.getKeyBindManager().updateUsedKeys();
        }

        if (this.worldChangeHandlers.isEmpty() == false)
        {
            for (IClientWorldChangeHandler listener : this.worldChangeHandlers)
            {
                listener.onPostClientWorldChange(worldBefore, worldAfter, mc);
            }
        }
    }
}
