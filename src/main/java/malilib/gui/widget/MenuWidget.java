package malilib.gui.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;

import malilib.listener.EventListener;
import malilib.util.data.EdgeInt;

public class MenuWidget extends ContainerWidget
{
    protected final List<MenuEntryWidget> menuEntries = new ArrayList<>();
    @Nullable protected EventListener menuCloseHook;
    protected boolean renderEntryBackground = true;
    protected int hoveredEntryBackgroundColor = 0xFF206060;
    protected int normalEntryBackgroundColor = 0xFF000000;

    public MenuWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height);

        // Raise the z-level, so it's likely to be on top of all other widgets in the same screen
        this.zLevelIncrement = 50;

        this.setShouldReceiveOutsideClicks(true);
        this.getBorderRenderer().getNormalSettings().setBorderWidthAndColor(1, 0xFFC0C0C0);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        for (MenuEntryWidget widget : this.menuEntries)
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
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getX() + 1;
        int y = this.getY() + 1;
        int width = this.getWidth() - 2;

        for (MenuEntryWidget widget : this.menuEntries)
        {
            widget.setPosition(x, y);
            widget.setWidth(width);
            y += widget.getHeight();
        }
    }

    public void setMenuEntries(MenuEntryWidget... menuEntries)
    {
        this.setMenuEntries(Arrays.asList(menuEntries));
    }

    public void setMenuEntries(List<MenuEntryWidget> menuEntries)
    {
        this.menuEntries.clear();
        this.menuEntries.addAll(menuEntries);

        this.setCloseHookToEntries();
        this.updateSize();
        this.clampToScreen();
        this.updateSubWidgetPositions();
        this.reAddSubWidgets();
    }

    protected void setCloseHookToEntries()
    {
        for (MenuEntryWidget widget : this.menuEntries)
        {
            widget.setMenuCloseHook(this.menuCloseHook);
        }
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

    public void setMenuCloseHook(@Nullable EventListener menuCloseHook)
    {
        this.menuCloseHook = menuCloseHook;
        this.setCloseHookToEntries();
    }

    @Override
    public void updateSize()
    {
        int width = 0;
        int height = 0;

        for (MenuEntryWidget widget : this.menuEntries)
        {
            width = Math.max(width, widget.getWidth());
            height += widget.getHeight();
        }

        EdgeInt padding = this.padding;
        int borderWidth = this.getBorderRenderer().getNormalSettings().getActiveBorderWidth() * 2;
        width += padding.getHorizontalTotal() + borderWidth;
        height += padding.getVerticalTotal() + borderWidth;

        this.setSizeNoUpdate(width, height);
    }

    /* This won't do anything, since the entry widgets are consuming the click
    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.onMouseClicked(mouseX, mouseY, mouseButton);
        this.tryCloseMenu();
        return true;
    }

    public void tryCloseMenu()
    {
        if (this.menuCloseHook != null)
        {
            this.scheduleTask(this.menuCloseHook::onEvent);
        }
    }
    */
}
