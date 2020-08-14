package fi.dy.masa.malilib.gui.widget.button;

import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.gui.icon.BaseIcon;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.message.MessageHelpers;

public class BooleanConfigButton extends GenericButton
{
    protected final BooleanConfig config;
    protected OnOffStyle style;

    public BooleanConfigButton(int x, int y, int width, int height, BooleanConfig config)
    {
        this(x, y, width, height, config, OnOffStyle.SLIDER_ON_OFF);
    }

    public BooleanConfigButton(int x, int y, int width, int height, BooleanConfig config, OnOffStyle style)
    {
        super(x, y, width, height, "");

        this.config = config;
        this.style = style;

        this.setBackgroundAndBorderColors(0xFF303030, 0xFF000000, 0xFF000000);
        this.updateWidth();
        this.updateDisplayString();
    }

    public BooleanConfigButton setStyle(OnOffStyle style)
    {
        this.style = style;
        this.updateWidth();
        this.updateDisplayString();
        return this;
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        this.config.toggleBooleanValue();
        this.updateDisplayString();

        return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
    }

    @Override
    protected String generateDisplayString()
    {
        boolean value = this.config.getBooleanValue();
        boolean isSlider = this.style == OnOffStyle.SLIDER_ON_OFF;
        String valueStr = this.generateDisplayStringForState(value);

        this.setBorderWidth(isSlider ? 1 : 0);
        this.backgroundEnabled = isSlider;
        this.textColorNormal = isSlider ? (value ? 0xFFE0E0E0 : 0xFF707070) : 0xFFE0E0E0;
        this.textColorHovered = isSlider ? 0xFFF0F000 : 0xFFFFFFA0;

        return valueStr;
    }

    @Override
    public void updateWidth()
    {
        if (this.automaticWidth)
        {
            int width = Math.max(BaseIcon.SLIDER_GREEN.getWidth(), BaseIcon.SLIDER_RED.getWidth());
            int sw1 = this.getStringWidth(this.generateDisplayStringForState(false));
            int sw2 = this.getStringWidth(this.generateDisplayStringForState(true));
            width += Math.max(sw1, sw2) + 8;

            this.setWidth(width);
        }
    }

    protected String generateDisplayStringForState(boolean value)
    {
        boolean isSlider = this.style == OnOffStyle.SLIDER_ON_OFF;
        String valueStr = "?";

        if (isSlider)
        {
            valueStr = MessageHelpers.getOnOff(value, true);
        }
        else if (this.style == OnOffStyle.TEXT_ON_OFF)
        {
            valueStr = MessageHelpers.getOnOffColored(value, true);
        }
        else if (this.style == OnOffStyle.TEXT_TRUE_FALSE)
        {
            valueStr = MessageHelpers.getTrueFalseColored(value, true);
        }

        return valueStr;
    }

    @Override
    protected int getTextStartX(int x, int width)
    {
        if (this.style == OnOffStyle.SLIDER_ON_OFF)
        {
            boolean value = this.config.getBooleanValue();
            Icon icon = value ? BaseIcon.SLIDER_GREEN : BaseIcon.SLIDER_RED;
            int iconWidth = icon.getWidth();
            int offX = (width - iconWidth) / 2;

            return x + (value ? offX : offX + iconWidth);
        }

        return super.getTextStartX(x, width);
    }

    @Override
    protected void renderButtonBackground(int x, int y, int width, int height, boolean hovered)
    {
        if (this.style == OnOffStyle.SLIDER_ON_OFF)
        {
            boolean value = this.config.getBooleanValue();
            Icon icon = value ? BaseIcon.SLIDER_GREEN : BaseIcon.SLIDER_RED;

            int iconWidth = icon.getWidth();
            int iconHeight1 = height / 2 - 1;
            int iconHeight2 = (height % 2) != 0 ? iconHeight1 + 1 : iconHeight1; // Account for odd height
            int sliderX = value ? x + width - iconWidth - 1 : x + 1;
            int z = this.getZLevel();
            int u = icon.getU();
            int v1 = icon.getV();
            int v2 = v1 + icon.getHeight() - iconHeight2;

            this.bindTexture(icon.getTexture());

            RenderUtils.drawTexturedRect(sliderX, y + 1              , u, v1, iconWidth, iconHeight1, z);
            RenderUtils.drawTexturedRect(sliderX, y + 1 + iconHeight1, u, v2, iconWidth, iconHeight2, z);
        }
        else
        {
            super.renderButtonBackground(x, y, width, height, hovered);
        }
    }
}
