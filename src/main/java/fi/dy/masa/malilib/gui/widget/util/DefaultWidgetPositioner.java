package fi.dy.masa.malilib.gui.widget.util;

import fi.dy.masa.malilib.gui.util.EdgeInt;
import fi.dy.masa.malilib.gui.widget.InteractableWidget;

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
