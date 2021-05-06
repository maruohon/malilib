package fi.dy.masa.malilib.gui.widget;

import fi.dy.masa.malilib.gui.position.EdgeInt;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;

public class BackgroundWidget extends InteractableWidget
{
    protected final EdgeInt normalBorderColor = new EdgeInt(0xFFC0C0C0);
    protected final EdgeInt hoveredBorderColor = new EdgeInt(0xFFFFFFFF);
    protected boolean renderNormalBackground;
    protected boolean renderNormalBorder;
    protected boolean renderHoverBackground;
    protected boolean renderHoverBorder;
    protected int normalBackgroundColor = 0xFF101010;
    protected int hoveredBackgroundColor = 0x50FFFFFF;
    protected int normalBorderWidth = 1;
    protected int hoveredBorderWidth = 1;

    public BackgroundWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    public BackgroundWidget setRenderNormalBackground(boolean enabled)
    {
        this.renderNormalBackground = enabled;
        this.updateSize();
        return this;
    }

    public BackgroundWidget setRenderHoverBackground(boolean enabled)
    {
        this.renderHoverBackground = enabled;
        return this;
    }

    public BackgroundWidget setRenderNormalBorder(boolean renderBorder)
    {
        this.renderNormalBorder = renderBorder;
        return this;
    }

    public BackgroundWidget setRenderHoverBorder(boolean renderHoverBorder)
    {
        this.renderHoverBorder = renderHoverBorder;
        return this;
    }

    public BackgroundWidget setNormalBorderWidth(int borderWidthNormal)
    {
        this.normalBorderWidth = borderWidthNormal;
        this.renderNormalBorder = borderWidthNormal > 0;
        this.updateSize();
        return this;
    }

    public BackgroundWidget setHoveredBorderWidth(int borderWidth)
    {
        this.hoveredBorderWidth = borderWidth;
        this.renderHoverBorder = borderWidth > 0;
        return this;
    }

    public BackgroundWidget setNormalBackgroundColor(int normalBackgroundColor)
    {
        this.normalBackgroundColor = normalBackgroundColor;
        return this;
    }

    public BackgroundWidget setHoveredBackgroundColor(int backgroundColor)
    {
        this.hoveredBackgroundColor = backgroundColor;
        return this;
    }

    public BackgroundWidget setNormalBorderColor(int borderColor)
    {
        this.normalBorderColor.setAll(borderColor);
        return this;
    }

    public BackgroundWidget setHoveredBorderColor(int borderColor)
    {
        this.hoveredBorderColor.setAll(borderColor);
        return this;
    }

    public EdgeInt getNormalBorderColor()
    {
        return this.normalBorderColor;
    }

    public EdgeInt getHoveredBorderColor()
    {
        return this.hoveredBorderColor;
    }

    public int getNormalBackgroundColor()
    {
        return this.normalBackgroundColor;
    }

    public int getHoveredBackgroundColor()
    {
        return this.hoveredBackgroundColor;
    }

    public int getActiveBorderWidth()
    {
        return this.renderNormalBorder ? this.normalBorderWidth : 0;
    }

    protected int getBackgroundWidth(boolean hovered, ScreenContext ctx)
    {
        return this.getWidth();
    }

    protected int getBackgroundHeight(boolean hovered, ScreenContext ctx)
    {
        return this.getHeight();
    }

    protected boolean shouldRenderNormalBackground(boolean hovered, ScreenContext ctx)
    {
        return this.renderNormalBackground;
    }

    protected boolean shouldRenderHoverBackground(boolean hovered, ScreenContext ctx)
    {
        return hovered && this.renderHoverBackground;
    }

    protected boolean shouldRenderNormalBorder(boolean hovered, ScreenContext ctx)
    {
        return this.renderNormalBorder;
    }

    protected boolean shouldRenderHoverBorder(boolean hovered, ScreenContext ctx)
    {
        return hovered && this.renderHoverBorder;
    }

    protected EdgeInt getNormalBorderColorForRender()
    {
        return this.normalBorderColor;
    }

    protected EdgeInt getHoveredBorderColorForRender()
    {
        return this.hoveredBorderColor;
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        this.renderWidgetBackground(x, y, z, ctx);
        super.renderAt(x, y, z, ctx);
    }

    protected void renderWidgetBackground(int x, int y, float z, ScreenContext ctx)
    {
        boolean hovered = this.isHoveredForRender(ctx);
        int width = this.getBackgroundWidth(hovered, ctx);
        int height = this.getBackgroundHeight(hovered, ctx);

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.setupBlend();

        this.renderWidgetBackground(x, y, z, width, height,hovered, ctx);
    }

    protected void renderWidgetBackground(int x, int y, float z, int width, int height,
                                          boolean hovered, ScreenContext ctx)
    {
        this.renderBackgroundIfEnabled(x, y, z, width, height, hovered, ctx);
        this.renderBorderIfEnabled(x, y, z, width, height, hovered, ctx);
    }

    protected void renderBorderIfEnabled(int x, int y, float z, int width, int height,
                                         boolean hovered, ScreenContext ctx)
    {
        if (this.shouldRenderHoverBorder(hovered, ctx))
        {
            this.renderBorder(x, y, z, width, height, this.hoveredBorderWidth, hovered, this.getHoveredBorderColorForRender(), ctx);
        }
        else if (this.shouldRenderNormalBorder(hovered, ctx))
        {
            this.renderBorder(x, y, z, width, height, this.normalBorderWidth, hovered, this.getNormalBorderColorForRender(), ctx);
        }
    }

    protected void renderBorder(int x, int y, float z, int width, int height, int borderWidth,
                                boolean hovered, EdgeInt color, ScreenContext ctx)
    {
        int w = width;
        int h = height;
        int bw = borderWidth;
        int b2 = bw * 2;

        // Horizontal lines/borders
        ShapeRenderUtils.renderRectangle(x, y         , z, w, bw, color.getTop());
        ShapeRenderUtils.renderRectangle(x, y + h - bw, z, w, bw, color.getBottom());

        // Vertical lines/borders
        ShapeRenderUtils.renderRectangle(x         , y + bw, z, bw, h - b2, color.getLeft());
        ShapeRenderUtils.renderRectangle(x + w - bw, y + bw, z, bw, h - b2, color.getRight());
    }

    protected void renderBackgroundIfEnabled(int x, int y, float z, int width, int height,
                                             boolean hovered, ScreenContext ctx)
    {
        if (this.shouldRenderHoverBackground(hovered, ctx))
        {
            int borderWidth = this.renderHoverBorder ? this.hoveredBorderWidth : 0;
            this.renderBackground(x, y, z, width, height, borderWidth, hovered, this.hoveredBackgroundColor, ctx);
        }
        else if (this.shouldRenderNormalBackground(hovered, ctx))
        {
            int borderWidth = this.renderNormalBorder ? this.normalBorderWidth : 0;
            this.renderBackground(x, y, z, width, height, borderWidth, hovered, this.normalBackgroundColor, ctx);
        }
    }

    protected void renderBackground(int x, int y, float z, int width, int height, int borderWidth,
                                    boolean hovered, int color, ScreenContext ctx)
    {
        int bw = borderWidth;
        int b2 = bw * 2;

        ShapeRenderUtils.renderRectangle(x + bw, y + bw, z, width - b2, height - b2, color);
    }
}
