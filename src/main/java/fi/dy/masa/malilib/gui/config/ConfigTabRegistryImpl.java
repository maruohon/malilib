package fi.dy.masa.malilib.gui.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class ConfigTabRegistryImpl implements ConfigTabRegistry
{
    private final Map<String, Supplier<List<ConfigTab>>> configTabProviders = new HashMap<>();

    ConfigTabRegistryImpl()
    {
    }

    @Override
    public void registerConfigTabProvider(String modId, Supplier<List<ConfigTab>> tabProvider)
    {
        this.configTabProviders.put(modId, tabProvider);
    }

    @Override
    @Nullable
    public Supplier<List<ConfigTab>> getConfigTabProviderFor(String modId)
    {
        return this.configTabProviders.get(modId);
    }

    @Override
    public List<ConfigTab> getAllRegisteredConfigTabs()
    {
        List<ConfigTab> list = new ArrayList<>();

        for (Supplier<List<ConfigTab>> provider : this.configTabProviders.values())
        {
            list.addAll(provider.get());
        }

        return list;
    }
}
