package fi.dy.masa.malilib.gui.widget.list.entry;

import fi.dy.masa.malilib.gui.widget.list.DataListWidget;

public class StringListEditEntryWidget extends BaseStringListEditEntryWidget<String>
{
    public StringListEditEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                     String initialValue, String defaultValue, DataListWidget<String> parent)
    {
        super(x, y, width, height, listIndex, originalListIndex, initialValue, defaultValue, (v) -> v, (s) -> s, parent);
    }

    @Override
    protected String getNewDataEntry()
    {
        return "";
    }
}
