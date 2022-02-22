package fi.dy.masa.malilib.gui.widget.list.entry;

import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.widget.list.DataListEntrySelectionHandler;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;

public class BaseDataListEntryWidget<DATATYPE> extends BaseListEntryWidget
{
    @Nullable protected final DataListWidget<?> listWidget;
    protected final DATATYPE data;

    public BaseDataListEntryWidget(DATATYPE data,
                                   DataListEntryWidgetData constructData)
    {
        super(constructData);

        this.data = data;
        this.listWidget = constructData.listWidget;
    }

    public DATATYPE getData()
    {
        return this.data;
    }

    @Override
    protected boolean isSelected()
    {
        if (this.listWidget != null)
        {
            DataListEntrySelectionHandler<?> handler = this.listWidget.getEntrySelectionHandler();
            return handler != null && handler.isEntrySelected(this.getListIndex());
        }

        return false;
    }

    @Override
    protected boolean isKeyboardNavigationSelected()
    {
        if (this.listWidget != null)
        {
            DataListEntrySelectionHandler<?> handler = this.listWidget.getEntrySelectionHandler();
            return handler != null && handler.getKeyboardNavigationIndex() == this.getListIndex();
        }

        return false;
    }

    @Nullable
    public Consumer<? extends BaseDataListEntryWidget<DATATYPE>> createWidgetInitializer(List<DATATYPE> dataList)
    {
        return null;
    }
}
