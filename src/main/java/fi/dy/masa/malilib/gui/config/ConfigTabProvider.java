package fi.dy.masa.malilib.gui.config;

import java.util.List;

public interface ConfigTabProvider
{
    /**
     * Returns the list of config screen tabs/categories that should
     * appear on the config screen.
     * @return
     */
    List<ConfigTab> getConfigTabs();
}
