package malilib.gui.widget;

import malilib.gui.util.BackgroundRenderer;
import malilib.gui.util.BackgroundSettings;
import malilib.gui.util.BorderRenderer;
import malilib.gui.util.BorderSettings;
import malilib.gui.util.ScreenContext;
import malilib.util.game.wrap.RenderWrap;

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

    @Override
    protected int getNonContentWidth()
    {
        int extraWidth = super.getNonContentWidth();

        if (this.getBackgroundRenderer().getNormalSettings().isEnabled())
        {
            int bw = this.getBorderRenderer().getNormalSettings().getActiveBorderWidth();
            extraWidth += bw * 2;
        }

        return extraWidth;
    }

    @Override
    protected int getNonContentHeight()
    {
        int extraHeight = super.getNonContentHeight();

        if (this.getBackgroundRenderer().getNormalSettings().isEnabled())
        {
            int bw = this.getBorderRenderer().getNormalSettings().getActiveBorderWidth();
            extraHeight += bw * 2;
        }

        return extraHeight;
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

        RenderWrap.setupBlendSeparate();

        BackgroundSettings backgroundSettings = this.getActiveBackgroundSettings(ctx);
        this.getBackgroundRenderer().renderBackgroundIfEnabled(x, y, z, width, height, backgroundSettings, ctx);

        BorderSettings borderSettings = this.getActiveBorderSettings(ctx);
        this.getBorderRenderer().renderBorderIfEnabled(x, y, z + 0.0125f, width, height, borderSettings, ctx);
    }
}
