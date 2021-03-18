package fi.dy.masa.malilib.overlay.widget;

import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextStyle;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class OptionListConfigStatusWidget extends BaseConfigStatusIndicatorWidget<OptionListConfig<OptionListConfigValue>>
{
    protected TextStyle style = TextStyle.builder().withColor(0xFFFFFF00).build();
    protected OptionListConfigValue lastValue;

    public OptionListConfigStatusWidget(OptionListConfig<OptionListConfigValue> config, ConfigOnTab configOnTab)
    {
        super(config, configOnTab);

        this.updateValue();
    }

    @Override
    public void updateState()
    {
        if (this.lastValue != this.config.getValue())
        {
            this.updateValue();
        }
    }

    protected void updateValue()
    {
        this.lastValue = this.config.getValue();
        this.valueDisplayText = StyledTextLine.of(this.lastValue.getDisplayName(), this.style);
        this.valueRenderWidth = this.valueDisplayText.renderWidth;
    }
}
