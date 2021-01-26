package fi.dy.masa.malilib.gui.config;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.ValueChangeCallback;
import fi.dy.masa.malilib.config.option.BaseConfig;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.gui.BaseDialogScreen;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.util.DialogHandler;
import fi.dy.masa.malilib.gui.widget.button.BooleanConfigButton;
import fi.dy.masa.malilib.gui.widget.button.OptionListConfigButton;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.util.StringUtils;

public class KeybindSettingsScreen extends BaseDialogScreen
{
    protected final KeyBind keybind;
    protected final String keybindName;
    protected final OptionListConfig<KeyAction> cfgActivateOn;
    protected final OptionListConfig<KeyBindSettings.Context> cfgContext;
    protected final BooleanConfig cfgAllowEmpty;
    protected final BooleanConfig cfgAllowExtra;
    protected final BooleanConfig cfgOrderSensitive;
    protected final BooleanConfig cfgExclusive;
    protected final BooleanConfig cfgCancel;
    protected final List<BaseConfig<?>> configList;
    protected int labelWidth;
    protected int configWidth;

    public KeybindSettingsScreen(KeyBind keybind, String name, @Nullable DialogHandler dialogHandler, GuiScreen parent)
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

        this.title = BaseScreen.TXT_BOLD + StringUtils.translate("malilib.gui.title.keybind_settings.advanced", this.keybindName) + BaseScreen.TXT_RST;

        KeyBindSettings defaultSettings = this.keybind.getDefaultSettings();
        this.cfgActivateOn     = new OptionListConfig<>("", defaultSettings.getActivateOn(),"malilib.gui.label.keybind_settings.activate_on",               "malilib.config.comment.keybind_settings.activate_on");
        this.cfgContext        = new OptionListConfig<>("", defaultSettings.getContext(),   "malilib.gui.label.keybind_settings.context",                   "malilib.config.comment.keybind_settings.context");
        this.cfgAllowEmpty     = new BooleanConfig("", defaultSettings.getAllowEmpty(),     "malilib.gui.label.keybind_settings.allow_empty_keybind",       "malilib.config.comment.keybind_settings.allow_empty_keybind");
        this.cfgAllowExtra     = new BooleanConfig("", defaultSettings.getAllowExtraKeys(), "malilib.gui.label.keybind_settings.allow_extra_keys",          "malilib.config.comment.keybind_settings.allow_extra_keys");
        this.cfgOrderSensitive = new BooleanConfig("", defaultSettings.isOrderSensitive(),  "malilib.gui.label.keybind_settings.order_sensitive",           "malilib.config.comment.keybind_settings.order_sensitive");
        this.cfgExclusive      = new BooleanConfig("", defaultSettings.isExclusive(),       "malilib.gui.label.keybind_settings.exclusive",                 "malilib.config.comment.keybind_settings.exclusive");
        this.cfgCancel         = new BooleanConfig("", defaultSettings.shouldCancel(),      "malilib.gui.label.keybind_settings.cancel_further_processing", "malilib.config.comment.keybind_settings.cancel_further");

        KeyBindSettings settings = this.keybind.getSettings();
        this.cfgActivateOn.setValue(settings.getActivateOn());
        this.cfgContext.setValue(settings.getContext());
        this.cfgAllowEmpty.setValue(settings.getAllowEmpty());
        this.cfgAllowExtra.setValue(settings.getAllowExtraKeys());
        this.cfgOrderSensitive.setValue(settings.isOrderSensitive());
        this.cfgExclusive.setValue(settings.isExclusive());
        this.cfgCancel.setValue(settings.shouldCancel());

        this.cfgActivateOn.setValueChangeCallback((nv, ov) -> this.initGui());
        this.cfgContext.setValueChangeCallback((nv, ov) -> this.initGui());
        ValueChangeCallback<Boolean> cbb = (nv, ov) -> this.initGui();
        this.cfgAllowEmpty.setValueChangeCallback(cbb);
        this.cfgAllowExtra.setValueChangeCallback(cbb);
        this.cfgOrderSensitive.setValueChangeCallback(cbb);
        this.cfgExclusive.setValueChangeCallback(cbb);
        this.cfgCancel.setValueChangeCallback(cbb);

        this.configList = ImmutableList.of(this.cfgActivateOn, this.cfgContext, this.cfgAllowEmpty, this.cfgAllowExtra, this.cfgOrderSensitive, this.cfgExclusive, this.cfgCancel);
        this.labelWidth = this.getMaxDisplayNameLength(this.configList);
        this.configWidth = 100;

        int totalWidth = this.labelWidth + this.configWidth + 30;
        totalWidth = Math.max(totalWidth, this.getStringWidth(this.title) + 20);
        int totalHeight = this.configList.size() * 22 + 30;

        this.setWidthAndHeight(totalWidth, totalHeight);
        this.setScreenWidthAndHeight(totalWidth, totalHeight);
        this.centerOnScreen();
    }

    @Override
    public void initGui()
    {
        this.clearElements();

        int x = this.dialogLeft + 10;
        int y = this.dialogTop + 24;

        for (BaseConfig<?> config : this.configList)
        {
            this.addConfig(x, y, this.labelWidth, this.configWidth, config);
            y += 22;
        }
    }

    public int getMaxDisplayNameLength(List<BaseConfig<?>> configs)
    {
        int width = 0;

        for (BaseConfig<?> config : configs)
        {
            width = Math.max(width, this.getStringWidth(config.getPrettyName()));
        }

        return width;
    }

    protected void addConfig(int x, int y, int labelWidth, int configWidth, BaseConfig<?> config)
    {
        int color = config.isModified() ? 0xFFFFFF55 : 0xFFAAAAAA;
        this.addLabel(x, y, labelWidth, 20, color, config.getPrettyName()).setPaddingTop(5).addHoverStrings(config.getComment());
        x += labelWidth + 10;

        if (config instanceof BooleanConfig)
        {
            this.addWidget(new BooleanConfigButton(x, y, configWidth, 20, (BooleanConfig) config));
        }
        else if (config instanceof OptionListConfig)
        {
            this.addWidget(new OptionListConfigButton(x, y, configWidth, 20, (OptionListConfig<?>) config));
        }
    }

    @Override
    public void onGuiClosed()
    {
        KeyAction activateOn = this.cfgActivateOn.getValue();
        KeyBindSettings.Context context = this.cfgContext.getValue();
        boolean allowEmpty = this.cfgAllowEmpty.getBooleanValue();
        boolean allowExtraKeys = this.cfgAllowExtra.getBooleanValue();
        boolean orderSensitive = this.cfgOrderSensitive.getBooleanValue();
        boolean exclusive = this.cfgExclusive.getBooleanValue();
        boolean cancel = this.cfgCancel.getBooleanValue();

        KeyBindSettings settingsNew = KeyBindSettings.create(context, activateOn, allowExtraKeys, orderSensitive, exclusive, cancel, allowEmpty);
        this.keybind.setSettings(settingsNew);

        super.onGuiClosed();
    }
}
