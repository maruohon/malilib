package malilib.overlay.widget.sub;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import malilib.MaLiLibReference;
import malilib.config.option.BooleanConfig;
import malilib.gui.BaseScreen;
import malilib.gui.config.indicator.BooleanConfigStatusIndicatorEditScreen;
import malilib.gui.icon.DefaultIcons;
import malilib.gui.util.GuiUtils;
import malilib.gui.util.ScreenContext;
import malilib.gui.widget.button.OnOffButton;
import malilib.render.ShapeRenderUtils;
import malilib.render.text.StyledTextLine;
import malilib.render.text.TextStyle;
import malilib.util.StringUtils;
import malilib.util.data.ConfigOnTab;
import malilib.util.data.json.JsonUtils;

public class BooleanConfigStatusWidget extends BaseConfigStatusIndicatorWidget<BooleanConfig>
{
    public static final TextStyle STYLE_ON  = TextStyle.builder().withColor(0xFF00FF00).build();
    public static final TextStyle STYLE_OFF = TextStyle.builder().withColor(0xFFFF0000).build();

    protected final StyledTextLine textOn;
    protected final StyledTextLine textOff;
    protected final int sliderWidth;
    protected EnabledCondition condition = EnabledCondition.ALWAYS;
    protected Style renderStyle = Style.ON_OFF_SLIDER;
    protected boolean lastValue;
    protected int booleanValueRenderWidth;

    public BooleanConfigStatusWidget(BooleanConfig config, ConfigOnTab configOnTab)
    {
        this(config, configOnTab, MaLiLibReference.MOD_ID + ":csi_value_boolean");
    }

