package fi.dy.masa.malilib.gui.widget.list.entry;

import fi.dy.masa.malilib.gui.widget.list.DataListWidget;

public class StringListEditEntryWidget extends BaseStringListEditEntryWidget<String>
{
    public StringListEditEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                     String initialValue, String defaultValue, DataListWidget<String> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, initialValue, defaultValue, (v) -> v, (s) -> s, listWidget);

        this.newEntryFactory = () -> "";
    }
}
