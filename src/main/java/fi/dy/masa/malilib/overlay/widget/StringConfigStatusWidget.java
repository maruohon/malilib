package fi.dy.masa.malilib.overlay.widget;

import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class StringConfigStatusWidget extends BaseConfigStatusIndicatorWidget<StringConfig>
{
    protected StyledTextLine valueDisplayText;
    protected String lastValue;

    public StringConfigStatusWidget(StringConfig config, ConfigOnTab configOnTab)
    {
        super(config, configOnTab);

        this.updateValue();
    }

    protected void updateValue()
    {
        this.lastValue = this.config.getStringValue();
        this.valueDisplayText = StyledTextLine.of(this.lastValue);
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        if (this.lastValue.equals(this.config.getStringValue()) == false)
        {
            this.updateValue();
        }

        super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hovered);

        int ty = y + this.getHeight() / 2 - this.fontHeight / 2;
        this.renderTextLine(this.getRight() - this.valueDisplayText.renderWidth, ty, z,
                            this.valueColor, this.valueShadow, this.valueDisplayText);
    }
}
