package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;

public class BaseDataListEntryWidget<TYPE> extends BaseListEntryWidget
{
    @Nullable protected final TYPE data;

    public BaseDataListEntryWidget(int x, int y, int width, int height, int listIndex, @Nullable TYPE data)
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
