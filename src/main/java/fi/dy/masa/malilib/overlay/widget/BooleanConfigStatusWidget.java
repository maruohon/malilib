package fi.dy.masa.malilib.overlay.widget;

import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextStyle;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class BooleanConfigStatusWidget extends BaseConfigStatusIndicatorWidget<BooleanConfig>
{
    public static final TextStyle STYLE_ON  = TextStyle.builder().withColor(0xFF00FF00).build();
    public static final TextStyle STYLE_OFF = TextStyle.builder().withColor(0xFFFF0000).build();

    protected boolean lastValue;

    public BooleanConfigStatusWidget(BooleanConfig config, ConfigOnTab configOnTab)
    {
        super(config, configOnTab);

        this.updateValue();
    }

    @Override
    public void updateState()
    {
        if (this.lastValue != this.config.getBooleanValue())
        {
            this.updateValue();
        }
    }

    protected void updateValue()
    {
        this.lastValue = this.config.getBooleanValue();

        if (this.lastValue)
        {
            this.valueDisplayText = StyledTextLine.translatedOf("malilib.label.on.caps", STYLE_ON);
        }
        else
        {
            this.valueDisplayText = StyledTextLine.translatedOf("malilib.label.off.caps", STYLE_OFF);
        }

        this.valueRenderWidth = this.valueDisplayText.renderWidth;
        this.notifyContainerOfChanges(true);
    }

    @Override
    protected void renderContents(int x, int y, float z)
    {
        super.renderContents(x, y, z);
    }
}
