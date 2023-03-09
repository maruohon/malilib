package malilib.gui.config.registry;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import malilib.gui.config.ConfigTab;
import malilib.gui.tab.ScreenTab;
import malilib.util.data.ModInfo;

public interface ConfigTabRegistry
{
    /**
     * Registers a config tab provider for the given modId
     */
    void registerConfigTabSupplier(ModInfo modInfo, Supplier<List<? extends ConfigTab>> tabSupplier);

    /**
     * Registers an additional config tab supplier for the given mod.
     * This is intended for extension mods to use, to register additional config tabs that
     * are shown at the end of the tab list on the parent mod's config screen.
     * <br><br>Note: This {@link ConfigTab} supplier is used for the config search and the configs' owner
     * mod and category info via the tab, and also for the config status indicator config source lists.
     * So this supplier should contain all the extra config option tabs that actually contain config
     * options that you want to add.
     * <b>This supplier is not used for adding the actual screen tabs, as there
     * could also be non-{@link ConfigTab} screen tabs.</b>
     * <br<br></b>The actual extra config screen tabs come from
     * {@link #registerExtensionModConfigScreenTabSupplier(ModInfo, Supplier<List<ScreenTab>>)}.
     * If all the extra tabs you want to register just have configs and there are no
     * "non-config-option screens", then register the same supplier via both methods.
     * If you have some extra custom screen tabs without a list of configs, then register those actual
     * screen tabs via {@link #registerExtensionModConfigScreenTabSupplier(ModInfo, Supplier<List<ScreenTab>>)}.
     * @param modInfo the parent mod for which this extension mod tab provider is being registered
     */
    void registerExtensionModConfigTabSupplier(ModInfo modInfo, Supplier<List<? extends ConfigTab>> tabSupplier);

    /**
     * Registers an additional config screen tab supplier for the given mod.
     * This is intended for extension mods to use, to register additional config screen tabs that
     * are shown at the end of the tab list on the parent mod's config screen.
     * @param modInfo the parent mod for which this extension mod tab provider is being registered
     */
    void registerExtensionModConfigScreenTabSupplier(ModInfo modInfo, Supplier<List<? extends ScreenTab>> tabSupplier);

    /**
     * @return the config tab provider for the given mod.
     *         If there are any extension mod tabs, then this returns a wrapper provider
     *         that includes both the parent mod tabs and any extension mod tabs.
     */
    @Nullable
    Supplier<List<? extends ConfigTab>> getConfigTabSupplierFor(ModInfo modInfo);

    /**
     * @return a list of all registered config tabs
     */
    List<? extends ConfigTab> getAllRegisteredConfigTabs();

    /**
     * @return a list of any extra config screen tabs for the given mod
     */
    List<? extends ScreenTab> getExtraConfigScreenTabsFor(ModInfo modInfo);
}
