package fi.dy.masa.malilib.overlay.widget;

import fi.dy.masa.malilib.config.option.DoubleConfig;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class DoubleConfigStatusWidget extends BaseConfigStatusIndicatorWidget<DoubleConfig>
{
    protected StyledTextLine valueDisplayText;
    protected double lastValue;

    public DoubleConfigStatusWidget(DoubleConfig config, ConfigOnTab configOnTab)
    {
        super(config, configOnTab);

        this.updateValue();
    }

    protected void updateValue()
    {
        this.lastValue = this.config.getDoubleValue();
        this.valueDisplayText = StyledTextLine.of(String.valueOf(this.lastValue));
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        if (this.lastValue != this.config.getDoubleValue())
        {
            this.updateValue();
        }

        super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hovered);

        int ty = y + this.getHeight() / 2 - this.fontHeight / 2;
        this.renderTextLine(this.getRight() - this.valueDisplayText.renderWidth, ty, z,
                            this.valueColor, this.valueShadow, this.valueDisplayText);
    }
}
