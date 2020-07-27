package fi.dy.masa.malilib.gui.config;

import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.option.ConfigBase;
import fi.dy.masa.malilib.config.option.ConfigBoolean;
import fi.dy.masa.malilib.config.option.ConfigOptionList;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiDialogBase;
import fi.dy.masa.malilib.gui.button.ConfigButtonBoolean;
import fi.dy.masa.malilib.gui.button.ConfigButtonOptionList;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.input.IKeyBind;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.config.IValueChangeCallback;
import fi.dy.masa.malilib.util.StringUtils;

public class GuiKeybindSettings extends GuiDialogBase
{
    protected final IKeyBind keybind;
    protected final String keybindName;
    protected final ConfigOptionList<KeyAction> cfgActivateOn;
    protected final ConfigOptionList<KeyBindSettings.Context> cfgContext;
    protected final ConfigBoolean cfgAllowEmpty;
    protected final ConfigBoolean cfgAllowExtra;
    protected final ConfigBoolean cfgOrderSensitive;
    protected final ConfigBoolean cfgExclusive;
    protected final ConfigBoolean cfgCancel;
    protected final List<ConfigBase<?>> configList;
    @Nullable protected final IDialogHandler dialogHandler;
    protected int labelWidth;
    protected int configWidth;

    public GuiKeybindSettings(IKeyBind keybind, String name, @Nullable IDialogHandler dialogHandler, GuiScreen parent)
    {
        this.keybind = keybind;
        this.keybindName = name;
        this.dialogHandler = dialogHandler;

        // When we have a dialog handler, then we are inside the Liteloader config menu.
        // In there we don't want to use the normal "GUI replacement and render parent first" trick.
        // The "dialog handler" stuff is used within the Liteloader config menus,
        // because there we can't change the mc.currentScreen reference to this GUI,
        // because otherwise Liteloader will freak out.
        // So instead we are using a weird wrapper "sub panel" thingy in there, and thus
        // we can NOT try to render the parent GUI here in that case, otherwise it will
        // lead to an infinite recursion loop and a StackOverflowError.
        if (this.dialogHandler == null)
        {
            this.setParent(parent);
        }

        this.title = GuiBase.TXT_BOLD + StringUtils.translate("malilib.gui.title.keybind_settings.advanced", this.keybindName) + GuiBase.TXT_RST;

        KeyBindSettings defaultSettings = this.keybind.getDefaultSettings();
        this.cfgActivateOn     = new ConfigOptionList<>("malilib.gui.label.keybind_settings.activate_on", defaultSettings.getActivateOn(), "malilib.config.comment.keybind_settings.activate_on");
        this.cfgContext        = new ConfigOptionList<>("malilib.gui.label.keybind_settings.context", defaultSettings.getContext(), "malilib.config.comment.keybind_settings.context");
        this.cfgAllowEmpty     = new ConfigBoolean("malilib.gui.label.keybind_settings.allow_empty_keybind", defaultSettings.getAllowEmpty(), "malilib.config.comment.keybind_settings.allow_empty_keybind");
        this.cfgAllowExtra     = new ConfigBoolean("malilib.gui.label.keybind_settings.allow_extra_keys", defaultSettings.getAllowExtraKeys(), "malilib.config.comment.keybind_settings.allow_extra_keys");
        this.cfgOrderSensitive = new ConfigBoolean("malilib.gui.label.keybind_settings.order_sensitive", defaultSettings.isOrderSensitive(), "malilib.config.comment.keybind_settings.order_sensitive");
        this.cfgExclusive      = new ConfigBoolean("malilib.gui.label.keybind_settings.exclusive", defaultSettings.isExclusive(), "malilib.config.comment.keybind_settings.exclusive");
        this.cfgCancel         = new ConfigBoolean("malilib.gui.label.keybind_settings.cancel_further_processing", defaultSettings.shouldCancel(), "malilib.config.comment.keybind_settings.cancel_further");

        KeyBindSettings settings = this.keybind.getSettings();
        this.cfgActivateOn.setOptionListValue(settings.getActivateOn());
        this.cfgContext.setOptionListValue(settings.getContext());
        this.cfgAllowEmpty.setBooleanValue(settings.getAllowEmpty());
        this.cfgAllowExtra.setBooleanValue(settings.getAllowExtraKeys());
        this.cfgOrderSensitive.setBooleanValue(settings.isOrderSensitive());
        this.cfgExclusive.setBooleanValue(settings.isExclusive());
        this.cfgCancel.setBooleanValue(settings.shouldCancel());

        this.cfgActivateOn.setValueChangeCallback((nv, ov) -> this.initGui());
        this.cfgContext.setValueChangeCallback((nv, ov) -> this.initGui());
        IValueChangeCallback<Boolean> cbb = (nv, ov) -> this.initGui();
        this.cfgAllowEmpty.setValueChangeCallback(cbb);
        this.cfgAllowExtra.setValueChangeCallback(cbb);
        this.cfgOrderSensitive.setValueChangeCallback(cbb);
        this.cfgExclusive.setValueChangeCallback(cbb);
        this.cfgCancel.setValueChangeCallback(cbb);

        this.configList = ImmutableList.of(this.cfgActivateOn, this.cfgContext, this.cfgAllowEmpty, this.cfgAllowExtra, this.cfgOrderSensitive, this.cfgExclusive, this.cfgCancel);
        this.labelWidth = this.getMaxPrettyNameLength(this.configList);
        this.configWidth = 100;

        int totalWidth = this.labelWidth + this.configWidth + 30;
        totalWidth = Math.max(totalWidth, this.getStringWidth(this.title) + 20);

        this.setWidthAndHeight(totalWidth, this.configList.size() * 22 + 30);
        this.centerOnScreen();

        this.setWorldAndResolution(this.mc, this.dialogWidth, this.dialogHeight);
    }

