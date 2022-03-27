package fi.dy.masa.malilib.gui.config.registry;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.config.ConfigTab;
import fi.dy.masa.malilib.util.data.ModInfo;

public interface ConfigTabRegistry
{
    /**
     * Registers a config tab provider for the given modId
     */
    void registerConfigTabProvider(ModInfo modInfo, Supplier<List<ConfigTab>> tabProvider);

    /**
     * @return the registered config tab provider for the given mod
     */
    @Nullable
    Supplier<List<ConfigTab>> getConfigTabProviderFor(ModInfo modInfo);

    /**
     * @return a list of all registered config tabs
     */
    List<ConfigTab> getAllRegisteredConfigTabs();
}
