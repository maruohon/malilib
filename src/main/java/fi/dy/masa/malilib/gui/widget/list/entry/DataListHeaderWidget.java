package fi.dy.masa.malilib.gui.widget.list.entry;

import fi.dy.masa.malilib.gui.widget.ContainerWidget;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;

public class DataListHeaderWidget<DATATYPE> extends ContainerWidget
{
    protected final DataListWidget<DATATYPE> listWidget;

    public DataListHeaderWidget(int x, int y, int width, int height, DataListWidget<DATATYPE> listWidget)
    {
        super(x, y, width, height);

        this.listWidget = listWidget;
    }
}
