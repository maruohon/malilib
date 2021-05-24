package fi.dy.masa.malilib.gui.util;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.render.RenderContext;

public class ScreenContext extends RenderContext
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

    public boolean matches(int mouseX, int mouseY, boolean isActiveScreen, int hoveredWidgetId)
    {
        return this.isActiveScreen == isActiveScreen &&
               this.hoveredWidgetId == hoveredWidgetId &&
               this.mouseX == mouseX &&
               this.mouseY == mouseY;
    }
}
