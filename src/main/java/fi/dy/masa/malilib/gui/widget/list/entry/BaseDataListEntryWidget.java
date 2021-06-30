package fi.dy.masa.malilib.gui.widget.list.entry;

import java.util.List;
import java.util.function.Consumer;
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

    @Override
    protected boolean isSelected()
    {
        if (this.listWidget != null)
        {
            DataListEntrySelectionHandler<? extends TYPE> handler = this.listWidget.getEntrySelectionHandler();
            return handler != null && handler.isEntrySelected(this.getListIndex());
        }

        return false;
    }

    @Override
    protected boolean isKeyboardNavigationSelected()
    {
        if (this.listWidget != null)
        {
            DataListEntrySelectionHandler<? extends TYPE> handler = this.listWidget.getEntrySelectionHandler();
            return handler != null && handler.getKeyboardNavigationIndex() == this.getListIndex();
        }

        return false;
    }

    @Nullable
    public Consumer<? extends BaseDataListEntryWidget<TYPE>> createWidgetInitializer(List<TYPE> dataList)
    {
        return null;
    }
}
