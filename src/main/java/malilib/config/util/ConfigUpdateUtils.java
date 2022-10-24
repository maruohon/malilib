package malilib.config.util;

import java.util.List;
import java.util.function.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.tuple.Pair;

import malilib.MaLiLib;
import malilib.config.JsonModConfig.ConfigDataUpdater;
import malilib.config.category.ConfigOptionCategory;
import malilib.input.Hotkey;

public class ConfigUpdateUtils
{
    public static class ChainedConfigDataUpdater implements ConfigDataUpdater
    {
        protected final ImmutableList<ConfigDataUpdater> updaters;

        public ChainedConfigDataUpdater(ConfigDataUpdater... updaters)
        {
            this.updaters = ImmutableList.copyOf(updaters);
        }

        @Override
        public void updateConfigDataBeforeLoading(JsonObject root, int configDataVersion)
        {
            for (ConfigDataUpdater updater : this.updaters)
            {
                updater.updateConfigDataBeforeLoading(root, configDataVersion);
            }
        }

        @Override
        public void updateConfigsAfterLoading(List<ConfigOptionCategory> categories,
                                              int readConfigDataVersion,
                                              int currentConfigDataVersion)
        {
            for (ConfigDataUpdater updater : this.updaters)
            {
                updater.updateConfigsAfterLoading(categories, readConfigDataVersion, currentConfigDataVersion);
            }
        }
    }

    /**
     * This updater will reset all the KeyBindSettings of the hotkeys from the given Supplier
     * back to their current default values, if the config version read from file is older than
     * the given minimumConfigVersion.
     */
    public static class KeyBindSettingsResetter implements ConfigDataUpdater
    {
        protected final Supplier<List<? extends Hotkey>> hotkeyListSupplier;
        protected final int maximumConfigVersion;

        public KeyBindSettingsResetter(Supplier<List<? extends Hotkey>> hotkeyListSupplier, int maximumConfigVersion)
        {
            this.hotkeyListSupplier = hotkeyListSupplier;
            this.maximumConfigVersion = maximumConfigVersion;
        }

        @Override
        public void updateConfigsAfterLoading(List<ConfigOptionCategory> categories,
                                              int readConfigDataVersion,
                                              int currentConfigDataVersion)
        {
            if (readConfigDataVersion <= this.maximumConfigVersion)
            {
                String name = categories.size() > 0 ? categories.get(0).getModInfo().getModId() : "?";
                MaLiLib.debugLog("Resetting KeyBindSettings of mod '{}' - read: {} current: {}, max: {}",
                                 name, readConfigDataVersion, currentConfigDataVersion, this.maximumConfigVersion);

                List<? extends Hotkey> list = this.hotkeyListSupplier.get();

                if (list != null)
                {
                    ConfigUtils.resetAllKeybindSettingsToDefaults(list);
                }
            }
        }
    }

    public static class ConfigCategoryRenamer implements ConfigDataUpdater
    {
        protected final List<Pair<String, String>> renamedCategories;
        protected final int maximumConfigVersion;
        protected final int minimumConfigVersion;

        public ConfigCategoryRenamer(List<Pair<String, String>> renamedCategories,
                                     int minimumConfigVersion,
                                     int maximumConfigVersion)
        {
            this.renamedCategories = renamedCategories;
            this.minimumConfigVersion = minimumConfigVersion;
            this.maximumConfigVersion = maximumConfigVersion;
        }

        @Override
        public void updateConfigDataBeforeLoading(JsonObject root, int configDataVersion)
        {
            if (configDataVersion >= this.minimumConfigVersion &&
                configDataVersion <= this.maximumConfigVersion)
            {
                for (Pair<String, String> pair : this.renamedCategories)
                {
                    String oldName = pair.getLeft();
                    String newName = pair.getRight();
                    JsonElement el = root.get(oldName);

                    if (el != null)
                    {
                        root.remove(oldName);
                        root.add(newName, el);
                    }
                }
            }
        }
    }
}
