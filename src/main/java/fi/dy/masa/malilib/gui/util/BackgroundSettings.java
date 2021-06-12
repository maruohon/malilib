package fi.dy.masa.malilib.gui.util;

public class BackgroundSettings
{
    protected boolean enabled;
    protected int color;

    public BackgroundSettings(int color)
    {
        this.color = color;
    }

    public BackgroundSettings setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    public BackgroundSettings setColor(int color)
    {
        this.color = color;
        return this;
    }

    public BackgroundSettings setEnabledAndColor(boolean enabled, int color)
    {
        this.enabled = enabled;
        this.color = color;
        return this;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public int getColor()
    {
        return this.color;
    }
}
