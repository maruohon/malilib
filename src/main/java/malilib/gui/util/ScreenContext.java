package malilib.gui.util;

import malilib.MaLiLibConfigs;
import malilib.render.RenderContext;
import net.minecraft.client.util.math.MatrixStack;

public class ScreenContext extends RenderContext
{
    public final int mouseX;
    public final int mouseY;
    public final int hoveredWidgetId;
    public final boolean isActiveScreen;

    public ScreenContext(int mouseX, int mouseY,
                         int hoveredWidgetId, boolean isActiveScreen,
                         MatrixStack matrixStack)
    {
        super (matrixStack);

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
