package fi.dy.masa.malilib.render.text;

import com.google.gson.JsonObject;
import fi.dy.masa.malilib.util.JsonUtils;

public class TextRenderSettings
{
    protected static final int DEFAULT_BG_COLOR = 0xA0505050;
    protected static final int DEFAULT_TEXT_COLOR = 0xFFFFFFFF;
    protected static final int DEFAULT_HOVERED_TEXT_COLOR = 0xFFFFFFFF;

    protected boolean backgroundEnabled;
    protected boolean textShadowEnabled = true;
    protected boolean useHoverTextColor;
    protected int backgroundColor = DEFAULT_BG_COLOR;
    protected int hoveredTextColor = DEFAULT_HOVERED_TEXT_COLOR;
    protected int textColor = DEFAULT_TEXT_COLOR;
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

    public boolean getUseHoverTextColor()
    {
        return this.useHoverTextColor;
    }

    public int getBackgroundColor()
    {
        return this.backgroundColor;
    }

    public int getTextColor()
    {
        return this.textColor;
    }

    public int getEffectiveTextColor(boolean hovered)
    {
        if (hovered && this.useHoverTextColor)
        {
            return this.hoveredTextColor;
        }

        return this.textColor;
    }

    public int getHoveredTextColor()
    {
        return this.hoveredTextColor;
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

    public void setUseHoverTextColor(boolean useHoverTextColor)
    {
        this.useHoverTextColor = useHoverTextColor;
    }

    public void setBackgroundColor(int color)
    {
        this.backgroundColor = color;
    }

    public void setTextColor(int color)
    {
        this.textColor = color;
    }

    public void setHoveredTextColor(int hoveredTextColor)
    {
        this.hoveredTextColor = hoveredTextColor;
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

    public boolean isModified()
    {
        return this.backgroundEnabled ||
               this.useHoverTextColor ||
               this.textShadowEnabled == false ||
               this.lineHeight != TextRenderer.INSTANCE.getLineHeight() ||
               this.backgroundColor != DEFAULT_BG_COLOR ||
               this.textColor != DEFAULT_TEXT_COLOR ||
               this.hoveredTextColor != DEFAULT_HOVERED_TEXT_COLOR;
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("bg_enabled", this.backgroundEnabled);
        obj.addProperty("text_shadow", this.textShadowEnabled);
        obj.addProperty("bg_color", this.backgroundColor);
        obj.addProperty("text_color", this.textColor);
        obj.addProperty("hover_text_color", this.hoveredTextColor);
        obj.addProperty("line_height", this.lineHeight);
        obj.addProperty("use_hover_text_color", this.useHoverTextColor);

        return obj;
    }

    public JsonObject toJsonModifiedOnly()
    {
        JsonObject obj = new JsonObject();

        if (this.backgroundEnabled) { obj.addProperty("bg_enabled", this.backgroundEnabled); }
        if (this.textShadowEnabled == false) { obj.addProperty("text_shadow", this.textShadowEnabled); }
        if (this.backgroundColor != DEFAULT_BG_COLOR) { obj.addProperty("bg_color", this.backgroundColor); }
        if (this.textColor != DEFAULT_TEXT_COLOR) { obj.addProperty("text_color", this.textColor); }
        if (this.hoveredTextColor != DEFAULT_HOVERED_TEXT_COLOR) { obj.addProperty("hover_text_color", this.hoveredTextColor); }
        if (this.lineHeight != TextRenderer.INSTANCE.getLineHeight()) { obj.addProperty("line_height", this.lineHeight); }
        if (this.useHoverTextColor) { obj.addProperty("use_hover_text_color", this.useHoverTextColor); }

        return obj;
    }

    public void fromJson(JsonObject obj)
    {
        this.backgroundEnabled = JsonUtils.getBooleanOrDefault(obj, "bg_enabled", false);
        this.backgroundColor = JsonUtils.getIntegerOrDefault(obj, "bg_color", DEFAULT_BG_COLOR);
        this.textShadowEnabled = JsonUtils.getBooleanOrDefault(obj, "text_shadow", true);
        this.useHoverTextColor = JsonUtils.getBooleanOrDefault(obj, "use_hover_text_color", false);
        this.textColor = JsonUtils.getIntegerOrDefault(obj, "text_color", DEFAULT_TEXT_COLOR);
        this.hoveredTextColor = JsonUtils.getIntegerOrDefault(obj, "hover_text_color", DEFAULT_HOVERED_TEXT_COLOR);
        this.lineHeight = JsonUtils.getIntegerOrDefault(obj, "line_height", TextRenderer.INSTANCE.getLineHeight());
    }
}
