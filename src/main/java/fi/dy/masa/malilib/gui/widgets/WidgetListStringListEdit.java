package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.config.IConfigStringList;
import fi.dy.masa.malilib.gui.GuiStringListEdit;

public class WidgetListStringListEdit extends WidgetListConfigOptionsBase<String, WidgetStringListEditEntry>
{
    protected final GuiStringListEdit parent;

    public WidgetListStringListEdit(int x, int y, int width, int height, int configWidth, GuiStringListEdit parent)
    {
        super(x, y, width, height, configWidth);

        this.parent = parent;
    }

    @Override
    public GuiStringListEdit getParent()
    {
        return this.parent;
    }

    @Override
    protected void refreshBrowserEntries()
    {
        this.listContents.clear();
        this.listContents.addAll(this.parent.getConfig().getStrings());

        this.reCreateListEntryWidgets();
    }

    @Override
    protected void reCreateListEntryWidgets()
    {
        // Add a dummy entry that allows adding the first actual string to the list
        if (this.listContents.size() == 0)
        {
            this.listWidgets.clear();
            this.maxVisibleBrowserEntries = 1;

            int x = this.posX + 2;
            int y = this.posY + 4 + this.browserEntriesOffsetY;

            this.listWidgets.add(this.createListEntryWidget(x, y, -1, false, ""));
            this.scrollBar.setMaxValue(0);
        }
        else
        {
            super.reCreateListEntryWidgets();
        }
    }

    @Override
    protected WidgetStringListEditEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd, String entry)
    {
        IConfigStringList config = this.parent.getConfig();

        if (listIndex >= 0 && listIndex < config.getStrings().size())
        {
            String defaultValue = config.getDefaultStrings().size() > listIndex ? config.getDefaultStrings().get(listIndex) : "";

            return new WidgetStringListEditEntry(x, y, this.browserEntryWidth, this.browserEntryHeight,
                    listIndex, isOdd, config.getStrings().get(listIndex), defaultValue, this);
        }
        else
        {
            return new WidgetStringListEditEntry(x, y, this.browserEntryWidth, this.browserEntryHeight,
                    listIndex, isOdd, "", "", this);
        }
    }
}
