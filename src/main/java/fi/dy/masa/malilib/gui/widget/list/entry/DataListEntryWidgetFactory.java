package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;

public interface DataListEntryWidgetFactory<DATATYPE>
{
    /**
     * Creates the list entry widget for the given data entry and list index
     */
    @Nullable
    BaseListEntryWidget createWidget(DATATYPE data, DataListEntryWidgetData constructData);
}
