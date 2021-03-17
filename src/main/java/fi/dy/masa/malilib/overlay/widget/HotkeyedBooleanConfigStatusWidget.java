package fi.dy.masa.malilib.overlay.widget;

import java.util.List;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextStyle;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class HotkeyedBooleanConfigStatusWidget extends BaseConfigStatusIndicatorWidget<HotkeyedBooleanConfig>
{
    protected final TextStyle styleOn = TextStyle.builder().withColor(Color4f.fromColor(0xFF00FF00)).build();
    protected final TextStyle styleOff = TextStyle.builder().withColor(Color4f.fromColor(0xFFFF0000)).build();
    protected StyledTextLine valueDisplayText;
    protected boolean lastBoolean;
    protected List<Integer> lastKeys;
    protected boolean showKeys;
    protected boolean showBoolean = true;

    public HotkeyedBooleanConfigStatusWidget(HotkeyedBooleanConfig config, ConfigOnTab configOnTab)
    {
        super(config, configOnTab);

        this.updateValue();
    }

    protected void updateValue()
    {
        this.lastBoolean = this.config.getBooleanValue();
        this.lastKeys = this.config.getKeyBind().getKeys();

        if (this.showBoolean)
        {
            if (this.lastBoolean)
            {
                this.valueDisplayText = StyledTextLine.translatedOf("malilib.label.on.caps", this.styleOn);
            }
            else
            {
                this.valueDisplayText = StyledTextLine.translatedOf("malilib.label.off.caps", this.styleOff);
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
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        if (this.lastKeys.equals(this.config.getKeyBind().getKeys()) == false ||
            this.lastBoolean != this.config.getBooleanValue())
        {
            this.updateValue();
        }

        super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hovered);

        int ty = y + this.getHeight() / 2 - this.fontHeight / 2;
        this.renderTextLine(this.getRight() - this.valueDisplayText.renderWidth, ty, z,
                            this.valueColor, this.valueShadow, this.valueDisplayText);
    }
}
