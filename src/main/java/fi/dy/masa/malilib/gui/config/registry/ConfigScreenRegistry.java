package fi.dy.masa.malilib.gui.config.registry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.util.data.ModInfo;

public class ConfigScreenRegistry
{
    protected final Map<ModInfo, Supplier<BaseScreen>> configScreenFactories = new HashMap<>();
    protected ImmutableList<ModInfo> mods = ImmutableList.of();

    public ConfigScreenRegistry()
    {
    }

    public void registerConfigScreenFactory(ModInfo modInfo, Supplier<BaseScreen> screenFactory)
    {
        this.configScreenFactories.put(modInfo, screenFactory);

        ArrayList<ModInfo> list = new ArrayList<>(this.configScreenFactories.keySet());
        list.sort(Comparator.comparing(ModInfo::getModName));
        this.mods = ImmutableList.copyOf(list);
    }

    @Nullable
    public Supplier<BaseScreen> getConfigScreenFactoryFor(ModInfo modInfo)
    {
        return this.configScreenFactories.get(modInfo);
    }

    public ImmutableList<ModInfo> getAllModsWithConfigScreens()
    {
        return this.mods;
    }
}
