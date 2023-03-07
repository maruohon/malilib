package malilib.gui.config.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import com.google.common.collect.ArrayListMultimap;

import malilib.config.option.ConfigInfo;
import malilib.gui.config.ConfigTab;

public class ConfigTabExtensionRegistry
{
    protected final ArrayListMultimap<ConfigTab, Supplier<List<? extends ConfigInfo>>> configTabExtensions = ArrayListMultimap.create();

    public void registerConfigTabExtension(ConfigTab tab,
                                           Supplier<List<? extends ConfigInfo>> customOptionSupplier)
    {
        this.configTabExtensions.put(tab, customOptionSupplier);
    }

    public List<ConfigInfo> getExtensionConfigsForTab(ConfigTab tab)
    {
        List<ConfigInfo> list = new ArrayList<>();

        for (Supplier<List<? extends ConfigInfo>> extension : this.configTabExtensions.get(tab))
        {
            list.addAll(extension.get());
        }

        return list;
    }
}
