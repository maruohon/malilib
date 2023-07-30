package malilib.gui.util;

import malilib.render.ShapeRenderUtils;
import malilib.util.data.EdgeInt;

public class BorderRenderer
{
    protected final BorderSettings normalSettings = new BorderSettings(0xFFC0C0C0);
    protected final BorderSettings hoverSettings = new BorderSettings(0xFFFFFFFF);

    public BorderSettings getNormalSettings()
    {
        return this.normalSettings;
    }

    public BorderSettings getHoverSettings()
    {
        return this.hoverSettings;
    }

    public BorderSettings getActiveSettings(boolean hovered)
    {
        return hovered && this.hoverSettings.isEnabled() ? this.hoverSettings : this.normalSettings;
    }

    public void renderBorderIfEnabled(int x, int y, float z,
                                      int width, int height,
                                      BorderSettings settings, ScreenContext ctx)
    {
        if (settings.isEnabled())
        {
            this.renderBorder(x, y, z, width, height, settings, ctx);
        }
    }

    public void renderBorder(int x, int y, float z,
                             int width, int height,
                             BorderSettings settings, ScreenContext ctx)
    {
        EdgeInt color = settings.getColor();
        int borderWidth = settings.getActiveBorderWidth();
        ShapeRenderUtils.renderOutline(x, y, z, width, height, borderWidth, color, ctx);
    }
}
