package fi.dy.masa.malilib.gui.config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import com.google.common.collect.ArrayListMultimap;
import fi.dy.masa.malilib.config.option.ConfigInfo;

public class ConfigTabExtensionRegistry
{
    protected final ArrayListMultimap<List<?>, Supplier<List<? extends ConfigInfo>>> configTabExtensions = ArrayListMultimap.create();

    public void registerConfigTabExtension(List<?> baseConfigList,
                                           Supplier<List<? extends ConfigInfo>> customOptionSupplier)
    {
        this.configTabExtensions.put(baseConfigList, customOptionSupplier);
    }

    public List<? extends ConfigInfo> getExtendedList(List<? extends ConfigInfo> baseList, boolean sort)
    {
        List<Supplier<List<? extends ConfigInfo>>> customOptionSuppliers = this.configTabExtensions.get(baseList);

        if (customOptionSuppliers.isEmpty() == false)
        {
            List<ConfigInfo> newList = new ArrayList<>(baseList);

            for (Supplier<List<? extends ConfigInfo>> extension : customOptionSuppliers)
            {
                newList.addAll(extension.get());
            }

            if (sort)
            {
                newList.sort(Comparator.comparing(ConfigInfo::getDisplayName));
            }

            return newList;
        }

        return baseList;
    }
}
