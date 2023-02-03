package malilib.gui.icon;

import javax.annotation.Nullable;

public interface IconProvider<T>
{
    /**
     * @return the expected width of the icons for nice alignment,
     *         in case some entries return null icons
     */
    int getExpectedWidth();

    /**
     * @return the icon to use for the given value
     */
    @Nullable
    Icon getIconFor(T entry);
}
