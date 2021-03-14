package fi.dy.masa.malilib.gui.config;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public interface ConfigTabRegistry
{
    ConfigTabRegistry INSTANCE = new ConfigTabRegistryImpl();

    /**
     * Registers a config tab provider for the given modId.
     * @param modId
     * @param tabProvider
     */
    void registerConfigTabProvider(String modId, Supplier<List<ConfigTab>> tabProvider);

    /**
     * Returns the registered config tab provider for the given mod.
     * @param modId
     * @return
     */
    @Nullable
    Supplier<List<ConfigTab>> getConfigTabProviderFor(String modId);

    /**
     * Returns a list of all registered config tabs.
     * @return
     */
    List<ConfigTab> getAllRegisteredConfigTabs();
}
