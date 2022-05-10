package fi.dy.masa.malilib.gui.util;

import net.minecraft.client.util.math.MatrixStack;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.render.RenderContext;

public class ScreenContext extends RenderContext
{
    public final int mouseX;
    public final int mouseY;
    public final int hoveredWidgetId;
    public final boolean isActiveScreen;
    public final MatrixStack matrices;

    public ScreenContext(int mouseX, int mouseY, int hoveredWidgetId, boolean isActiveScreen, MatrixStack matrices)
    {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.hoveredWidgetId = hoveredWidgetId;
        this.isActiveScreen = isActiveScreen;
        this.matrices = matrices;
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
