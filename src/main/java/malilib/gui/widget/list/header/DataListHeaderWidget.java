package malilib.gui.widget.list.header;

import malilib.gui.widget.ContainerWidget;
import malilib.gui.widget.list.DataListWidget;

public class DataListHeaderWidget<DATATYPE> extends ContainerWidget
{
    protected final DataListWidget<DATATYPE> listWidget;

    public DataListHeaderWidget(int width, int height, DataListWidget<DATATYPE> listWidget)
    {
        super(width, height);

        this.listWidget = listWidget;
    }

    public void setHeaderWidgetSize(int width, int height)
    {
        width = width > 0 ? width : this.getWidth();
        height = height > 0 ? height : this.getHeight();

        this.setSize(width, height);
    }
}
