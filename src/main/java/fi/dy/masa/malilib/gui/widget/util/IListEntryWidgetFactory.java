package fi.dy.masa.malilib.gui.widgets.util;

import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;

public interface IListEntryWidgetFactory
{
    WidgetListEntryBase create(int x, int y, int width, int height, int listIndex, boolean isOdd);
}
