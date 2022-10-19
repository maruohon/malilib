package malilib.gui.widget.list.entry;

import javax.annotation.Nullable;
import malilib.gui.widget.list.DataListEntrySelectionHandler;
import malilib.gui.widget.list.DataListWidget;

public class BaseDataListEntryWidget<DATATYPE> extends BaseListEntryWidget
{
    protected final DATATYPE data;
    @Nullable protected final DataListWidget<DATATYPE> listWidget;

    @SuppressWarnings("unchecked")
    public BaseDataListEntryWidget(DATATYPE data,
                                   DataListEntryWidgetData constructData)
    {
        super(constructData);

        this.data = data;
        this.listWidget = (DataListWidget<DATATYPE>) constructData.listWidget;
    }

    public DATATYPE getData()
    {
        return this.data;
    }

    @Override
    protected boolean isSelected()
    {
        int listIndex = this.getDataListIndex();

        if (listIndex >= 0 && this.listWidget != null)
        {
            DataListEntrySelectionHandler<?> handler = this.listWidget.getEntrySelectionHandler();
            return handler != null && handler.isEntrySelected(listIndex);
        }

        return false;
    }

    @Override
    protected boolean isKeyboardNavigationSelected()
    {
        int listIndex = this.getDataListIndex();

        if (listIndex >= 0 && this.listWidget != null)
        {
            DataListEntrySelectionHandler<?> handler = this.listWidget.getEntrySelectionHandler();
            return handler != null && handler.getKeyboardNavigationIndex() == listIndex;
        }

        return false;
    }
}
