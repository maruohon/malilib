package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.render.RenderUtils;

public class WidgetBackground extends WidgetBase
{
    protected boolean backgroundEnabled;
    protected int backgroundColor;
    protected int borderColorBR;
    protected int borderColorUL;
    protected int borderWidth;

    public WidgetBackground(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    public WidgetBackground setBackgroundEnabled(boolean enabled)
    {
        this.backgroundEnabled = enabled;
        return this;
    }

    public WidgetBackground setBorderWidth(int borderWidth)
    {
        this.borderWidth = borderWidth;
        return this;
    }

    public WidgetBackground setBackgroundColor(int backgroundColor)
    {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public WidgetBackground setBorderColor(int borderColor)
    {
        this.borderColorUL = borderColor;
        this.borderColorBR = borderColor;
        return this;
    }

    public WidgetBackground setBackgroundProperties(int borderWidth, int backgroundColor, int borderColorUL, int borderColorBR)
    {
        this.borderWidth = borderWidth;
        this.backgroundColor = backgroundColor;
        this.borderColorUL = borderColorUL;
        this.borderColorBR = borderColorBR;
        this.backgroundEnabled = true;
        return this;
    }

    protected void drawBackground()
    {
        if (this.backgroundEnabled)
        {
            int x = this.getX();
            int y = this.getY();
            int z = this.getZLevel();
            int w = this.getWidth();
            int h = this.getHeight();
            int bs = this.borderWidth;

            RenderUtils.drawRect(x + bs, y + bs, w - bs * 2 + 1, h - bs * 2 + 1, this.backgroundColor, z);

            // Horizontal lines/borders
            RenderUtils.drawRect(x, y         , w, bs, this.borderColorUL, z);
            RenderUtils.drawRect(x, y + h - bs, w, bs, this.borderColorBR, z);

            // Vertical lines/borders
            RenderUtils.drawRect(x + bs        , y + bs, bs, h - bs * 2, this.borderColorUL, z);
            RenderUtils.drawRect(x + w - bs * 2, y + bs, bs, h - bs * 2, this.borderColorBR, z);
        }
    }
}
