package fi.dy.masa.malilib.overlay.widget.sub;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.option.StringConfig;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextStyle;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class StringConfigStatusWidget extends BaseConfigStatusIndicatorWidget<StringConfig>
{
    protected TextStyle style = TextStyle.builder().withColor(0xFF00FFFF).build();
    protected String lastValue = "";

    public StringConfigStatusWidget(StringConfig config, ConfigOnTab configOnTab)
    {
        this(config, configOnTab, MaLiLibReference.MOD_ID + ":csi_value_string");
    }

    public StringConfigStatusWidget(StringConfig config, ConfigOnTab configOnTab, String widgetTypeId)
    {
        super(config, configOnTab, widgetTypeId);
    }

    @Override
    public void updateState(boolean force)
    {
        if (force || this.config.getValue().equals(this.lastValue) == false)
        {
            this.updateValue();
        }
    }

    protected void updateValue()
    {
        this.lastValue = this.config.getValue();
        this.valueDisplayText = StyledTextLine.of(this.lastValue, this.style);
        this.valueRenderWidth = this.valueDisplayText.renderWidth;
    }
}