    public BooleanConfigStatusWidget(BooleanConfig config, ConfigOnTab configOnTab, String widgetTypeId)
    {
        super(config, configOnTab, widgetTypeId);

        this.textOn  = StyledTextLine.translate(STYLE_ON, "malilib.label.misc.on.caps");
        this.textOff = StyledTextLine.translate(STYLE_OFF, "malilib.label.misc.off.caps");
        int sw = Math.max(this.textOn.renderWidth, this.textOff.renderWidth);
        sw += DefaultIcons.SLIDER_GREEN.getWidth() + 6;
        this.sliderWidth = sw;
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

    public EnabledCondition getEnabledCondition()
    {
        return this.condition;
    }

    public void setEnabledCondition(EnabledCondition condition)
    {
        this.condition = condition;
        this.updateEnabledState();
    }

    @Override
    public void openEditScreen()
    {
        BooleanConfigStatusIndicatorEditScreen<?> screen = new BooleanConfigStatusIndicatorEditScreen<>(this);
        screen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(screen);
    }

    @Override
    public void updateState(boolean force)
    {
        if (force || this.isModified())
        {
            this.updateEnabledState();
            this.updateValue();
        }
    }

    protected boolean isModified()
    {
        return this.lastValue != this.config.getBooleanValue();
    }

    protected void updateEnabledState()
    {
        boolean enabled = this.condition == EnabledCondition.ALWAYS ||
                          (this.condition == EnabledCondition.WHEN_ON) == this.lastValue;
        this.setEnabled(enabled);
    }

    protected void updateValue()
    {
        this.lastValue = this.config.getBooleanValue();
        this.valueDisplayText = null;
        this.valueRenderWidth = 0;
        this.booleanValueRenderWidth = 0;

        this.setIcon(null);
        this.updateBooleanDisplayValue();
        this.updateEnabledState();
        this.geometryResizeNotifier.checkAndNotifyContainerOfChanges(false);
    }

    protected void updateBooleanDisplayValue()
    {
        if (this.renderStyle == Style.ON_OFF_TEXT)
        {
            this.valueDisplayText = this.lastValue ? this.textOn : this.textOff;
            this.booleanValueRenderWidth = this.valueDisplayText.renderWidth;
        }
        else if (this.renderStyle == Style.TRUE_FALSE_TEXT)
        {
            String translationKey = this.lastValue ? "malilib.label.misc.true.lower_case" : "malilib.label.misc.false.lower_case";
            TextStyle style = this.lastValue ? STYLE_ON : STYLE_OFF;
            this.valueDisplayText = StyledTextLine.translate(style, translationKey);
            this.booleanValueRenderWidth = this.valueDisplayText.renderWidth;
        }
        else if (this.renderStyle == Style.ON_OFF_SLIDER)
        {
            this.booleanValueRenderWidth = this.sliderWidth;
        }
        else if (this.renderStyle == Style.ON_OFF_LIGHT)
        {
            this.setIcon(this.lastValue ? DefaultIcons.LIGHT_GREEN_ON : DefaultIcons.LIGHT_RED_OFF);
            this.booleanValueRenderWidth = this.getIcon().getWidth();
        }

        this.valueRenderWidth = this.booleanValueRenderWidth;
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
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        super.renderAt(x, y, z, ctx);

        this.renderValueIndicator(x, y, z, ctx);
    }

    protected void renderValueIndicator(int x, int y, float z, ScreenContext ctx)
    {
        if (this.renderStyle == Style.ON_OFF_SLIDER)
        {
            this.renderSlider(x, y, z, ctx);
        }
        else
        {
            this.renderIcon(x, y, z, ctx);
        }
    }

    @Override
    protected int getIconPositionX(int x, int usableWidth, int iconWidth)
    {
        return x + this.getWidth() - this.booleanValueRenderWidth;
    }

    protected void renderSlider(int x, int y, float z, ScreenContext ctx)
    {
        int height = this.getHeight();
        int sx = x + this.getWidth() - this.sliderWidth;

        ShapeRenderUtils.renderRectangle(sx, y, z, this.sliderWidth, height, 0x70000000);
        OnOffButton.renderOnOffSlider(sx, y, z, this.sliderWidth, height, this.lastValue, true, false,
                                      DefaultIcons.SLIDER_GREEN, DefaultIcons.SLIDER_RED, ctx);

        int usableHeight = this.getHeight() - this.padding.getVerticalTotal();
        int tx = this.getSliderStyleTextStartX(sx + 4, this.lastValue);
        int ty = this.getTextPositionY(y, usableHeight, this.getLineHeight());
        this.renderTextLine(tx, ty, z, -1, true, this.lastValue ? this.textOn : this.textOff, ctx);
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();

        obj.addProperty("render_style", this.renderStyle.getName());
        obj.addProperty("condition", this.condition.getName());

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

        if (JsonUtils.hasString(obj, "condition"))
        {
            this.condition = EnabledCondition.fromName(JsonUtils.getString(obj, "condition"));
        }
    }

    public enum Style
    {
        ON_OFF_TEXT     ("on_off_text",     "malilib.label.boolean_config_status.style.on_off_text"),
        TRUE_FALSE_TEXT ("true_false_text", "malilib.label.boolean_config_status.style.true_false_text"),
        ON_OFF_SLIDER   ("on_off_slider",   "malilib.label.boolean_config_status.style.on_off_slider"),
        ON_OFF_LIGHT    ("on_off_light",    "malilib.label.boolean_config_status.style.on_off_light");

        private final String name;
        private final String translationKey;

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

    public enum EnabledCondition
    {
        ALWAYS   ("always",   "malilib.name.enabled_condition.always"),
        WHEN_ON  ("when_on",  "malilib.name.enabled_condition.when_on"),
        WHEN_OFF ("when_off", "malilib.name.enabled_condition.when_off");

        private final String name;
        private final String translationKey;

        public static final ImmutableList<EnabledCondition> VALUES = ImmutableList.copyOf(values());

        EnabledCondition(String name, String translationKey)
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

        public static EnabledCondition fromName(String name)
        {
            for (EnabledCondition value : VALUES)
            {
                if (value.name.equalsIgnoreCase(name))
                {
                    return value;
                }
            }

            return ALWAYS;
        }
    }
}
