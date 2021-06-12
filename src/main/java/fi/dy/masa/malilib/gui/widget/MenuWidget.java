package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuWidget extends ContainerWidget
{
    protected final List<InteractableWidget> menuEntries = new ArrayList<>();
    protected boolean renderEntryBackground = true;
    protected int hoveredEntryBackgroundColor = 0xFF206060;
    protected int normalEntryBackgroundColor = 0xFF000000;

    public MenuWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height);

        this.getBorderRenderer().getNormalSettings().setBorderWidthAndColor(1, 0xFFC0C0C0);
    }

    public void setMenuEntries(InteractableWidget... menuEntries)
    {
        this.setMenuEntries(Arrays.asList(menuEntries));
    }

    public void setMenuEntries(List<InteractableWidget> menuEntries)
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

        for (InteractableWidget widget : this.menuEntries)
        {
            this.addWidget(widget);

            if (this.renderEntryBackground)
            {
                widget.getBackgroundRenderer().getNormalSettings().setEnabledAndColor(true, this.normalEntryBackgroundColor);
                widget.getBackgroundRenderer().getHoverSettings().setEnabledAndColor(true, this.hoveredEntryBackgroundColor);
            }
            else
            {
                widget.getBackgroundRenderer().getNormalSettings().setEnabled(false);
                widget.getBackgroundRenderer().getHoverSettings().setEnabled(false);
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
