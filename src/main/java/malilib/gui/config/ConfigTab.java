package malilib.gui.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import malilib.config.option.ConfigInfo;
import malilib.gui.tab.ScreenTab;
import malilib.util.data.ConfigOnTab;
import malilib.util.data.ModInfo;

public interface ConfigTab extends ScreenTab
{
    /**
     * @return the ModInfo of the mod this tab belongs to.
     *         Used on the config screens when showing options from multiple categories
     *         or from all mods, and also used by the config status indicator widgets.
     */
    ModInfo getModInfo();

    /**
     * @return the width of the config option edit widgets on the config screen.
     *         This is used for nicely aligned positioning of the reset button after the edit widgets.
     */
    int getConfigWidgetsWidth();

    /**
     * @return the list of all configs on this tab, without expanding any config groups
     */
    List<? extends ConfigInfo> getConfigs();

    default List<ConfigOnTab> getTabbedConfigs()
    {
        ArrayList<ConfigOnTab> list = new ArrayList<>();

        for (ConfigInfo config : this.getConfigs())
        {
            list.add(new ConfigOnTab(this, config, 0));
        }

        return list;
    }

    /**
     * @return a full list of configs on this tab, including the configs from
     *         any possible nested expandable/collapsible config groups
     */
    default List<ConfigOnTab> getTabbedExpandedConfigs()
    {
        ArrayList<ConfigOnTab> expandedList = new ArrayList<>();

        for (ConfigInfo config : this.getConfigs())
        {
            expandedList.add(new ConfigOnTab(this, config, 0));
            config.addNestedOptionsToList(expandedList, this, 1, true);
        }

        return expandedList;
    }

    /**
     * Returns a full list of configs on this tab, including the configs from
     * any possible nested expandable/collapsible config groups, wrapped in
     * ConfigOnTab to include the tab information, which includes the owning mod.
     */
    default void offerTabbedExpandedConfigs(Consumer<ConfigOnTab> configConsumer)
    {
        this.getTabbedExpandedConfigs().forEach(configConsumer);
    }
}
