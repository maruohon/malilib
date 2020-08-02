package fi.dy.masa.malilib.gui.widget;

public class WidgetListEntryBase extends WidgetContainer
{
    protected final int listIndex;
    protected boolean isOdd;

    public WidgetListEntryBase(int x, int y, int width, int height, int listIndex)
    {
        super(x, y, width, height);

        this.listIndex = listIndex;
    }

    public void setIsOdd(boolean isOdd)
    {
        this.isOdd = isOdd;
    }

    public int getListIndex()
    {
        return this.listIndex;
    }

    /**
     * Returns true if this widget can be selected by clicking at the given point
     */
    public boolean canSelectAt(int mouseX, int mouseY, int mouseButton)
    {
        return true;
    }

    public void render(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId, boolean selected)
    {
        this.render(mouseX, mouseY, isActiveGui, hoveredWidgetId);
    }
}
