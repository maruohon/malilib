package fi.dy.masa.malilib.overlay.widget;

import java.util.List;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class HotkeyedBooleanConfigStatusWidget extends BaseConfigStatusIndicatorWidget<HotkeyedBooleanConfig>
{
    protected boolean lastBoolean;
    protected List<Integer> lastKeys;
    protected boolean showKeys;
    protected boolean showBoolean = true;

    public HotkeyedBooleanConfigStatusWidget(HotkeyedBooleanConfig config, ConfigOnTab configOnTab)
    {
        super(config, configOnTab);

        this.updateValue();
    }

    @Override
    public void updateState()
    {
        if (this.lastKeys.equals(this.config.getKeyBind().getKeys()) == false ||
            this.lastBoolean != this.config.getBooleanValue())
        {
            this.updateValue();
        }
    }

    protected void updateValue()
    {
        this.lastBoolean = this.config.getBooleanValue();
        this.lastKeys = this.config.getKeyBind().getKeys();

        if (this.showBoolean)
        {
            if (this.lastBoolean)
            {
                this.valueDisplayText = StyledTextLine.translatedOf("malilib.label.on.caps", BooleanConfigStatusWidget.STYLE_ON);
            }
            else
            {
                this.valueDisplayText = StyledTextLine.translatedOf("malilib.label.off.caps", BooleanConfigStatusWidget.STYLE_OFF);
            }
        }

        String keysString = this.config.getKeyBind().getKeysDisplayString();

        if (this.showBoolean && this.showKeys)
        {
            this.valueDisplayText = this.valueDisplayText.append(StyledTextLine.of(String.format(" [%s]", keysString)));
        }
        else if (this.showKeys)
        {
            this.valueDisplayText = StyledTextLine.of(String.format("[%s]", keysString));
        }

        this.valueRenderWidth = this.valueDisplayText.renderWidth;
    }
}
