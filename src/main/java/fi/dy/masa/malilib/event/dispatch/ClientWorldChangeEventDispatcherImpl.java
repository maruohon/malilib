package fi.dy.masa.malilib.event.dispatch;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import fi.dy.masa.malilib.action.ActionRegistry;
import fi.dy.masa.malilib.action.ActionRegistryImpl;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.ConfigManagerImpl;
import fi.dy.masa.malilib.event.ClientWorldChangeHandler;
import fi.dy.masa.malilib.input.CustomHotkeyManager;
import fi.dy.masa.malilib.input.HotkeyManager;
import fi.dy.masa.malilib.overlay.InfoWidgetManager;

public class ClientWorldChangeEventDispatcherImpl implements ClientWorldChangeEventDispatcher
{
    private final List<ClientWorldChangeHandler> worldChangeHandlers = new ArrayList<>();

    ClientWorldChangeEventDispatcherImpl()
    {
    }

    @Override
    public void registerClientWorldChangeHandler(ClientWorldChangeHandler listener)
    {
        if (this.worldChangeHandlers.contains(listener) == false)
        {
            this.worldChangeHandlers.add(listener);
        }
    }

    @Override
    public void unregisterClientWorldChangeHandler(ClientWorldChangeHandler listener)
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
            for (ClientWorldChangeHandler listener : this.worldChangeHandlers)
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
        if (worldAfter == null && worldBefore != null)
        {
            ((ConfigManagerImpl) ConfigManager.INSTANCE).saveAllConfigs();
            InfoWidgetManager.INSTANCE.saveToFileIfDirty();
        }
        // (Re-)Load all the configs from file when entering a world
        else if (worldBefore == null && worldAfter != null)
        {
            ((ConfigManagerImpl) ConfigManager.INSTANCE).loadAllConfigs();
            ((ActionRegistryImpl) ActionRegistry.INSTANCE).loadFromFile();
            CustomHotkeyManager.INSTANCE.loadFromFile();
            InfoWidgetManager.INSTANCE.loadFromFile();
            HotkeyManager.INSTANCE.updateUsedKeys();
        }

        if (this.worldChangeHandlers.isEmpty() == false)
        {
            for (ClientWorldChangeHandler listener : this.worldChangeHandlers)
            {
                listener.onPostClientWorldChange(worldBefore, worldAfter, mc);
            }
        }
    }
}
