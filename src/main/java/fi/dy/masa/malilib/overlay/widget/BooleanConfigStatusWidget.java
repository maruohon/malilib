package fi.dy.masa.malilib.overlay.widget;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.BooleanConfigStatusIndicatorEditScreen;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.button.OnOffButton;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextStyle;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class BooleanConfigStatusWidget extends BaseConfigStatusIndicatorWidget<BooleanConfig>
{
    public static final TextStyle STYLE_ON  = TextStyle.builder().withColor(0xFF00FF00).build();
    public static final TextStyle STYLE_OFF = TextStyle.builder().withColor(0xFFFF0000).build();

    protected final StyledTextLine textOn;
    protected final StyledTextLine textOff;
    protected final int sliderWidth;
    protected Style renderStyle = Style.ON_OFF_TEXT;
    @Nullable protected Icon icon;
    protected boolean lastValue;

    public BooleanConfigStatusWidget(BooleanConfig config, ConfigOnTab configOnTab)
    {
        super(config, configOnTab);

        this.textOn  = StyledTextLine.translatedOf("malilib.label.on.caps", STYLE_ON);
        this.textOff = StyledTextLine.translatedOf("malilib.label.off.caps", STYLE_OFF);
        int sw = Math.max(this.textOn.renderWidth, this.textOff.renderWidth);
        sw += DefaultIcons.SLIDER_GREEN.getWidth() + 6;
        this.sliderWidth = sw;

        this.updateValue();
    }

    public Style getRenderStyle()
    {
        return this.renderStyle;
    }

    public void setRenderStyle(Style renderStyle)
    {
        this.renderStyle = renderStyle;
        this.updateValue();
    }

    @Override
    public void openEditScreen()
    {
        BooleanConfigStatusIndicatorEditScreen screen = new BooleanConfigStatusIndicatorEditScreen(this, GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(screen);
    }

    @Override
    public void updateState(boolean force)
    {
        if (force || this.lastValue != this.config.getBooleanValue())
        {
            this.updateValue();
        }
    }

    protected void updateValue()
    {
        this.lastValue = this.config.getBooleanValue();
        this.valueDisplayText = null;
        this.icon = null;

        TextStyle style = this.lastValue ? STYLE_ON : STYLE_OFF;

        if (this.renderStyle == Style.ON_OFF_TEXT)
        {
            this.valueDisplayText = this.lastValue ? this.textOn : this.textOff;
            this.valueRenderWidth = this.valueDisplayText.renderWidth;
        }
        else if (this.renderStyle == Style.TRUE_FALSE_TEXT)
        {
            String translationKey = this.lastValue ? "malilib.label.true" : "malilib.label.false";
            this.valueDisplayText = StyledTextLine.translatedOf(translationKey, style);
            this.valueRenderWidth = this.valueDisplayText.renderWidth;
        }
        else if (this.renderStyle == Style.ON_OFF_SLIDER)
        {
            this.valueRenderWidth = this.sliderWidth;
        }
        else if (this.renderStyle == Style.ON_OFF_LIGHT)
        {
            this.icon = this.lastValue ? DefaultIcons.LIGHT_GREEN_ON : DefaultIcons.LIGHT_RED_OFF;
        }

        this.notifyContainerOfChanges(true);
    }

    protected int getSliderStyleTextStartX(int baseX, boolean state)
    {
        // The slider is on the left side
        if (state == false)
        {
            baseX += DefaultIcons.SLIDER_RED.getWidth();
        }

        return baseX;
    }

    @Override
    protected void renderContents(int x, int y, float z)
    {
        super.renderContents(x, y, z);

        if (this.icon != null)
        {
            int ix = x + this.getWidth() - this.icon.getWidth();
            int iy = y + this.getHeight() / 2 - this.icon.getHeight() / 2;
            this.icon.renderAt(ix, iy, z);
        }
        else if (this.renderStyle == Style.ON_OFF_SLIDER)
        {
            int height = this.getHeight();
            int sx = x + this.getWidth() - this.sliderWidth;

            ShapeRenderUtils.renderRectangle(sx, y, z, this.sliderWidth, height, 0x70000000);
            OnOffButton.renderOnOffSlider(sx, y, z, this.sliderWidth, height, this.lastValue, true, false);

            int tx = this.getSliderStyleTextStartX(sx + 4, this.lastValue);
            int ty = y + height / 2 - this.fontHeight / 2;
            this.renderTextLine(tx, ty, z, -1, true, this.lastValue ? this.textOn : this.textOff);
        }
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();

        obj.addProperty("render_style", this.renderStyle.getName());

        return obj;
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        super.fromJson(obj);

        if (JsonUtils.hasString(obj, "render_style"))
        {
            this.renderStyle = Style.fromName(JsonUtils.getString(obj, "render_style"));
        }
    }

    public enum Style
    {
        ON_OFF_TEXT     ("on_off_text",     "malilib.label.boolean_config_status_type.on_off_text"),
        TRUE_FALSE_TEXT ("true_false_text", "malilib.label.boolean_config_status_type.true_false_text"),
        ON_OFF_SLIDER   ("on_off_slider",   "malilib.label.boolean_config_status_type.on_off_slider"),
        ON_OFF_LIGHT    ("on_off_light",    "malilib.label.boolean_config_status_type.on_off_light");

        protected final String name;
        protected final String translationKey;

        public static final ImmutableList<Style> VALUES = ImmutableList.copyOf(values());

        Style(String name, String translationKey)
        {
            this.name = name;
            this.translationKey = translationKey;
        }

        public String getName()
        {
            return this.name;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.translationKey);
        }

        public static Style fromName(String name)
        {
            for (Style style : VALUES)
            {
                if (style.name.equalsIgnoreCase(name))
                {
                    return style;
                }
            }

            return ON_OFF_TEXT;
        }
    }
}