    @Override
    public void initGui()
    {
        this.clearElements();

        int x = this.dialogLeft + 10;
        int y = this.dialogTop + 24;

        for (ConfigBase<?> config : this.configList)
        {
            this.addConfig(x, y, this.labelWidth, this.configWidth, config);
            y += 22;
        }
    }

    protected void addConfig(int x, int y, int labelWidth, int configWidth, ConfigBase<?> config)
    {
        int color = config.isModified() ? 0xFFFFFF55 : 0xFFAAAAAA;
        this.addLabel(x, y, labelWidth, 20, color, StringUtils.translate(config.getPrettyName()))
            .setPaddingY(5).addHoverStrings(config.getComment());
        x += labelWidth + 10;

        if (config instanceof ConfigBoolean)
        {
            this.addWidget(new ConfigButtonBoolean(x, y, configWidth, 20, (ConfigBoolean) config));
        }
        else if (config instanceof ConfigOptionList)
        {
            this.addWidget(new ConfigButtonOptionList(x, y, configWidth, 20, (ConfigOptionList<?>) config));
        }
    }

    @Override
    public void onGuiClosed()
    {
        KeyAction activateOn = this.cfgActivateOn.getOptionListValue();
        KeyBindSettings.Context context = this.cfgContext.getOptionListValue();
        boolean allowEmpty = this.cfgAllowEmpty.getBooleanValue();
        boolean allowExtraKeys = this.cfgAllowExtra.getBooleanValue();
        boolean orderSensitive = this.cfgOrderSensitive.getBooleanValue();
        boolean exclusive = this.cfgExclusive.getBooleanValue();
        boolean cancel = this.cfgCancel.getBooleanValue();

        KeyBindSettings settingsNew = KeyBindSettings.create(context, activateOn, allowExtraKeys, orderSensitive, exclusive, cancel, allowEmpty);
        this.keybind.setSettings(settingsNew);

        super.onGuiClosed();
    }

    @Override
    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_ESCAPE && this.dialogHandler != null)
        {
            this.dialogHandler.closeDialog();
            return true;
        }
        else
        {
            return super.onKeyTyped(typedChar, keyCode);
        }
    }
}
