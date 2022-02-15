package fi.dy.masa.malilib.gui.widget;

import fi.dy.masa.malilib.gui.util.BackgroundSettings;
import fi.dy.masa.malilib.gui.util.BorderSettings;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.util.data.EdgeInt;

public abstract class BaseModelWidget extends InteractableWidget
{
    protected int dimensions;
    protected int highlightColor;
    protected float scale = 1f;
    protected boolean doHighlight;

    public BaseModelWidget(int dimensions)
    {
        super(dimensions, dimensions);

        this.dimensions = dimensions;

        if (dimensions > 0)
        {
            this.scale = (float) dimensions / 16.0f;
        }
    }

    public BaseModelWidget setDoHighlight(boolean doHighlight)
    {
        this.doHighlight = doHighlight;
        return this;
    }

    public BaseModelWidget setHighlightColor(int color)
    {
        this.highlightColor = color;
        return this;
    }

    public BaseModelWidget setScale(float scale)
    {
        this.scale = scale;
        return this;
    }

    @Override
    public void updateWidth()
    {
        int width = this.dimensions;
        BackgroundSettings settings = this.getBackgroundRenderer().getActiveSettings(false);

        if (settings.isEnabled())
        {
            BorderSettings borderSettings = this.getBorderRenderer().getActiveSettings(false);
            int bw = borderSettings.getActiveBorderWidth();
            width += this.padding.getHorizontalTotal() + bw * 2;
        }

        this.setWidthNoUpdate(width);
    }

    @Override
    public void updateHeight()
    {
        int height = this.dimensions;
        BackgroundSettings settings = this.getBackgroundRenderer().getActiveSettings(false);

        if (settings.isEnabled())
        {
            BorderSettings borderSettings = this.getBorderRenderer().getActiveSettings(false);
            int bw = borderSettings.getActiveBorderWidth();
            height += this.padding.getVerticalTotal() + bw * 2;
        }

        this.setHeightNoUpdate(height);
    }

    protected abstract void renderModel(int x, int y, float z, float scale, ScreenContext ctx);

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        super.renderAt(x, y, z, ctx);

        int width = this.getWidth();
        int height = this.getHeight();
        BackgroundSettings settings = this.getBackgroundRenderer().getActiveSettings(false);

        if (settings.isEnabled())
        {
            BorderSettings borderSettings = this.getBorderRenderer().getActiveSettings(false);
            int bw = borderSettings.getActiveBorderWidth();
            EdgeInt padding = this.padding;
            x += padding.getLeft() + bw;
            y += padding.getTop() + bw;
        }

        if (this.doHighlight && this.isHoveredForRender(ctx))
        {
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, this.highlightColor);
        }

        this.renderModel(x, y, z + 0.5f, this.scale, ctx);
    }
}
