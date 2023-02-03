package malilib.gui.icon;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableMap;

public class BaseIconProvider<T> implements IconProvider<T>
{
    protected final int expectedWidth;
    protected final ImmutableMap<T, Icon> icons;

    public BaseIconProvider(int expectedWidth, ImmutableMap<T, Icon> icons)
    {
        this.expectedWidth = expectedWidth;
        this.icons = icons;
    }

    @Override
    public int getExpectedWidth()
    {
        return this.expectedWidth;
    }

    @Nullable
    @Override
    public Icon getIconFor(T entry)
    {
        return this.icons.get(entry);
    }
}
