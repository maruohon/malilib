package fi.dy.masa.malilib.overlay.widget;

import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextStyle;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class StringConfigStatusWidget extends BaseConfigStatusIndicatorWidget<StringConfig>
{
    protected TextStyle style = TextStyle.builder().withColor(0xFF00FFFF).build();
    protected String lastValue;

    public StringConfigStatusWidget(StringConfig config, ConfigOnTab configOnTab)
    {
        super(config, configOnTab);

        this.updateValue();
    }

    @Override
    public void updateState(boolean force)
    {
        if (force || this.lastValue.equals(this.config.getStringValue()) == false)
        {
            this.updateValue();
        }
    }

    protected void updateValue()
    {
        this.lastValue = this.config.getStringValue();
        this.valueDisplayText = StyledTextLine.of(this.lastValue, this.style);
        this.valueRenderWidth = this.valueDisplayText.renderWidth;
    }
}
