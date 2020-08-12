package fi.dy.masa.malilib.gui.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class ConfigTabRegistryImpl implements ConfigTabRegistry
{
    private final Map<String, ConfigTabProvider> configTabProviders = new HashMap<>();

    ConfigTabRegistryImpl()
    {
    }

    @Override
    public void registerConfigTabProvider(String modId, ConfigTabProvider tabProvider)
    {
        this.configTabProviders.put(modId, tabProvider);
    }

    @Override
    @Nullable
    public ConfigTabProvider getConfigTabProviderFor(String modId)
    {
        return this.configTabProviders.get(modId);
    }

    @Override
    public List<ConfigTab> getAllRegisteredConfigTabs()
    {
        List<ConfigTab> list = new ArrayList<>();

        for (ConfigTabProvider provider : this.configTabProviders.values())
        {
            list.addAll(provider.getConfigTabs());
        }

        return list;
    }
}
