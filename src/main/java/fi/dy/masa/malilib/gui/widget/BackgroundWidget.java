package fi.dy.masa.malilib.gui.widget;

import fi.dy.masa.malilib.gui.position.EdgeInt;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;

public class BackgroundWidget extends InteractableWidget
{
    protected final EdgeInt borderColorNormal = new EdgeInt(0xFFC0C0C0);
    protected final EdgeInt borderColorHovered = new EdgeInt(0xFFFFFFFF);
    protected boolean backgroundEnabled;
    protected boolean borderEnabled;
    protected boolean borderEnabledHovered;
    protected boolean renderHoverBackground;
    protected int backgroundColor = 0xFF101010;
    protected int backgroundColorHovered = 0x50FFFFFF;
    protected int borderWidth;
    protected int borderWidthHovered;

    public BackgroundWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    public BackgroundWidget setBackgroundEnabled(boolean enabled)
    {
        this.backgroundEnabled = enabled;
        this.updateSize();
        return this;
    }

    public BackgroundWidget setRenderHoverBackground(boolean enabled)
    {
        this.renderHoverBackground = enabled;
        return this;
    }

    public BackgroundWidget setBorderWidth(int borderWidth)
    {
        this.borderWidth = borderWidth;
        this.borderEnabled = borderWidth > 0;
        this.updateSize();
        return this;
    }

    public BackgroundWidget setHoveredBorderWidth(int borderWidth)
    {
        this.borderWidthHovered = borderWidth;
        this.borderEnabledHovered = borderWidth > 0;
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
        return this.borderEnabled ? this.borderWidth : 0;
    }

    protected int getBackgroundWidth(int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        return this.getWidth();
    }

    protected int getBackgroundHeight(int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        return this.getHeight();
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        int width = this.getBackgroundWidth(mouseX, mouseY, isActiveGui, hovered);
        int height = this.getBackgroundHeight(mouseX, mouseY, isActiveGui, hovered);

        this.renderWidgetBackground(x, y, z, width, height, mouseX, mouseY, isActiveGui, hovered);
    }

    protected void renderWidgetBackground(int x, int y, float z, int width, int height, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        this.renderBackgroundIfEnabled(x, y, z, width, height, mouseX, mouseY, isActiveGui, hovered);
        this.renderBorderIfEnabled(x, y, z, width, height, mouseX, mouseY, isActiveGui, hovered);
    }

    protected void renderBorderIfEnabled(int x, int y, float z, int width, int height, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        boolean hoverBorder = this.borderEnabledHovered && hovered;

        if (this.borderEnabled || hoverBorder)
        {
            this.renderBorder(x, y, z, width, height, mouseX, mouseY, isActiveGui, hovered, hoverBorder);
        }
    }

    protected void renderBorder(int x, int y, float z, int width, int height, int mouseX, int mouseY, boolean isActiveGui, boolean hovered, boolean hoverBorder)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.setupBlend();

        int w = width;
        int h = height;
        int bw = hoverBorder ? this.borderWidthHovered : this.borderWidth;
        int b2 = bw * 2;
        EdgeInt color = hoverBorder ? this.borderColorHovered : this.borderColorNormal;

        // Horizontal lines/borders
        ShapeRenderUtils.renderRectangle(x, y         , z, w, bw, color.getTop());
        ShapeRenderUtils.renderRectangle(x, y + h - bw, z, w, bw, color.getBottom());

        // Vertical lines/borders
        ShapeRenderUtils.renderRectangle(x         , y + bw, z, bw, h - b2, color.getLeft());
        ShapeRenderUtils.renderRectangle(x + w - bw, y + bw, z, bw, h - b2, color.getRight());
    }

    protected void renderBackgroundIfEnabled(int x, int y, float z, int width, int height, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        boolean hoverBg = this.renderHoverBackground && hovered;

        if (this.backgroundEnabled || hoverBg)
        {
            this.renderBackground(x, y, z, width, height, mouseX, mouseY, isActiveGui, hovered, hoverBg);
        }
    }

    protected void renderBackground(int x, int y, float z, int width, int height, int mouseX, int mouseY, boolean isActiveGui, boolean hovered, boolean hoverBg)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.setupBlend();

        boolean hoverBorder = this.borderEnabledHovered && hovered;
        int bw = hoverBorder ? this.borderWidthHovered : this.borderWidth;
        int b2 = bw * 2;

        // Background
        int color = hoverBg ? this.backgroundColorHovered : this.backgroundColor;
        ShapeRenderUtils.renderRectangle(x + bw, y + bw, z, width - b2, height - b2, color);
    }
}
