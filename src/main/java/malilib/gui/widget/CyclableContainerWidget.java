package malilib.gui.widget;

import java.util.List;
import malilib.gui.icon.DefaultIcons;
import malilib.gui.widget.button.GenericButton;

public class CyclableContainerWidget extends ContainerWidget
{
    protected final List<? extends InteractableWidget> cyclableWidgets;
    protected final GenericButton leftButton;
    protected final GenericButton rightButton;
    protected int startIndex;
    protected int widgetGap = 2;

    public CyclableContainerWidget(int width, int height, List<? extends InteractableWidget> cyclableWidgets)
    {
        super(width, height);

        this.cyclableWidgets = cyclableWidgets;

        this.leftButton = GenericButton.create(12, 20, DefaultIcons.MEDIUM_ARROW_LEFT);
        this.leftButton.translateAndAddHoverString("malilibdev.hover.button.cycle_left");
        this.leftButton.setRenderButtonBackgroundTexture(true);
        this.leftButton.setActionListener(this::cycleLeft);

        this.rightButton = GenericButton.create(12, 20, DefaultIcons.MEDIUM_ARROW_RIGHT);
        this.rightButton.translateAndAddHoverString("malilibdev.hover.button.cycle_right");
        this.rightButton.setRenderButtonBackgroundTexture(true);
        this.rightButton.setActionListener(this::cycleRight);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();
        this.reAddFittingWidgets();
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();
    }

    public CyclableContainerWidget setWidgetGap(int gap)
    {
        this.widgetGap = gap;
        return this;
    }

    public int getStartIndex()
    {
        return this.startIndex;
    }

    public void setStartIndex(int index)
    {
        this.startIndex = index;
    }

    protected void cycleLeft()
    {
        this.startIndex = Math.max(this.startIndex - 1, 0);
        this.reAddSubWidgets();
    }

    protected void cycleRight()
    {
        this.startIndex = Math.min(this.startIndex + 1, this.getMaxStartIndex());
        this.reAddSubWidgets();
    }

    protected int getMaxStartIndex()
    {
        int allWidgetsWidth = this.getTotalCyclableWidgetsWidth();

        if (allWidgetsWidth <= this.getWidth())
        {
            return 0;
        }

        int usableWidth = this.getWidth() - this.leftButton.getWidth() - this.rightButton.getWidth() - this.widgetGap * 2;
        int index = this.cyclableWidgets.size();

        while (index > 0)
        {
            InteractableWidget widget = this.cyclableWidgets.get(index - 1);
            int width = widget.getWidth();

            if (width > usableWidth)
            {
                break;
            }

            usableWidth -= (width + this.widgetGap);
            --index;
        }

        return index;
    }

    protected int getTotalCyclableWidgetsWidth()
    {
        int widgetCount = this.cyclableWidgets.size();
        int totalWidth = 0;

        for (InteractableWidget widget : this.cyclableWidgets)
        {
            totalWidth += widget.getWidth();
        }

        if (widgetCount > 1)
        {
            totalWidth += (widgetCount - 1) * this.widgetGap;
        }

        return totalWidth;
    }

    protected void reAddFittingWidgets()
    {
        int width = this.getWidth();
        int allWidgetsWidth = this.getTotalCyclableWidgetsWidth();

        int x = this.getX();
        int y = this.getY();
        int maxX = this.getRight();

        this.startIndex = Math.min(this.startIndex, this.getMaxStartIndex());

        // All the widgets can't fit, add the cycle buttons
        if (allWidgetsWidth > width)
        {
            this.leftButton.setPosition(x, y);
            x = this.leftButton.getRight() + this.widgetGap;
            this.leftButton.setEnabled(this.startIndex > 0);

            this.rightButton.setPosition(maxX - this.rightButton.getWidth(), y);
            maxX = this.rightButton.getX() - this.widgetGap;
            this.rightButton.setEnabled(this.startIndex < this.getMaxStartIndex());

            this.addWidget(this.leftButton);
            this.addWidget(this.rightButton);
        }

        for (int index = this.startIndex; index < this.cyclableWidgets.size(); ++index)
        {
            InteractableWidget widget = this.cyclableWidgets.get(index);

            if (x + widget.getWidth() > maxX)
            {
                break;
            }

            widget.setPosition(x, y);
            x = widget.getRight() + this.widgetGap;
            this.addWidget(widget);
        }
    }
}
