package fi.dy.masa.malilib.gui.util;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.render.RenderContext;

public class ScreenContext extends RenderContext
{
    public final int mouseX;
    public final int mouseY;
    public final int hoveredWidgetId;
    public final boolean isActiveScreen;

    public ScreenContext(int mouseX, int mouseY, int hoveredWidgetId, boolean isActiveScreen)
    {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.hoveredWidgetId = hoveredWidgetId;
        this.isActiveScreen = isActiveScreen;
    }

    public boolean getDebugRenderAll()
    {
        return MaLiLibConfigs.Debug.GUI_DEBUG_ALL.getBooleanValue();
    }

    public boolean getDebugInfoAlways()
    {
        return MaLiLibConfigs.Debug.GUI_DEBUG_INFO_ALWAYS.getBooleanValue();
    }

    public boolean matches(int mouseX, int mouseY, boolean isActiveScreen, int hoveredWidgetId)
    {
        return this.isActiveScreen == isActiveScreen &&
               this.hoveredWidgetId == hoveredWidgetId &&
               this.mouseX == mouseX &&
               this.mouseY == mouseY;
    }
}
