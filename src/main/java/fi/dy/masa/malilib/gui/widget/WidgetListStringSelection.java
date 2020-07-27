package fi.dy.masa.malilib.gui.widget;

import java.util.List;
import java.util.function.Supplier;

public class WidgetListStringSelection extends WidgetListBase<String, WidgetStringListEntry>
{
    protected final Supplier<List<String>> stringProvider;

    public WidgetListStringSelection(int x, int y, int width, int height, Supplier<List<String>> stringProvider)
    {
        super(x, y, width, height, null);

        this.allowMultiSelection = true;
        this.stringProvider = stringProvider;
        this.browserEntryHeight = 22;
    }

    @Override
    protected void refreshBrowserEntries()
    {
        this.listContents.clear();
        this.listContents.addAll(this.stringProvider.get());

        this.reCreateListEntryWidgets();
    }

    @Override
    protected WidgetStringListEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd, String entry)
    {
        return new WidgetStringListEntry(x, y, this.browserEntryWidth, this.getBrowserEntryHeightFor(entry), isOdd, entry, listIndex);
    }
}
