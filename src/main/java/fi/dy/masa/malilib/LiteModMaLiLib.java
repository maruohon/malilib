package fi.dy.masa.malilib;

import fi.dy.masa.malilib.config.ConfigManagerImpl;
import fi.dy.masa.malilib.event.dispatch.InitializationDispatcherImpl;
import fi.dy.masa.malilib.registry.Registry;

public class LiteModMaLiLib
{
    // TODO 1.13+ port
    public void onInitCompleted()
    {
        // Dispatch the init calls to all the registered handlers
        ((InitializationDispatcherImpl) Registry.INITIALIZATION_DISPATCHER).onGameInitDone();
    }

    public void onShutDown()
    {
        ((ConfigManagerImpl) Registry.CONFIG_MANAGER).saveIfDirty();
    }
}
