package fi.dy.masa.malilib.overlay.widget;

import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class IntegerConfigStatusWidget extends BaseConfigStatusIndicatorWidget<IntegerConfig>
{
    protected int lastValue;

    public IntegerConfigStatusWidget(IntegerConfig config, ConfigOnTab configOnTab)
    {
        super(config, configOnTab);

        this.updateValue();
    }

    @Override
    public void updateState(boolean force)
    {
        if (force || this.lastValue != this.config.getIntegerValue())
        {
            this.updateValue();
        }
    }

    protected void updateValue()
    {
        this.lastValue = this.config.getIntegerValue();
        this.valueDisplayText = StyledTextLine.of(String.valueOf(this.lastValue));
        this.valueRenderWidth = this.valueDisplayText.renderWidth;
    }
}
