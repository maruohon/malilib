package fi.dy.masa.malilib.gui.interfaces;

import java.util.List;
import fi.dy.masa.malilib.config.options.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
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
    List<? extends IConfigBase> getConfigOptions();

    /**
     * Returns the button action listener that should be used for this tab's selection button
     * @return
     */
    IButtonActionListener getButtonActionListener(GuiConfigsBase gui);
}
