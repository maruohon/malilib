package fi.dy.masa.malilib.overlay.widget;

import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class OptionListConfigStatusWidget extends BaseConfigStatusIndicatorWidget<OptionListConfig<OptionListConfigValue>>
{
    protected StyledTextLine valueDisplayText;
    protected OptionListConfigValue lastValue;

    public OptionListConfigStatusWidget(OptionListConfig<OptionListConfigValue> config, ConfigOnTab configOnTab)
    {
        super(config, configOnTab);

        this.updateValue();
    }

    protected void updateValue()
    {
        this.lastValue = this.config.getValue();
        this.valueDisplayText = StyledTextLine.of(this.lastValue.getDisplayName());
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        if (this.lastValue != this.config.getValue())
        {
            this.updateValue();
        }

        super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hovered);

        int ty = y + this.getHeight() / 2 - this.fontHeight / 2;
        this.renderTextLine(this.getRight() - this.valueDisplayText.renderWidth, ty, z,
                            this.valueColor, this.valueShadow, this.valueDisplayText);
    }
}
