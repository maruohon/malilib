package fi.dy.masa.malilib.gui.widget.list;

public class StringListWidget// extends WidgetListConfigOptionsBase<String>
{
    /*
    protected final StringListEditScreen parent;

    public StringListWidget(int x, int y, int width, int height, int configWidth, StringListEditScreen parent)
    {
        super(x, y, width, height, configWidth);

        this.parent = parent;
    }

    @Override
    public StringListEditScreen getParentScreen()
    {
        return this.parent;
    }

    @Override
    public void refreshEntries()
    {
        this.listContents.clear();
        this.listContents.addAll(this.parent.getConfig().getStrings());

        this.reCreateListEntryWidgets();
    }

    @Override
    protected void reCreateListEntryWidgets()
    {
        // Add a dummy entry that allows adding the first actual string to the list
        if (this.getTotalListEntryCount() == 0)
        {
            this.listWidgets.clear();
            this.visibleListEntries = 1;

            int x = this.getX() + 2;
            int y = this.getY() + 4 + this.entryWidgetsStartY;

            this.listWidgets.add(this.createListEntryWidget(x, y, -1));
            this.scrollBar.setMaxValue(0);
        }
        else
        {
            super.reCreateListEntryWidgets();
        }
    }

    @Override
    protected StringListEditEntryWidget createListEntryWidget(int x, int y, int listIndex)
    {
        StringListConfig config = this.parent.getConfig();

        if (listIndex >= 0 && listIndex < config.getStrings().size())
        {
            String defaultValue = config.getDefaultStrings().size() > listIndex ? config.getDefaultStrings().get(listIndex) : "";

            return new StringListEditEntryWidget(x, y, this.entryWidgetWidth, this.entryWidgetFixedHeight,
                                                 listIndex, config.getStrings().get(listIndex), defaultValue, this);
        }
        else
        {
            return new StringListEditEntryWidget(x, y, this.entryWidgetWidth, this.entryWidgetFixedHeight,
                                                 listIndex, "", "", this);
        }
    }
    */
}
