package fi.dy.masa.malilib.gui.widget;

import fi.dy.masa.malilib.MaLiLibConfigs;

public class ScreenContext
{
    public final int mouseX;
    public final int mouseY;
    public final int hoveredWidgetId;
    public final boolean isActiveScreen;
    public final boolean debugRenderAll;
    public final boolean debugInfoAlways;

    public ScreenContext(int mouseX, int mouseY, int hoveredWidgetId, boolean isActiveScreen)
    {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.hoveredWidgetId = hoveredWidgetId;
        this.isActiveScreen = isActiveScreen;
        this.debugRenderAll = MaLiLibConfigs.Debug.GUI_DEBUG_ALL.getBooleanValue();
        this.debugInfoAlways = MaLiLibConfigs.Debug.GUI_DEBUG_INFO_ALWAYS.getBooleanValue();
    }
}
