package fi.dy.masa.malilib.gui.interfaces;

import javax.annotation.Nullable;

public interface IIconProvider<T>
{
    /**
     * Returns the expected width of the icons for nice alignment,
     * in case some of the entries return null icons
     * @return
     */
    int getExpectedWidth();

    /**
     * Returns the icon to use for the given value
     * @param entry
     * @return
     */
    @Nullable IGuiIcon getIconFor(T entry);
}
