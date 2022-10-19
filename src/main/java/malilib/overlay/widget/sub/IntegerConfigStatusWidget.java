package malilib.overlay.widget.sub;

import malilib.MaLiLibReference;
import malilib.config.option.IntegerConfig;
import malilib.render.text.StyledTextLine;
import malilib.util.data.ConfigOnTab;

public class IntegerConfigStatusWidget extends BaseConfigStatusIndicatorWidget<IntegerConfig>
{
    protected int lastValue;

    public IntegerConfigStatusWidget(IntegerConfig config, ConfigOnTab configOnTab)
    {
        this(config, configOnTab, MaLiLibReference.MOD_ID + ":csi_value_integer");
    }

    public IntegerConfigStatusWidget(IntegerConfig config, ConfigOnTab configOnTab, String widgetTypeId)
    {
        super(config, configOnTab, widgetTypeId);
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
