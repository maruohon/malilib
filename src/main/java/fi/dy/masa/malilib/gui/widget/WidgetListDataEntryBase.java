package fi.dy.masa.malilib.gui.widgets;

import javax.annotation.Nullable;

public class WidgetListDataEntryBase<TYPE> extends WidgetListEntryBase
{
    @Nullable protected final TYPE data;

    public WidgetListDataEntryBase(int x, int y, int width, int height, int listIndex, @Nullable TYPE data)
    {
        super(x, y, width, height, listIndex);

        this.data = data;
    }

    @Nullable
    public TYPE getData()
    {
        return this.data;
    }
}
