package fi.dy.masa.malilib.gui.widget.list;

public interface ListEntryWidgetInitializer<DATATYPE>
{
    void onListContentsRefreshed(DataListWidget<DATATYPE> dataListWidget, int entryWidgetWidth);

    void applyToEntryWidgets(DataListWidget<DATATYPE> dataListWidget);
}
