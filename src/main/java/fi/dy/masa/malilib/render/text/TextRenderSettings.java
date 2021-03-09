package fi.dy.masa.malilib.render.text;

public class TextRenderSettings
{
    public boolean useBackground;
    public boolean useTextShadow = true;
    public int backgroundColor = 0xA0505050;
    public int textColor = 0xFFFFFFFF;

    public TextRenderSettings setUseBackground(boolean useBackground)
    {
        this.useBackground = useBackground;
        return this;
    }

    public TextRenderSettings setUseTextShadow(boolean useTextShadow)
    {
        this.useTextShadow = useTextShadow;
        return this;
    }

    public TextRenderSettings setBackgroundColor(int backgroundColor)
    {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public TextRenderSettings setTextColor(int textColor)
    {
        this.textColor = textColor;
        return this;
    }

    public void setFrom(TextRenderSettings other)
    {
        this.useBackground = other.useBackground;
        this.useTextShadow = other.useTextShadow;
        this.backgroundColor = other.backgroundColor;
        this.textColor = other.textColor;
    }
}
