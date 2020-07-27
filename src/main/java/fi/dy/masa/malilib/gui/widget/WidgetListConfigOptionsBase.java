package fi.dy.masa.malilib.gui.widget;

public abstract class WidgetListConfigOptionsBase<TYPE, WIDGET extends WidgetConfigOptionBase<TYPE>> extends WidgetListBase<TYPE, WIDGET>
{
    protected boolean configsModified;
    protected int maxLabelWidth;
    protected int configWidth;

    public WidgetListConfigOptionsBase(int x, int y, int width, int height, int configWidth)
    {
        super(x, y, width, height, null);

        this.configWidth = configWidth;
        this.browserEntryHeight = 22;
    }

    @Override
    protected void reCreateListEntryWidgets()
    {
        this.applyPendingModifications();

        // Check for modifications before re-creating the widgets.
        // This is needed for the keybind settings, as re-creating
        // those widgets wipes the cached initial settings value.
        if (this.configsModified == false)
        {
            this.wereConfigsModified();
        }

        super.reCreateListEntryWidgets();
    }

    public void markConfigsModified()
    {
        this.configsModified = true;
    }

    public boolean wereConfigsModified()
    {
        // First check the cached value, this gets updated when scrolling
        // the list and the widgets get re-created.
        if (this.configsModified)
        {
            return true;
        }

        for (WidgetConfigOptionBase<TYPE> widget : this.listWidgets)
        {
            if (widget.wasConfigModified())
            {
                this.configsModified = true;
                return true;
            }
        }

        return false;
    }

    public void clearConfigsModifiedFlag()
    {
        this.configsModified = false;
    }

    public void applyPendingModifications()
    {
        for (WidgetConfigOptionBase<TYPE> widget : this.listWidgets)
        {
            if (widget.hasPendingModifications())
            {
                widget.applyNewValueToConfig();
                // Cache the modified status before scrolling etc. and thus re-creating the widgets
                this.configsModified = true;
            }
        }
    }
}
