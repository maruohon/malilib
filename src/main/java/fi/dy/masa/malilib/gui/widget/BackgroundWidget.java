package fi.dy.masa.malilib.gui.widget;

import fi.dy.masa.malilib.render.RenderUtils;

public class BackgroundWidget extends BaseWidget
{
    protected boolean backgroundEnabled;
    protected boolean borderEnabled;
    protected boolean borderEnabledHovered;
    protected boolean renderHoverBackground;
    protected int backgroundColor = 0xFF101010;
    protected int backgroundColorHovered = 0x50FFFFFF;
    protected int borderColorBR = 0xFFC0C0C0;
    protected int borderColorUL = 0xFFC0C0C0;
    protected int borderColorHovered = 0xFFFFFFFF;
    protected int borderWidth;
    protected int borderWidthHovered;
    protected int paddingLeft;
    protected int paddingRight;
    protected int paddingTop;
    protected int paddingBottom;

    public BackgroundWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    public BackgroundWidget setBackgroundEnabled(boolean enabled)
    {
        this.backgroundEnabled = enabled;
        this.updateWidth();
        this.updateHeight();
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
        this.updateWidth();
        this.updateHeight();
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

    public BackgroundWidget setBorderColor(int borderColor)
    {
        this.borderColorUL = borderColor;
        this.borderColorBR = borderColor;
        return this;
    }

    public BackgroundWidget setHoveredBorderColor(int borderColor)
    {
        this.borderColorHovered = borderColor;
        return this;
    }

    public BackgroundWidget setBackgroundAndBorderColors(int backgroundColor, int borderColorUL, int borderColorBR)
    {
        this.backgroundColor = backgroundColor;
        this.borderColorUL = borderColorUL;
        this.borderColorBR = borderColorBR;
        return this;
    }

    public BackgroundWidget setPaddingLeft(int padding)
    {
        this.paddingLeft = padding;
        this.updateWidth();
        return this;
    }

    public BackgroundWidget setPaddingRight(int padding)
    {
        this.paddingRight = padding;
        this.updateWidth();
        return this;
    }

    public BackgroundWidget setPaddingTop(int padding)
    {
        this.paddingTop = padding;
        this.updateHeight();
        return this;
    }

    public BackgroundWidget setPaddingBottom(int padding)
    {
        this.paddingBottom = padding;
        this.updateHeight();
        return this;
    }

    public BackgroundWidget setPaddingXY(int padding)
    {
        this.paddingLeft = padding;
        this.paddingRight = padding;
        this.paddingTop = padding;
        this.paddingBottom = padding;
        this.updateWidth();
        this.updateHeight();
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
        int colorUL = hoverBorder ? this.borderColorHovered : this.borderColorUL;
        int colorBR = hoverBorder ? this.borderColorHovered : this.borderColorBR;

        // Horizontal lines/borders
        RenderUtils.renderRectangle(x, y         , w, bw, colorUL, z);
        RenderUtils.renderRectangle(x, y + h - bw, w, bw, colorBR, z);

        // Vertical lines/borders
        RenderUtils.renderRectangle(x         , y + bw, bw, h - b2, colorUL, z);
        RenderUtils.renderRectangle(x + w - bw, y + bw, bw, h - b2, colorBR, z);
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
        RenderUtils.renderRectangle(x + bw, y + bw, width - b2, height - b2, color, z);
    }
}
