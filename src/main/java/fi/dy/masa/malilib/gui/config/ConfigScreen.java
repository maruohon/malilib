package fi.dy.masa.malilib.gui.config;

import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.util.DialogHandler;

public interface ConfigScreen
{
    /**
     * Returns the Mod ID of the mod to which the configs on this GUI belong to
     * @return
     */
    String getModId();

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

    /**
     * Get the "dialog window" handler for this GUI, if any.
     * @return
     */
    @Nullable
    default DialogHandler getDialogHandler()
    {
        return null;
    }
}
