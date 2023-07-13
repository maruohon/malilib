package malilib.config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import malilib.MaLiLib;
import malilib.util.data.ModInfo;

public class ConfigManagerImpl implements ConfigManager
{
    protected final Map<ModInfo, ModConfig> configHandlers = new LinkedHashMap<>();

    @Override
    public void registerConfigHandler(ModConfig handler)
    {
        final ModInfo modInfo = handler.getModInfo();

        if (this.configHandlers.containsKey(modInfo))
        {
            MaLiLib.LOGGER.warn("Tried to override an existing config handler for mod ID '{}'", modInfo);
            return;
        }

        MaLiLib.debugLog("Registering config handler for mod {}, containing {} categories", modInfo.getModId(), handler.getConfigOptionCategories().size());
        handler.getConfigOptionCategories().forEach((category) -> category.getConfigOptions().forEach((config) -> config.setModInfo(modInfo)));

        this.configHandlers.put(modInfo, handler);
    }

    @Override
    @Nullable
    public ModConfig getConfigHandler(ModInfo modInfo)
    {
        return this.configHandlers.get(modInfo);
    }

    @Override
    public boolean saveConfigsIfChanged(ModInfo modInfo)
    {
        ModConfig handler = this.configHandlers.get(modInfo);

        if (handler != null)
        {
            return handler.onConfigsPotentiallyChanged();
        }

        return false;
    }

    public List<ModConfig> getAllModConfigs()
    {
        return new ArrayList<>(this.configHandlers.values());
    }

    public List<ModConfig> getAllModConfigsSorted()
    {
        ArrayList<ModConfig> list = new ArrayList<>(this.configHandlers.values());
        list.sort(Comparator.comparing(v -> v.getModInfo().getModName()));
        return list;
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void loadAllConfigs()
    {
        for (ModConfig handler : this.configHandlers.values())
        {
            MaLiLib.debugLog("Loading configs for mod {}", handler.getModInfo().getModId());
            handler.loadFromFile();
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void saveAllConfigs()
    {
        for (ModConfig handler : this.configHandlers.values())
        {
            handler.saveToFile();
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public boolean saveIfDirty()
    {
        boolean savedSomething = false;

        for (ModConfig handler : this.configHandlers.values())
        {
            savedSomething |= handler.saveIfDirty();
        }

        return savedSomething;
    }
}
