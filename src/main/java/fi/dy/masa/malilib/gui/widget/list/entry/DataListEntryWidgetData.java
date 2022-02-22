package fi.dy.masa.malilib.gui.widget.list.entry;

import fi.dy.masa.malilib.gui.widget.list.DataListWidget;

public class DataListEntryWidgetData
{
    public int x;
    public int y;
    public int width;
    public int height;
    public int listIndex;
    public int originalListIndex;
    public DataListWidget<?> listWidget;

    public DataListEntryWidgetData(int x, int y, int width, int height,
                                   int listIndex, int originalListIndex,
                                   DataListWidget<?> listWidget)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.listIndex = listIndex;
        this.originalListIndex = originalListIndex;
        this.listWidget = listWidget;
    }
}
