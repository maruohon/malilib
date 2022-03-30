package fi.dy.masa.malilib.config.util;

import java.util.List;
import java.util.function.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.JsonModConfig.ConfigDataUpdater;
import fi.dy.masa.malilib.config.category.ConfigOptionCategory;
import fi.dy.masa.malilib.input.Hotkey;

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
        protected final int minimumConfigVersion;

        public KeyBindSettingsResetter(Supplier<List<? extends Hotkey>> hotkeyListSupplier, int minimumConfigVersion)
        {
            this.hotkeyListSupplier = hotkeyListSupplier;
            this.minimumConfigVersion = minimumConfigVersion;
        }

        @Override
        public void updateConfigDataBeforeLoading(JsonObject root, int configDataVersion)
        {
        }

        @Override
        public void updateConfigsAfterLoading(List<ConfigOptionCategory> categories,
                                              int readConfigDataVersion,
                                              int currentConfigDataVersion)
        {
            if (readConfigDataVersion < this.minimumConfigVersion)
            {
                String name = categories.size() > 0 ? categories.get(0).getModInfo().getModId() : "?";
                MaLiLib.debugLog("Resetting KeyBindSettings of mod '{}' - read: {} current: {}, min: {}",
                                 name, readConfigDataVersion, currentConfigDataVersion, this.minimumConfigVersion);

                List<? extends Hotkey> list = this.hotkeyListSupplier.get();

                if (list != null)
                {
                    ConfigUtils.resetAllKeybindSettingsToDefaults(list);
                }
            }
        }
    }
}
