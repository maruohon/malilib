package fi.dy.masa.malilib.gui.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.ScreenTab;
import fi.dy.masa.malilib.util.data.ConfigOnTab;
import fi.dy.masa.malilib.util.data.ModInfo;

public interface ConfigTab extends ScreenTab
{
    /**
     * Returns the ModInfo of the mod this tab belongs to.<br>
     * Used on the config screens when showing options from multiple categories
     * or all mods, and also used by the config status indicator widgets.
     * @return
     */
    ModInfo getModInfo();

    /**
     * Returns the width of the config option edit widgets on the config screen.
     * This is used for nicely aligned positioning of the reset button after the edit widgets.
     * @return
     */
    int getConfigWidgetsWidth();

    /**
     * Returns the list of config options included on this tab.
     * @return
     */
    List<? extends ConfigInfo> getConfigs();

    /**
     * Returns a full list of configs on this tab, including the configs from
     * any possible nested expandable/collapsible config groups.
     * @return
     */
    default List<? extends ConfigInfo> getExpandedConfigs()
    {
        ArrayList<ConfigInfo> expandedList = new ArrayList<>();

        for (ConfigInfo config : this.getConfigs())
        {
            expandedList.add(config);
            config.addNestedOptionsToList(expandedList, 1);
        }

        return expandedList;
    }

    /**
     * Returns a full list of configs on this tab, including the configs from
     * any possible nested expandable/collapsible config groups, wrapped in
     * ConfigOnTab to include the tab information, which includes the owning mod.
     * @param configConsumer
     */
    default void getTabbedExpandedConfigs(Consumer<ConfigOnTab> configConsumer)
    {
        for (ConfigInfo config : this.getExpandedConfigs())
        {
            configConsumer.accept(new ConfigOnTab(this, config));
        }
    }
}
