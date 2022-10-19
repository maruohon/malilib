package malilib.gui.config;

import java.util.List;
import malilib.config.option.ConfigInfo;
import malilib.util.data.ModInfo;

public interface ConfigScreen
{
    /**
     * Returns the ModInfo of the mod to which the configs on this config screen belong to
     * @return
     */
    ModInfo getModInfo();

    /**
     * When called, the implementer should clear all the stored config options
     * and any associated change listeners etc.
     */
    void clearOptions();

    /**
     * Returns a list of all currently visible/available/selected/whatever config options
     * that the widget list can use.
     * @return
     */
    List<? extends ConfigInfo> getConfigs();
}
