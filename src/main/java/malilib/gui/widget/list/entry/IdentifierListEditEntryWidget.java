package malilib.gui.widget.list.entry;

import malilib.util.data.Identifier;

public class IdentifierListEditEntryWidget extends BaseStringListEditEntryWidget<Identifier>
{
    public IdentifierListEditEntryWidget(Identifier data,
                                         DataListEntryWidgetData constructData,
                                         Identifier defaultValue)
    {
        super(data, constructData, defaultValue, Identifier::toString, Identifier::new);
        this.newEntryFactory = () -> new Identifier("minecraft:foo");
    }
}
