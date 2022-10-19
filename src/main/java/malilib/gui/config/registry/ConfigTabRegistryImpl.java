package malilib.gui.config.registry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import malilib.gui.config.ConfigTab;
import malilib.util.data.ModInfo;

public class ConfigTabRegistryImpl implements ConfigTabRegistry
{
    protected final Map<ModInfo, Supplier<List<ConfigTab>>> configTabProviders = new HashMap<>();

    public ConfigTabRegistryImpl()
    {
    }

    @Override
    public void registerConfigTabProvider(ModInfo modInfo, Supplier<List<ConfigTab>> tabProvider)
    {
        this.configTabProviders.put(modInfo, tabProvider);
    }

    @Override
    @Nullable
    public Supplier<List<ConfigTab>> getConfigTabProviderFor(ModInfo modInfo)
    {
        return this.configTabProviders.get(modInfo);
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

    public List<ModInfo> getAllModsWithConfigTabs()
    {
        ArrayList<ModInfo> list = new ArrayList<>(this.configTabProviders.keySet());
        list.sort(Comparator.comparing(ModInfo::getModName));
        return list;
    }
}
