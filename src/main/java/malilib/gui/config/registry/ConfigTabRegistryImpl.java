package malilib.gui.config.registry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import malilib.gui.config.ConfigTab;
import malilib.gui.tab.ScreenTab;
import malilib.util.data.ModInfo;

public class ConfigTabRegistryImpl implements ConfigTabRegistry
{
    protected final Map<ModInfo, List<Supplier<List<? extends ConfigTab>>>> configTabSuppliers = new HashMap<>();
    protected final Map<ModInfo, List<Supplier<List<? extends ScreenTab>>>> screenTabSuppliers = new HashMap<>();
    protected final Map<ModInfo, Supplier<List<? extends ConfigTab>>> wrappedSuppliers = new HashMap<>();

    @Override
    public void registerConfigTabSupplier(ModInfo modInfo, Supplier<List<? extends ConfigTab>> tabSupplier)
    {
        // Add the parent mod provider as the first entry
        List<Supplier<List<? extends ConfigTab>>> list = this.configTabSuppliers.computeIfAbsent(modInfo, m -> new ArrayList<>());
        list.add(0, tabSupplier);
        this.wrappedSuppliers.put(modInfo, createWrapperSupplier(list));
    }

    @Override
    public void registerExtensionModConfigTabSupplier(ModInfo modInfo, Supplier<List<? extends ConfigTab>> tabSupplier)
    {
        List<Supplier<List<? extends ConfigTab>>> list = this.configTabSuppliers.computeIfAbsent(modInfo, m -> new ArrayList<>());
        list.add(tabSupplier);
        this.wrappedSuppliers.put(modInfo, createWrapperSupplier(list));
    }

    @Override
    public void registerExtensionModConfigScreenTabSupplier(ModInfo modInfo, Supplier<List<? extends ScreenTab>> tabSupplier)
    {
        this.screenTabSuppliers.computeIfAbsent(modInfo, m -> new ArrayList<>()).add(tabSupplier);
    }

    @Override
    @Nullable
    public Supplier<List<? extends ConfigTab>> getConfigTabSupplierFor(ModInfo modInfo)
    {
        return this.wrappedSuppliers.get(modInfo);
    }

    @Override
    public List<? extends ConfigTab> getAllRegisteredConfigTabs()
    {
        List<ConfigTab> tabList = new ArrayList<>();
        this.wrappedSuppliers.values().forEach(p -> tabList.addAll(p.get()));
        return tabList;
    }

    public List<ModInfo> getAllModsWithConfigTabs()
    {
        ArrayList<ModInfo> list = new ArrayList<>(this.configTabSuppliers.keySet());
        list.sort(Comparator.comparing(ModInfo::getModName));
        return list;
    }

    @Override
    public List<? extends ScreenTab> getExtraConfigScreenTabsFor(ModInfo mod)
    {
        ArrayList<ScreenTab> list = new ArrayList<>();
        List<Supplier<List<? extends ScreenTab>>> supplierList = this.screenTabSuppliers.get(mod);

        if (supplierList != null)
        {
            for (Supplier<List<? extends ScreenTab>> listSupplier : supplierList)
            {
                list.addAll(listSupplier.get());
            }
        }

        return list;
    }

    public static <T> Supplier<List<? extends T>> createWrapperSupplier(List<Supplier<List<? extends T>>> listOfSuppliers)
    {
        return () -> {
            ArrayList<T> list = new ArrayList<>();

            for (Supplier<List<? extends T>> listSupplier : listOfSuppliers)
            {
                list.addAll(listSupplier.get());
            }

            return list;
        };
    }
}
