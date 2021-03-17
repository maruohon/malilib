package fi.dy.masa.malilib.overlay.widget;

import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class IntegerConfigStatusWidget extends BaseConfigStatusIndicatorWidget<IntegerConfig>
{
    protected StyledTextLine valueDisplayText;
    protected int lastValue;

    public IntegerConfigStatusWidget(IntegerConfig config, ConfigOnTab configOnTab)
    {
        super(config, configOnTab);

        this.updateValue();
    }

    protected void updateValue()
    {
        this.lastValue = this.config.getIntegerValue();
        this.valueDisplayText = StyledTextLine.of(String.valueOf(this.lastValue));
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        if (this.lastValue != this.config.getIntegerValue())
        {
            this.updateValue();
        }

        super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hovered);

        int ty = y + this.getHeight() / 2 - this.fontHeight / 2;
        this.renderTextLine(this.getRight() - this.valueDisplayText.renderWidth, ty, z,
                            this.valueColor, this.valueShadow, this.valueDisplayText);
    }
}
