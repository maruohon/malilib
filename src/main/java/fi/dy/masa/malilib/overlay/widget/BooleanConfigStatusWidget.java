package fi.dy.masa.malilib.overlay.widget;

import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextStyle;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class BooleanConfigStatusWidget extends BaseConfigStatusIndicatorWidget<BooleanConfig>
{
    protected final TextStyle styleOn = TextStyle.builder().withColor(Color4f.fromColor(0xFF00FF00)).build();
    protected final TextStyle styleOff = TextStyle.builder().withColor(Color4f.fromColor(0xFFFF0000)).build();
    protected StyledTextLine valueDisplayText;
    protected boolean lastValue;

    public BooleanConfigStatusWidget(BooleanConfig config, ConfigOnTab configOnTab)
    {
        super(config, configOnTab);

        this.updateValue();
    }

    protected void updateValue()
    {
        this.lastValue = this.config.getBooleanValue();

        if (this.lastValue)
        {
            this.valueDisplayText = StyledTextLine.translatedOf("malilib.label.on.caps", this.styleOn);
        }
        else
        {
            this.valueDisplayText = StyledTextLine.translatedOf("malilib.label.off.caps", this.styleOff);
        }
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        if (this.lastValue != this.config.getBooleanValue())
        {
            this.updateValue();
        }

        super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hovered);

        int ty = y + this.getHeight() / 2 - this.fontHeight / 2;
        this.renderTextLine(this.getRight() - this.valueDisplayText.renderWidth, ty, z,
                            this.valueColor, this.valueShadow, this.valueDisplayText);
    }
}
