package malilib.gui.widget.util;

import malilib.gui.widget.InteractableWidget;
import malilib.util.data.EdgeInt;

public class DefaultWidgetPositioner implements WidgetPositioner
{
    @Override
    public void positionWidget(InteractableWidget widget, int defaultX, int defaultY, int defaultWidth)
    {
        EdgeInt margin = widget.getMargin();
        int x = defaultX + margin.getLeft();
        int y = defaultY + margin.getTop();

        widget.setPosition(x, y);
        widget.setWidth(defaultWidth);
    }
}
