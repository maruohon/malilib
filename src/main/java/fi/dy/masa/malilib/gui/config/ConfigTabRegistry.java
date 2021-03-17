package fi.dy.masa.malilib.gui.config;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.util.data.ModInfo;

public interface ConfigTabRegistry
{
    ConfigTabRegistry INSTANCE = new ConfigTabRegistryImpl();

    /**
     * Registers a config tab provider for the given modId.
     */
    void registerConfigTabProvider(ModInfo modInfo, Supplier<List<ConfigTab>> tabProvider);

    /**
     * Returns the registered config tab provider for the given mod.
     * @param modInfo
     * @return
     */
    @Nullable
    Supplier<List<ConfigTab>> getConfigTabProviderFor(ModInfo modInfo);

    /**
     * Returns a list of all registered config tabs.
     * @return
     */
    List<ConfigTab> getAllRegisteredConfigTabs();
}
