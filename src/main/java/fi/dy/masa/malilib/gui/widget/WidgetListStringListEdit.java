package fi.dy.masa.malilib.gui.widget;

import fi.dy.masa.malilib.config.option.StringListConfig;
import fi.dy.masa.malilib.gui.GuiStringListEdit;

public class WidgetListStringListEdit extends WidgetListConfigOptionsBase<String>
{
    protected final GuiStringListEdit parent;

    public WidgetListStringListEdit(int x, int y, int width, int height, int configWidth, GuiStringListEdit parent)
    {
        super(x, y, width, height, configWidth);

        this.parent = parent;
    }

    @Override
    public GuiStringListEdit getParentScreen()
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
    protected WidgetStringListEditEntry createListEntryWidget(int x, int y, int listIndex)
    {
        StringListConfig config = this.parent.getConfig();

        if (listIndex >= 0 && listIndex < config.getStrings().size())
        {
            String defaultValue = config.getDefaultStrings().size() > listIndex ? config.getDefaultStrings().get(listIndex) : "";

            return new WidgetStringListEditEntry(x, y, this.entryWidgetWidth, this.entryWidgetFixedHeight,
                                                 listIndex, config.getStrings().get(listIndex), defaultValue, this);
        }
        else
        {
            return new WidgetStringListEditEntry(x, y, this.entryWidgetWidth, this.entryWidgetFixedHeight,
                                                 listIndex, "", "", this);
        }
    }
}
