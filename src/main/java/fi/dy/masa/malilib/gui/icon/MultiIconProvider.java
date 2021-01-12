package fi.dy.masa.malilib.gui.icon;

import javax.annotation.Nullable;

public interface MultiIconProvider<T>
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
    @Nullable
    MultiIcon getIconFor(T entry);
}
