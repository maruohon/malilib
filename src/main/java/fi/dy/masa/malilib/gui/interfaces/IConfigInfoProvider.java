package fi.dy.masa.malilib.gui.interfaces;

import fi.dy.masa.malilib.gui.GuiConfigsBase.ConfigOptionWrapper;

public interface IConfigInfoProvider
{
    /**
     * Get the mouse-over hover info tooltip for the given config option wrapper
     * @param wrapper
     * @return
     */
    default String getHoverInfo(ConfigOptionWrapper wrapper)
    {
        return wrapper.getConfig() != null ? wrapper.getConfig().getComment() : wrapper.getLabel();
    }
}
