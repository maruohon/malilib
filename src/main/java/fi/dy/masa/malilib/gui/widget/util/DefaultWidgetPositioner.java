package fi.dy.masa.malilib.gui.widget.util;

import fi.dy.masa.malilib.gui.widget.InteractableWidget;

public class DefaultWidgetPositioner implements WidgetPositioner
{
    @Override
    public void positionWidget(InteractableWidget widget, int defaultX, int defaultY, int defaultWidth)
    {
        widget.setPosition(defaultX, defaultY);
        widget.setWidth(defaultWidth);
    }
}
