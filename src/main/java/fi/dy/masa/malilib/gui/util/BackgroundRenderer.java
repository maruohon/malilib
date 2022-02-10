package fi.dy.masa.malilib.gui.util;

import fi.dy.masa.malilib.render.ShapeRenderUtils;

public class BackgroundRenderer
{
    protected BackgroundSettings normalSettings = new BackgroundSettings(0xFF101010);
    protected BackgroundSettings hoverSettings = new BackgroundSettings(0x50FFFFFF);

    public BackgroundSettings getNormalSettings()
    {
        return this.normalSettings;
    }

    public BackgroundSettings getHoverSettings()
    {
        return this.hoverSettings;
    }

    public BackgroundSettings getActiveSettings(boolean hovered)
    {
        return hovered && this.hoverSettings.isEnabled() ? this.hoverSettings : this.normalSettings;
    }

    public void renderBackgroundIfEnabled(int x, int y, float z, int width, int height,
                                          BackgroundSettings settings, ScreenContext ctx)
    {
        if (settings.isEnabled())
        {
            this.renderBackground(x, y, z, width, height, settings, ctx);
        }
    }

    public void renderBackground(int x, int y, float z, int width, int height,
                                 BackgroundSettings settings, ScreenContext ctx)
    {
        ShapeRenderUtils.renderRectangle(x, y, z, width, height, settings.getColor());
    }
}
