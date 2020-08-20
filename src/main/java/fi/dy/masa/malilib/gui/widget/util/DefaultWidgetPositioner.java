package fi.dy.masa.malilib.gui.widget.util;

import fi.dy.masa.malilib.gui.widget.BaseWidget;

public class DefaultWidgetPositioner implements WidgetPositioner
{
    @Override
    public void positionWidget(BaseWidget widget, int defaultX, int defaultY, int defaultWidth)
    {
        widget.setPosition(defaultX, defaultY);
        widget.setWidth(defaultWidth);
    }
}
