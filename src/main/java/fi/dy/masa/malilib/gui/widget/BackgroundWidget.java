package fi.dy.masa.malilib.gui.widget;

import fi.dy.masa.malilib.gui.util.BackgroundRenderer;
import fi.dy.masa.malilib.gui.util.BackgroundSettings;
import fi.dy.masa.malilib.gui.util.BorderRenderer;
import fi.dy.masa.malilib.gui.util.BorderSettings;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.render.RenderUtils;

public class BackgroundWidget extends BaseWidget
{
    protected final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    protected final BorderRenderer borderRenderer = new BorderRenderer();

    public BackgroundWidget(int width, int height)
    {
        super(width, height);
    }

    public BackgroundWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    public BackgroundRenderer getBackgroundRenderer()
    {
        return this.backgroundRenderer;
    }

    public BorderRenderer getBorderRenderer()
    {
        return this.borderRenderer;
    }

    protected int getBackgroundWidth(boolean hovered, ScreenContext ctx)
    {
        return this.getWidth();
    }

    protected int getBackgroundHeight(boolean hovered, ScreenContext ctx)
    {
        return this.getHeight();
    }

    public boolean isHoveredForRender(ScreenContext ctx)
    {
        return false;
    }

    protected BackgroundSettings getActiveBackgroundSettings(ScreenContext ctx)
    {
        boolean hovered = this.isHoveredForRender(ctx);
        return this.getBackgroundRenderer().getActiveSettings(hovered);
    }

    protected BorderSettings getActiveBorderSettings(ScreenContext ctx)
    {
        boolean hovered = this.isHoveredForRender(ctx);
        return this.getBorderRenderer().getActiveSettings(hovered);
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        this.renderWidgetBackgroundAndBorder(x, y, z, ctx);
        super.renderAt(x, y, z, ctx);
    }

    protected void renderWidgetBackgroundAndBorder(int x, int y, float z, ScreenContext ctx)
    {
        boolean hovered = this.isHoveredForRender(ctx);
        int width = this.getBackgroundWidth(hovered, ctx);
        int height = this.getBackgroundHeight(hovered, ctx);

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.setupBlend();

        BackgroundSettings backgroundSettings = this.getActiveBackgroundSettings(ctx);
        this.getBackgroundRenderer().renderBackgroundIfEnabled(x, y, z, width, height, backgroundSettings, ctx);

        BorderSettings borderSettings = this.getActiveBorderSettings(ctx);
        this.getBorderRenderer().renderBorderIfEnabled(x, y, z + 0.0125f, width, height, borderSettings, ctx);
    }
}
