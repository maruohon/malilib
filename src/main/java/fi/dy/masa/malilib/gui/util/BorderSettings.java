package fi.dy.masa.malilib.gui.util;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.listener.EventListener;

public class BorderSettings
{
    protected final EdgeInt borderColor;
    @Nullable protected EventListener sizeChangeListener;
    protected boolean enabled;
    protected int borderWidth = 1;

    public BorderSettings(int defaultColor)
    {
        this.borderColor = new EdgeInt(defaultColor);
    }

    public BorderSettings(int borderWidth, int defaultColor)
    {
        this.setBorderWidth(borderWidth);
        this.borderColor = new EdgeInt(defaultColor);
    }

    public BorderSettings setSizeChangeListener(@Nullable EventListener sizeChangeListener)
    {
        this.sizeChangeListener = sizeChangeListener;
        return this;
    }

    public BorderSettings setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    public BorderSettings setBorderWidth(int borderWidth)
    {
        this.borderWidth = borderWidth;
        this.setEnabled(borderWidth > 0);
        this.updateSize();
        return this;
    }

    public BorderSettings setColor(EdgeInt borderColor)
    {
        this.borderColor.setFrom(borderColor);
        return this;
    }

    public BorderSettings setColor(int borderColor)
    {
        this.borderColor.setAll(borderColor);
        return this;
    }

    public BorderSettings setBorderWidthAndColor(int borderWidth, int color)
    {
        this.setBorderWidth(borderWidth);
        this.setColor(color);
        return this;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public int getBorderWidth()
    {
        return this.borderWidth;
    }

    public int getActiveBorderWidth()
    {
        return this.enabled ? this.borderWidth : 0;
    }

    public EdgeInt getColor()
    {
        return this.borderColor;
    }

    protected void updateSize()
    {
        if (this.sizeChangeListener != null)
        {
            this.sizeChangeListener.onEvent();
        }
    }
}
