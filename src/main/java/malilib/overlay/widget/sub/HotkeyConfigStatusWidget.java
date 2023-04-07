package malilib.overlay.widget.sub;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import malilib.MaLiLibReference;
import malilib.config.option.HotkeyConfig;
import malilib.render.text.StyledTextLine;
import malilib.render.text.TextStyle;
import malilib.util.data.ConfigOnTab;

public class HotkeyConfigStatusWidget extends BaseConfigStatusIndicatorWidget<HotkeyConfig>
{
    protected final TextStyle style = TextStyle.builder().withColor(0xFFFFA000).build();
    protected final IntArrayList lastValue = new IntArrayList();

    public HotkeyConfigStatusWidget(HotkeyConfig config, ConfigOnTab configOnTab)
    {
        this(config, configOnTab, MaLiLibReference.MOD_ID + ":csi_value_hotkey");
    }

    public HotkeyConfigStatusWidget(HotkeyConfig config, ConfigOnTab configOnTab, String widgetTypeId)
    {
        super(config, configOnTab, widgetTypeId);
    }

    @Override
    public void updateState(boolean force)
    {
        if (force || this.config.getValue().matches(this.lastValue) == false)
        {
            this.updateValue();
        }
    }

    protected void updateValue()
    {
        this.lastValue.clear();
        this.config.getValue().getKeysToList(this.lastValue);
        this.valueDisplayText = StyledTextLine.parseFirstLine(this.config.getValue().getKeysDisplayString(), this.style);
        this.valueRenderWidth = this.valueDisplayText.renderWidth;
    }
}
