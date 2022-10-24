package malilib.gui.util;

import malilib.gui.widget.BaseWidget;
import malilib.util.MathUtils;

public enum DraggedCorner
{
    BOTTOM_RIGHT (false, false),
    BOTTOM_LEFT  (true, false),
    TOP_LEFT     (true, true),
    TOP_RIGHT    (false, true);

    private final boolean xStart;
    private final boolean yStart;

    DraggedCorner(boolean xStart, boolean yStart)
    {
        this.xStart = xStart;
        this.yStart = yStart;
    }

    public void updateWidgetSize(int mouseX, int mouseY, int gridSize, BaseWidget widget)
    {
        if (gridSize > 1)
        {
            mouseX = MathUtils.roundDown(mouseX, gridSize);
            mouseY = MathUtils.roundDown(mouseY, gridSize);
        }

        int width = this.xStart ? widget.getRight() - mouseX : mouseX - widget.getX();
        int height = this.yStart ? widget.getBottom() - mouseY : mouseY - widget.getY();

        if (this.xStart)
        {
            widget.setX(mouseX);
        }

        if (this.yStart)
        {
            widget.setY(mouseY);
        }

        widget.setSize(width, height);
    }

    public static DraggedCorner getFor(int mouseX, int mouseY, BaseWidget widget)
    {
        int middleX = widget.getX() + widget.getWidth() / 2;
        int middleY = widget.getY() + widget.getHeight() / 2;

        if (mouseX > middleX)
        {
            if (mouseY > middleY)
            {
                return BOTTOM_RIGHT;
            }
            else
            {
                return TOP_RIGHT;
            }
        }
        else
        {
            if (mouseY > middleY)
            {
                return BOTTOM_LEFT;
            }
            else
            {
                return TOP_LEFT;
            }
        }
    }
}
