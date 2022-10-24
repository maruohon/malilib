package malilib.gui.icon;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableMap;

public class BaseMultiIconProvider<T> implements MultiIconProvider<T>
{
    protected final int expectedWidth;
    protected final ImmutableMap<T, MultiIcon> icons;

    public BaseMultiIconProvider(int expectedWidth, ImmutableMap<T, MultiIcon> icons)
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
    public MultiIcon getIconFor(T entry)
    {
        return this.icons.get(entry);
    }
}
