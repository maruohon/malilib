package fi.dy.masa.malilib.gui.config;

import java.util.List;
import javax.annotation.Nullable;

public interface ConfigTabRegistry
{
    ConfigTabRegistry INSTANCE = new ConfigTabRegistryImpl();

    /**
     * Registers a config tab provider for the given modId.
     * @param modId
     * @param tabProvider
     */
    void registerConfigTabProvider(String modId, ConfigTabProvider tabProvider);

    /**
     * Returns the registered config tab provider for the given mod.
     * @param modId
     * @return
     */
    @Nullable
    ConfigTabProvider getConfigTabProviderFor(String modId);

    /**
     * Returns a list of all registered config tabs.
     * @return
     */
    List<ConfigTab> getAllRegisteredConfigTabs();
}
