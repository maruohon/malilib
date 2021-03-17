package fi.dy.masa.malilib.overlay.widget;

import java.util.List;
import fi.dy.masa.malilib.config.option.HotkeyConfig;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class HotkeyConfigStatusWidget extends BaseConfigStatusIndicatorWidget<HotkeyConfig>
{
    protected StyledTextLine valueDisplayText;
    protected List<Integer> lastValue;

    public HotkeyConfigStatusWidget(HotkeyConfig config, ConfigOnTab configOnTab)
    {
        super(config, configOnTab);

        this.updateValue();
    }

    protected void updateValue()
    {
        this.lastValue = this.config.getValue().getKeys();
        this.valueDisplayText = StyledTextLine.of(this.config.getValue().getKeysDisplayString());
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        if (this.lastValue.equals(this.config.getValue().getKeys()) == false)
        {
            this.updateValue();
        }

        super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hovered);

        int ty = y + this.getHeight() / 2 - this.fontHeight / 2;
        this.renderTextLine(this.getRight() - this.valueDisplayText.renderWidth, ty, z,
                            this.valueColor, this.valueShadow, this.valueDisplayText);
    }
}
