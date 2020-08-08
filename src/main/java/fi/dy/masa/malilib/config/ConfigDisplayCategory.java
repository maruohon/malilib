package fi.dy.masa.malilib.config;

import java.util.List;
import fi.dy.masa.malilib.config.option.ConfigInfo;

public interface ConfigDisplayCategory extends ConfigCategory
{
    /**
     * Returns the list of config options to display on this tab/in this category
     * @return
     */
    List<? extends ConfigInfo> getConfigsForDisplay();
}
