package malilib.gui.widget.list.entry;

public class StringListEditEntryWidget extends BaseStringListEditEntryWidget<String>
{
    public StringListEditEntryWidget(String data,
                                     DataListEntryWidgetData constructData,
                                     String defaultValue)
    {
        super(data, constructData, defaultValue, (v) -> v, (s) -> s);

        this.newEntryFactory = () -> "";
    }
}
