package fi.dy.masa.malilib.gui.widget.util;

import fi.dy.masa.malilib.gui.widget.WidgetDataListHeader;
import fi.dy.masa.malilib.gui.widget.WidgetListData;

public interface ListHeaderWidgetFactory<DATATYPE>
{
    /**
     * Creates the widget for the given data entry index in the data list
     * @param x The screen x position this widget will be created at.
     * @param y The screen y position this widget will be created at.
     * @param width The width of the created widget
     * @param height The height of the created widget. Note: -1 is used to indicate automatic height, decided by the widget itself.
     * @param listWidget The parent list widget that is creating this widget.
     * @return
     */
    WidgetDataListHeader createWidget(int x, int y, int width, int height, WidgetListData<DATATYPE> listWidget);
}
