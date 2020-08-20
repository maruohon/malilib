package fi.dy.masa.malilib.gui.widget;

import java.util.List;
import fi.dy.masa.malilib.gui.icon.BaseIcon;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;

public class CyclableContainerWidget extends ContainerWidget
{
    protected final List<? extends BaseWidget> cyclableWidgets;
    protected final GenericButton leftButton;
    protected final GenericButton rightButton;
    protected int startIndex;
    protected int widgetGap = 2;

    public CyclableContainerWidget(int x, int y, int width, int height, List<? extends BaseWidget> cyclableWidgets)
    {
        super(x, y, width, height);

        this.cyclableWidgets = cyclableWidgets;
        this.leftButton = new GenericButton(x, y, 12, 20, "", BaseIcon.MEDIUM_ARROW_LEFT, "malilib.gui.button.hover.cycle_widgets_left");
        this.leftButton.setRenderBackground(true);
        this.leftButton.setActionListener((btn, mbtn) -> {
            this.startIndex = Math.max(this.startIndex - 1, 0);
            this.reAddSubWidgets();
        });
        this.rightButton = new GenericButton(x, y, 12, 20, "", BaseIcon.MEDIUM_ARROW_RIGHT, "malilib.gui.button.hover.cycle_widgets_right");
        this.rightButton.setRenderBackground(true);
        this.rightButton.setActionListener((btn, mbtn) -> {
            this.startIndex = Math.min(this.startIndex + 1, this.getMaxStartIndex());
            this.reAddSubWidgets();
        });
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

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.reAddFittingWidgets();
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();
    }

    protected int getMaxStartIndex()
    {
        int allWidgetsWidth = this.getAllWidgetsWidth();

        if (allWidgetsWidth <= this.getWidth())
        {
            return 0;
        }

        int usableWidth = this.getWidth() - this.leftButton.getWidth() - this.rightButton.getWidth() - this.widgetGap * 2;
        int index = this.cyclableWidgets.size();

        while (index > 0)
        {
            BaseWidget widget = this.cyclableWidgets.get(index - 1);
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

    protected int getAllWidgetsWidth()
    {
        int widgetCount = this.cyclableWidgets.size();
        int allWidgetsWidth = 0;

        for (BaseWidget widget : this.cyclableWidgets)
        {
            allWidgetsWidth += widget.getWidth();
        }

        if (widgetCount > 1)
        {
            allWidgetsWidth += (widgetCount - 1) * this.widgetGap;
        }

        return allWidgetsWidth;
    }

    protected void reAddFittingWidgets()
    {
        int width = this.getWidth();
        int allWidgetsWidth = this.getAllWidgetsWidth();

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

            this.rightButton.setPosition(this.getRight() - this.rightButton.getWidth(), y);
            maxX = this.rightButton.getX() - this.widgetGap;
            this.rightButton.setEnabled(this.startIndex < this.getMaxStartIndex());

            this.addWidget(this.leftButton);
            this.addWidget(this.rightButton);
        }

        for (int index = this.startIndex; index < this.cyclableWidgets.size(); ++index)
        {
            BaseWidget widget = this.cyclableWidgets.get(index);

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
