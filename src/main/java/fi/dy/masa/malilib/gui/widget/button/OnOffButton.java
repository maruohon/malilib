package fi.dy.masa.malilib.gui.widget.button;

import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.message.MessageHelpers;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class OnOffButton extends GenericButton
{
    @Nullable protected final String translationKey;
    protected final BooleanSupplier statusSupplier;
    protected OnOffStyle style;

    /**
     * Pass -1 as the <b>width</b> to automatically set the width
     * to a value where the ON and OFF state buttons are the same width.
     * @param x
     * @param y
     * @param width
     * @param height
     * @param style The button style to use
     * @param statusSupplier The supplier for the current on/off status of this button
     * @param translationKey The translation key to use for the full button text. It should have one %s format specifier for the current status string. Pass null to use the status string directly, without any other labeling text.
     * @param hoverStrings
     */
    public OnOffButton(int x, int y, int width, int height, OnOffStyle style, BooleanSupplier statusSupplier,
                       @Nullable String translationKey, String... hoverStrings)
    {
        super(x, y, width, height, "", hoverStrings);

        this.translationKey = translationKey;
        this.statusSupplier = statusSupplier;

        this.setBackgroundColor(0xFF303030);
        this.setNormalBorderColor(0xFF000000);
        this.setRenderHoverBackground(false);
        this.setStyle(style);
        this.updateWidth();
        this.updateDisplayString();
    }

    public OnOffButton setStyle(OnOffStyle style)
    {
        this.style = style;
        return this;
    }

    @Override
    protected String generateDisplayString()
    {
        // FIXME The null check here is required currently due to constructor hierarchy problems...
        boolean value = this.statusSupplier != null && this.statusSupplier.getAsBoolean();
        boolean isSlider = this.style == OnOffStyle.SLIDER_ON_OFF;
        String valueStr = this.getDisplayStringForState(value);

        this.setBorderWidth(isSlider ? 1 : 0);
        this.backgroundEnabled = isSlider;
        this.textColorNormal = isSlider ? (value ? 0xFFE0E0E0 : 0xFF909090) : 0xFFE0E0E0;
        this.textColorHovered = isSlider ? 0xFFF0F000 : 0xFFFFFFA0;

        return valueStr;
    }

    @Override
    public void updateWidth()
    {
        if (this.automaticWidth)
        {
            int sw1 = this.getStringWidth(this.getDisplayStringForState(false));
            int sw2 = this.getStringWidth(this.getDisplayStringForState(true));
            int width = Math.max(sw1, sw2) + 10;

            if (this.style == OnOffStyle.SLIDER_ON_OFF)
            {
                width += Math.max(DefaultIcons.SLIDER_GREEN.getWidth(), DefaultIcons.SLIDER_RED.getWidth());
            }

            this.setWidth(width);
        }
    }

    protected String getOnOffStringForState(boolean value)
    {
        String valueStr = "?";

        if (this.style == OnOffStyle.SLIDER_ON_OFF)
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

    public String getDisplayStringForState(boolean value)
    {
        String valueStr = this.getOnOffStringForState(value);
        return this.translationKey != null ? StringUtils.translate(this.translationKey, valueStr) : valueStr;
    }

    @Override
    protected int getTextStartX(int baseX, int usableWidth, int textWidth)
    {
        if (this.style == OnOffStyle.SLIDER_ON_OFF)
        {
            boolean value = this.statusSupplier.getAsBoolean();
            Icon icon = value ? DefaultIcons.SLIDER_GREEN : DefaultIcons.SLIDER_RED;
            int iconWidth = icon.getWidth();

            usableWidth -= iconWidth;

            // The slider is on the left side
            if (value == false)
            {
                baseX += iconWidth;
            }
        }

        return super.getTextStartX(baseX, usableWidth, textWidth);
    }

    @Override
    protected void renderButtonBackground(int x, int y, float z, int width, int height, boolean hovered)
    {
        if (this.style == OnOffStyle.SLIDER_ON_OFF)
        {
            boolean value = this.statusSupplier.getAsBoolean();
            MultiIcon icon = value ? DefaultIcons.SLIDER_GREEN : DefaultIcons.SLIDER_RED;

            int iconWidth = icon.getWidth();
            int iconHeight1 = height / 2 - 1;
            int iconHeight2 = (height % 2) != 0 ? iconHeight1 + 1 : iconHeight1; // Account for odd height
            int sliderX = value ? x + width - iconWidth - 1 : x + 1;
            int variantIndex = icon.getVariantIndex(this.enabled, hovered);
            int u = icon.getVariantU(variantIndex);
            int v1 = icon.getVariantV(variantIndex);
            int v2 = v1 + icon.getHeight() - iconHeight2;

            this.bindTexture(icon.getTexture());

            ShapeRenderUtils.renderTexturedRectangle(sliderX, y + 1              , z, u, v1, iconWidth, iconHeight1);
            ShapeRenderUtils.renderTexturedRectangle(sliderX, y + 1 + iconHeight1, z, u, v2, iconWidth, iconHeight2);
        }
        else
        {
            super.renderButtonBackground(x, y, z, width, height, hovered);
        }
    }
}
