package fi.dy.masa.malilib.overlay.widget.sub;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.indicator.HotkeyedBooleanConfigStatusIndicatorEditScreen;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class HotkeyedBooleanConfigStatusWidget extends BooleanConfigStatusWidget
{
    protected final Supplier<KeyBind> keyBindSupplier;
    protected List<Integer> lastKeys = Collections.emptyList();
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
        HotkeyedBooleanConfigStatusIndicatorEditScreen screen = new HotkeyedBooleanConfigStatusIndicatorEditScreen(this, GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(screen);
    }

    @Override
    protected boolean isModified()
    {
        return this.lastValue != this.config.getBooleanValue() ||
               this.keyBindSupplier.get().getKeys().equals(this.lastKeys) == false;
    }

    @Override
    protected void updateValue()
    {
        super.updateValue();

        this.lastKeys = this.keyBindSupplier.get().getKeys();
        this.keysText = null;

        if (this.showKeys)
        {
            String keysString = this.keyBindSupplier.get().getKeysDisplayString();
            this.keysText = StyledTextLine.translate("malilib.label.config_status_indicator.hotkeys_string", keysString);
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

            this.renderTextLine(tx, textY, z, this.valueColor, this.valueShadow, ctx, this.keysText);
        }
    }
}
