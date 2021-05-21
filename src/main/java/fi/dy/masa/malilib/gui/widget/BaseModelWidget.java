package fi.dy.masa.malilib.gui.widget;

import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.render.ShapeRenderUtils;

public abstract class BaseModelWidget extends BackgroundWidget
{
    protected int dimensions;
    protected int highlightColor;
    protected float scale = 1f;
    protected boolean doHighlight;

    public BaseModelWidget(int x, int y)
    {
        this(x, y, 16);
    }

    public BaseModelWidget(int x, int y, int dimensions)
    {
        super(x, y, dimensions, dimensions);

        this.dimensions = dimensions;
        this.setNormalBorderWidth(0);

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

        if (this.renderNormalBackground)
        {
            width += this.padding.getLeft() + this.padding.getRight() + this.normalBorderWidth * 2;
        }

        this.setWidth(width);
    }

    @Override
    public void updateHeight()
    {
        int height = this.dimensions;

        if (this.renderNormalBackground)
        {
            height += this.padding.getTop() + this.padding.getBottom() + this.normalBorderWidth * 2;
        }

        this.setHeight(height);
    }

    protected abstract void renderModel(int x, int y, float z, float scale, ScreenContext ctx);

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        super.renderAt(x, y, z, ctx);

        int width = this.getWidth();
        int height = this.getHeight();

        if (this.renderNormalBackground)
        {
            x += this.padding.getLeft() + this.normalBorderWidth;
            y += this.padding.getTop() + this.normalBorderWidth;
        }

        if (this.doHighlight && this.isHoveredForRender(ctx))
        {
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, this.highlightColor);
        }

        this.renderModel(x, y, z + 0.5f, this.scale, ctx);
    }
}
