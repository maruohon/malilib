package fi.dy.masa.malilib.gui.interfaces;

import java.util.List;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.gui.config.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;

public interface IConfigGuiTab
{
    /**
     * Returns the internal (config-savable) name of this tab/category
     * @return
     */
    String getName();

    /**
     * Returns the display name for this config category/tab
     * @return
     */
    String getDisplayName();

    /**
     * Returns the width of the config options in the config GUI
     * @return
     */
    int getConfigWidth();

    /**
     * Whether or not the config GUI tab should include the keybind search button
     * @return
     */
    boolean useKeybindSearch();

    /**
     * Returns the list of config options to display on this tab/in this category
     * @return
     */
    List<? extends ConfigOption> getConfigOptions();

    /**
     * Returns the button action listener that should be used for this tab's selection button
     * @return
     */
    IButtonActionListener getButtonActionListener(GuiConfigsBase gui);

    /**
     * Returns the tab by the given name from the provided list
     * @param tabName
     * @param list
     * @param defaultTab the default value to return, if no matches are found in the provided list
     * @return the first found tab by the given name, or the provided default tab if there were no matches
     */
    public static IConfigGuiTab getTabByNameOrDefault(String tabName, List<IConfigGuiTab> list, IConfigGuiTab defaultTab)
    {
        for (IConfigGuiTab tab : list)
        {
            if (tabName.equalsIgnoreCase(tab.getName()))
            {
                return tab;
            }
        }

        return defaultTab;
    }
}
