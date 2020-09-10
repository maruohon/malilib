package fi.dy.masa.malilib.gui.widget.list.entry;

import fi.dy.masa.malilib.gui.widget.ContainerWidget;

public class BaseListEntryWidget extends ContainerWidget
{
    protected final int listIndex;
    protected final int originalListIndex;
    protected boolean isOdd;

    public BaseListEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex)
    {
        super(x, y, width, height);

        this.listIndex = listIndex;
        this.originalListIndex = originalListIndex;

        this.setIsOdd((listIndex & 0x1) != 0);
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
     * This gets called from BaseListWidget before the widgets
     * are cleared before being re-created. This allows for example
     * config widgets to save their changes before being destroyed.
     */
    public void onAboutToDestroy()
    {
    }

    /**
     * Focuses this widget.
     * <br><br>
     * What this means is defined by the implementation.
     * In most cases it would be for example focusing a text field
     * in a newly created entry widget.
     */
    public void focusWidget()
    {
    }

    /**
     * Returns true if this widget can be selected by clicking at the given point
     */
    public boolean canSelectAt(int mouseX, int mouseY, int mouseButton)
    {
        return true;
    }

    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId, boolean selected)
    {
        this.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hoveredWidgetId);
    }
}
