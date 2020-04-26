package fi.dy.masa.malilib.gui.widget;

public class WidgetListEntryBase extends WidgetContainer
{
    protected final int listIndex;

    public WidgetListEntryBase(int x, int y, int width, int height, int listIndex)
    {
        super(x, y, width, height);

        this.listIndex = listIndex;
    }

    public int getListIndex()
    {
        return this.listIndex;
    }

    public void render(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId, boolean selected)
    {
        this.render(mouseX, mouseY, isActiveGui, hoveredWidgetId);
    }
}
