package fi.dy.masa.malilib.gui.config;

import fi.dy.masa.malilib.config.ConfigInfo;

public interface ConfigInfoProvider
{
    /**
     * Get the mouse-over hover info tooltip for the given config option
     * @param config
     * @return
     */
    default String getHoverInfo(ConfigInfo config)
    {
        return config.getComment();
    }
}
