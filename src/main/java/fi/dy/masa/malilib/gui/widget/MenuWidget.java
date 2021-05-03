package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuWidget extends ContainerWidget
{
    protected final List<BackgroundWidget> menuEntries = new ArrayList<>();
    protected boolean renderEntryBackground = true;
    protected int hoveredEntryBackgroundColor = 0xFF206060;
    protected int normalEntryBackgroundColor = 0xFF000000;

    public MenuWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height);

        this.setNormalBorderColor(0xFFC0C0C0);
        this.setNormalBorderWidth(1);
        this.setRenderNormalBorder(true);
    }

    public void setMenuEntries(BackgroundWidget... menuEntries)
    {
        this.setMenuEntries(Arrays.asList(menuEntries));
    }

    public void setMenuEntries(List<BackgroundWidget> menuEntries)
    {
        this.menuEntries.clear();
        this.menuEntries.addAll(menuEntries);

        this.updateSize();
        this.updateSubWidgetsToGeometryChanges();
        this.reAddSubWidgets();
    }

    public MenuWidget setRenderEntryBackground(boolean renderEntryBackground)
    {
        this.renderEntryBackground = renderEntryBackground;
        return this;
    }

    public MenuWidget setNormalEntryBackgroundColor(int normalEntryBackgroundColor)
    {
        this.normalEntryBackgroundColor = normalEntryBackgroundColor;
        return this;
    }

    public MenuWidget setHoveredEntryBackgroundColor(int hoveredEntryBackgroundColor)
    {
        this.hoveredEntryBackgroundColor = hoveredEntryBackgroundColor;
        return this;
    }

    @Override
    public void updateSize()
    {
        int width = 0;
        int height = 0;

        for (InteractableWidget widget : this.menuEntries)
        {
            width = Math.max(width, widget.getWidth());
            height += widget.getHeight();
        }

        this.setWidth(width + 2);
        this.setHeight(height + 2);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        for (BackgroundWidget widget : this.menuEntries)
        {
            this.addWidget(widget);

            if (this.renderEntryBackground)
            {
                widget.setNormalBackgroundColor(this.normalEntryBackgroundColor);
                widget.setHoveredBackgroundColor(this.hoveredEntryBackgroundColor);
                widget.setRenderNormalBackground(true);
                widget.setRenderHoverBackground(true);
            }
            else
            {
                widget.setRenderNormalBackground(false);
                widget.setRenderHoverBackground(false);
            }
        }
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int x = this.getX() + 1;
        int y = this.getY() + 1;
        int width = this.getWidth() - 2;

        for (InteractableWidget widget : this.menuEntries)
        {
            widget.setPosition(x, y);
            widget.setWidth(width);
            y += widget.getHeight();
        }
    }
}
