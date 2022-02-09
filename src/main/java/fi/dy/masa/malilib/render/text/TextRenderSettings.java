package fi.dy.masa.malilib.render.text;

import com.google.gson.JsonObject;
import fi.dy.masa.malilib.util.JsonUtils;

public class TextRenderSettings
{
    protected boolean backgroundEnabled;
    protected boolean textShadowEnabled = true;
    protected int backgroundColor = 0xA0505050;
    protected int textColor = 0xFFFFFFFF;
    protected int lineHeight;

    public TextRenderSettings()
    {
        this.lineHeight = TextRenderer.INSTANCE.getLineHeight();
    }

    public boolean getBackgroundEnabled()
    {
        return this.backgroundEnabled;
    }

    public boolean getTextShadowEnabled()
    {
        return this.textShadowEnabled;
    }

    public int getBackgroundColor()
    {
        return this.backgroundColor;
    }

    public int getTextColor()
    {
        return this.textColor;
    }

    public int getLineHeight()
    {
        return this.lineHeight;
    }

    public void setBackgroundEnabled(boolean backgroundEnabled)
    {
        this.backgroundEnabled = backgroundEnabled;
    }

    public void setTextShadowEnabled(boolean textShadowEnabled)
    {
        this.textShadowEnabled = textShadowEnabled;
    }

    public void setBackgroundColor(int color)
    {
        this.backgroundColor = color;
    }

    public void setTextColor(int color)
    {
        this.textColor = color;
    }

    public void setLineHeight(int lineHeight)
    {
        this.lineHeight = lineHeight;
    }

    public void toggleUseBackground()
    {
        this.backgroundEnabled = ! this.backgroundEnabled;
    }

    public void toggleUseTextShadow()
    {
        this.textShadowEnabled = ! this.textShadowEnabled;
    }

    public void setFrom(TextRenderSettings other)
    {
        this.backgroundEnabled = other.backgroundEnabled;
        this.textShadowEnabled = other.textShadowEnabled;
        this.backgroundColor = other.backgroundColor;
        this.textColor = other.textColor;
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("bg_enabled", this.backgroundEnabled);
        obj.addProperty("bg_color", this.backgroundColor);
        obj.addProperty("text_shadow", this.textShadowEnabled);
        obj.addProperty("text_color", this.textColor);
        obj.addProperty("line_height", this.lineHeight);


        return obj;
    }

    public void fromJson(JsonObject obj)
    {
        this.backgroundEnabled = JsonUtils.getBooleanOrDefault(obj, "bg_enabled", false);
        this.backgroundColor = JsonUtils.getIntegerOrDefault(obj, "bg_color", 0xA0505050);
        this.textShadowEnabled = JsonUtils.getBooleanOrDefault(obj, "text_shadow", true);
        this.textColor = JsonUtils.getIntegerOrDefault(obj, "text_color", 0xFFFFFFFF);
        this.lineHeight = JsonUtils.getIntegerOrDefault(obj, "line_height", TextRenderer.INSTANCE.getLineHeight());
    }
}
