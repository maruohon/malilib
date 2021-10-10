package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.interfaces.IStringListProvider;

import java.util.Collection;

public class WidgetListStringSelection extends WidgetListBase<String, WidgetStringListEntry>
{
    protected final IStringListProvider stringProvider;

    public WidgetListStringSelection(int x, int y, int width, int height, IStringListProvider stringProvider)
    {
        super(x, y, width, height, null);

        this.allowMultiSelection = true;
        this.stringProvider = stringProvider;
        this.browserEntryHeight = 22;
    }

    @Override
    protected Collection<String> getAllEntries()
    {
        return this.stringProvider.getStrings();
    }

    @Override
    protected WidgetStringListEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd, String entry)
    {
        return new WidgetStringListEntry(x, y, this.browserEntryWidth, this.getBrowserEntryHeightFor(entry), isOdd, entry, listIndex);
    }
}
