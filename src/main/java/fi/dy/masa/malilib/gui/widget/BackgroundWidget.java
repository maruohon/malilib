package fi.dy.masa.malilib.gui.widget;

import fi.dy.masa.malilib.render.RenderUtils;

public class BackgroundWidget extends BaseWidget
{
    protected boolean backgroundEnabled;
    protected boolean borderEnabled;
    protected boolean renderHoverBackground = true;
    protected int backgroundColor = 0xFF101010;
    protected int backgroundColorHovered = 0x60FFFFFF;
    protected int borderColorBR = 0xFFC0C0C0;
    protected int borderColorUL = 0xFFC0C0C0;
    protected int borderWidth;
    protected int paddingX;
    protected int paddingY;

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

    public BackgroundWidget setBackgroundAndBorderColors(int backgroundColor, int borderColorUL, int borderColorBR)
    {
        this.backgroundColor = backgroundColor;
        this.borderColorUL = borderColorUL;
        this.borderColorBR = borderColorBR;
        return this;
    }

    public BackgroundWidget setPaddingX(int offsetX)
    {
        this.paddingX = offsetX;
        this.updateWidth();
        return this;
    }

    public BackgroundWidget setPaddingY(int offsetY)
    {
        this.paddingY = offsetY;
        this.updateHeight();
        return this;
    }

    public BackgroundWidget setPaddingXY(int offset)
    {
        this.setPaddingX(offset);
        this.setPaddingY(offset);
        return this;
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

        this.renderWidgetBackground(x, y, z, width, height, mouseX, mouseY, hovered);
    }

    protected void renderWidgetBackground(int x, int y, float z, int width, int height, int mouseX, int mouseY, boolean hovered)
    {
        this.renderBackgroundOnly(x, y, z, width, height, mouseX, mouseY, hovered);
        this.renderBorder(x, y, z, width, height, mouseX, mouseY, hovered);
    }

    protected void renderBorder(int x, int y, float z, int width, int height, int mouseX, int mouseY, boolean hovered)
    {
        if (this.borderEnabled)
        {
            RenderUtils.color(1f, 1f, 1f, 1f);
            RenderUtils.setupBlend();

            int w = width;
            int h = height;
            int bw = this.borderWidth;
            int b2 = bw * 2;

            // Horizontal lines/borders
            RenderUtils.drawRect(x, y         , w, bw, this.borderColorUL, z);
            RenderUtils.drawRect(x, y + h - bw, w, bw, this.borderColorBR, z);

            // Vertical lines/borders
            RenderUtils.drawRect(x         , y + bw, bw, h - b2, this.borderColorUL, z);
            RenderUtils.drawRect(x + w - bw, y + bw, bw, h - b2, this.borderColorBR, z);
        }
    }

    protected void renderBackgroundOnly(int x, int y, float z, int width, int height, int mouseX, int mouseY, boolean hovered)
    {
        if (this.backgroundEnabled)
        {
            RenderUtils.color(1f, 1f, 1f, 1f);
            RenderUtils.setupBlend();

            int bw = this.borderWidth;
            int b2 = bw * 2;

            // Background
            int color = this.renderHoverBackground && (hovered || this.isMouseOver(mouseX, mouseY)) ? this.backgroundColorHovered : this.backgroundColor;
            RenderUtils.drawRect(x + bw, y + bw, width - b2, height - b2, color, z);
        }
    }
}
