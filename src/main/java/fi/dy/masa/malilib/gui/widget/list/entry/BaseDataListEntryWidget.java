package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;

public class BaseDataListEntryWidget<TYPE> extends BaseListEntryWidget
{
    @Nullable protected final DataListWidget<? extends TYPE> listWidget;
    @Nullable protected final TYPE data;

    public BaseDataListEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                   @Nullable TYPE data, @Nullable DataListWidget<? extends TYPE> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex);

        this.data = data;
        this.listWidget = listWidget;
    }

    @Nullable
    public TYPE getData()
    {
        return this.data;
    }
}
