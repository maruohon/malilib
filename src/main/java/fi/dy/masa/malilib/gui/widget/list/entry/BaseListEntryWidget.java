package fi.dy.masa.malilib.gui.widget.list.entry;

import fi.dy.masa.malilib.gui.position.EdgeInt;
import fi.dy.masa.malilib.gui.widget.ContainerWidget;
import fi.dy.masa.malilib.gui.widget.ScreenContext;
import fi.dy.masa.malilib.render.ShapeRenderUtils;

public class BaseListEntryWidget extends ContainerWidget
{
    protected final EdgeInt selectedBorderColor = new EdgeInt(0xFFFFFFFF);
    protected final int listIndex;
    protected final int originalListIndex;
    protected boolean isOdd;
    protected int keyboardNavigationHighlightColor = 0xFFFF5000;
    protected int selectedBackgroundColor = 0x50FFFFFF;

    public BaseListEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex)
    {
        super(x, y, width, height);

        this.listIndex = listIndex;
        this.originalListIndex = originalListIndex;

        this.setIsOdd((listIndex & 0x1) != 0);
        this.setRenderHoverBackground(true);
        this.setBackgroundColor(this.isOdd ? 0x20FFFFFF : 0x40FFFFFF);
        this.setBackgroundColorHovered(0x50FFFFFF);
    }

    public void setIsOdd(boolean isOdd)
    {
        this.isOdd = isOdd;
    }

    public void setSelectedBackgroundColor(int selectedBackgroundColor)
    {
        this.selectedBackgroundColor = selectedBackgroundColor;
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

    protected boolean isSelected()
    {
        return false;
    }

    protected boolean isKeyboardNavigationSelected()
    {
        return false;
    }

    @Override
    protected void renderBackgroundIfEnabled(int x, int y, float z, int width, int height,
                                             boolean hovered, ScreenContext ctx)
    {
        if (this.isSelected())
        {
            this.renderBackground(x, y, z, width, height, this.borderWidthNormal, this.selectedBackgroundColor, ctx);
        }
        else
        {
            super.renderBackgroundIfEnabled(x, y, z, width, height, hovered, ctx);
        }
    }

    @Override
    protected void renderBorderIfEnabled(int x, int y, float z, int width, int height,
                                         boolean hovered, ScreenContext ctx)
    {
        if (this.isSelected())
        {
            this.renderBorder(x, y, z, width, height, this.borderWidthNormal, this.selectedBorderColor, ctx);
        }
        else
        {
            super.renderBorderIfEnabled(x, y, z, width, height, hovered, ctx);
        }
    }

    protected void renderKeyboardNavigationHighlight(int x, int y, float z, int width, int height, ScreenContext ctx)
    {
        if (this.isKeyboardNavigationSelected())
        {
            ShapeRenderUtils.renderRectangle(x            , y, z, 2, height, this.keyboardNavigationHighlightColor);
            ShapeRenderUtils.renderRectangle(x + width - 2, y, z, 2, height, this.keyboardNavigationHighlightColor);
        }
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        super.renderAt(x, y, z, ctx);

        this.renderKeyboardNavigationHighlight(x, y, z, this.getWidth(), this.getHeight(), ctx);
    }
}
