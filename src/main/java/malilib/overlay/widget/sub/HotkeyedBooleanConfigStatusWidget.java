package malilib.overlay.widget.sub;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import malilib.MaLiLibReference;
import malilib.config.option.BooleanConfig;
import malilib.config.option.HotkeyedBooleanConfig;
import malilib.gui.BaseScreen;
import malilib.gui.config.indicator.HotkeyedBooleanConfigStatusIndicatorEditScreen;
import malilib.gui.util.ScreenContext;
import malilib.input.KeyBind;
import malilib.render.text.StyledTextLine;
import malilib.util.data.ConfigOnTab;

public class HotkeyedBooleanConfigStatusWidget extends BooleanConfigStatusWidget
{
    protected final Supplier<KeyBind> keyBindSupplier;
    protected final IntArrayList lastKeys = new IntArrayList();
    @Nullable protected StyledTextLine keysText;
    protected boolean showKeys;
    protected boolean showBoolean = true;

    public HotkeyedBooleanConfigStatusWidget(HotkeyedBooleanConfig config, ConfigOnTab configOnTab)
    {
        this(config, configOnTab, MaLiLibReference.MOD_ID + ":csi_value_hotkeyed_boolean");
    }

    public HotkeyedBooleanConfigStatusWidget(HotkeyedBooleanConfig config, ConfigOnTab configOnTab, String widgetTypeId)
    {
        this(config, config::getKeyBind, configOnTab, widgetTypeId);
    }

    public HotkeyedBooleanConfigStatusWidget(BooleanConfig booleanConfig, Supplier<KeyBind> keyBindSupplier,
                                             ConfigOnTab configOnTab, String widgetTypeId)
    {
        super(booleanConfig, configOnTab, widgetTypeId);

        this.keyBindSupplier = keyBindSupplier;
    }

    public boolean getShowBoolean()
    {
        return this.showBoolean;
    }

    public boolean getShowHotkey()
    {
        return this.showKeys;
    }

    public void toggleShowBoolean()
    {
        this.showBoolean = ! this.showBoolean;
        this.updateValue();
    }

    public void toggleShowHotkey()
    {
        this.showKeys = ! this.showKeys;
        this.updateValue();
    }

    @Override
    public void openEditScreen()
    {
        BaseScreen.openScreenWithParent(new HotkeyedBooleanConfigStatusIndicatorEditScreen(this));
    }

    @Override
    protected boolean isModified()
    {
        return this.lastValue != this.config.getBooleanValue() ||
               this.keyBindSupplier.get().matches(this.lastKeys) == false;
    }

    @Override
    protected void updateValue()
    {
        super.updateValue();

        this.lastKeys.clear();
        this.keyBindSupplier.get().getKeysToList(this.lastKeys);
        this.keysText = null;

        if (this.showKeys)
        {
            String keysString = this.keyBindSupplier.get().getKeysDisplayString();
            String key = "malilib.label.config_status_indicator.hotkeys_string";
            this.keysText = StyledTextLine.translateFirstLine(key, keysString);
            this.valueRenderWidth += this.keysText.renderWidth + 4;
        }
    }

    @Override
    protected void renderValueDisplayText(int x, int textY, float z, ScreenContext ctx)
    {
        if (this.showBoolean)
        {
            super.renderValueDisplayText(x, textY, z, ctx);
        }

        if (this.showKeys)
        {
            this.renderHotkeysValue(x, textY, z, ctx);
        }
    }

    @Override
    protected void renderValueIndicator(int x, int y, float z, ScreenContext ctx)
    {
        if (this.showBoolean)
        {
            super.renderValueIndicator(x, y, z, ctx);
        }
    }

    protected void renderHotkeysValue(int x, int textY, float z, ScreenContext ctx)
    {
        if (this.keysText != null)
        {
            int tx = x + this.getWidth() - this.keysText.renderWidth;

            if (this.showBoolean && this.booleanValueRenderWidth > 0)
            {
                tx -= this.booleanValueRenderWidth + 4;
            }

            this.renderTextLine(tx, textY, z, this.valueColor, this.valueShadow, this.keysText, ctx);
        }
    }
}
