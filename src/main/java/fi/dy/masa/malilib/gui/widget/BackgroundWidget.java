package fi.dy.masa.malilib.gui.widget;

import fi.dy.masa.malilib.gui.position.EdgeInt;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;

public class BackgroundWidget extends InteractableWidget
{
    protected final EdgeInt borderColorNormal = new EdgeInt(0xFFC0C0C0);
    protected final EdgeInt borderColorHovered = new EdgeInt(0xFFFFFFFF);
    protected boolean renderBackground;
    protected boolean renderBorder;
    protected boolean renderHoverBackground;
    protected boolean renderHoverBorder;
    protected int backgroundColor = 0xFF101010;
    protected int backgroundColorHovered = 0x50FFFFFF;
    protected int borderWidthNormal = 1;
    protected int borderWidthHovered = 1;

    public BackgroundWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    public BackgroundWidget setRenderBackground(boolean enabled)
    {
        this.renderBackground = enabled;
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
        this.renderBorder = renderBorder;
        return this;
    }

    public BackgroundWidget setRenderHoverBorder(boolean renderHoverBorder)
    {
        this.renderHoverBorder = renderHoverBorder;
        return this;
    }

    public BackgroundWidget setNormalBorderWidth(int borderWidthNormal)
    {
        this.borderWidthNormal = borderWidthNormal;
        this.renderBorder = borderWidthNormal > 0;
        this.updateSize();
        return this;
    }

    public BackgroundWidget setHoveredBorderWidth(int borderWidth)
    {
        this.borderWidthHovered = borderWidth;
        this.renderHoverBorder = borderWidth > 0;
        return this;
    }

    public BackgroundWidget setBackgroundColor(int backgroundColor)
    {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public BackgroundWidget setBackgroundColorHovered(int backgroundColor)
    {
        this.backgroundColorHovered = backgroundColor;
        return this;
    }

    public BackgroundWidget setNormalBorderColor(int borderColor)
    {
        this.borderColorNormal.setAll(borderColor);
        return this;
    }

    public BackgroundWidget setHoveredBorderColor(int borderColor)
    {
        this.borderColorHovered.setAll(borderColor);
        return this;
    }

    public int getActiveBorderWidth()
    {
        return this.renderBorder ? this.borderWidthNormal : 0;
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
        return this.renderBackground;
    }

    protected boolean shouldRenderHoverBackground(boolean hovered, ScreenContext ctx)
    {
        return hovered && this.renderHoverBackground;
    }

    protected boolean shouldRenderNormalBorder(boolean hovered, ScreenContext ctx)
    {
        return this.renderBorder;
    }

    protected boolean shouldRenderHoverBorder(boolean hovered, ScreenContext ctx)
    {
        return hovered && this.renderHoverBorder;
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
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
            this.renderBorder(x, y, z, width, height, this.borderWidthHovered, this.borderColorHovered, ctx);
        }
        else if (this.shouldRenderNormalBorder(hovered, ctx))
        {
            this.renderBorder(x, y, z, width, height, this.borderWidthNormal, this.borderColorNormal, ctx);
        }
    }

    protected void renderBorder(int x, int y, float z, int width, int height, int borderWidth,
                                EdgeInt color, ScreenContext ctx)
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
            int borderWidth = this.renderHoverBorder ? this.borderWidthHovered : 0;
            this.renderBackground(x, y, z, width, height, borderWidth, this.backgroundColorHovered, ctx);
        }
        else if (this.shouldRenderNormalBackground(hovered, ctx))
        {
            int borderWidth = this.renderBorder ? this.borderWidthNormal : 0;
            this.renderBackground(x, y, z, width, height, borderWidth, this.backgroundColor, ctx);
        }
    }

    protected void renderBackground(int x, int y, float z, int width, int height, int borderWidth,
                                    int color, ScreenContext ctx)
    {
        int bw = borderWidth;
        int b2 = bw * 2;

        ShapeRenderUtils.renderRectangle(x + bw, y + bw, z, width - b2, height - b2, color);
    }
}
