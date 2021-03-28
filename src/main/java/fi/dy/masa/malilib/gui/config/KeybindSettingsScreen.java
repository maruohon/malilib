package fi.dy.masa.malilib.gui.config;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.ValueChangeCallback;
import fi.dy.masa.malilib.config.option.BaseConfigOption;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.config.liteloader.DialogHandler;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.IntegerTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.SliderWidget;
import fi.dy.masa.malilib.gui.widget.button.BooleanConfigButton;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.button.OptionListConfigButton;
import fi.dy.masa.malilib.input.CancelCondition;
import fi.dy.masa.malilib.input.Context;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.util.StringUtils;

public class KeybindSettingsScreen extends BaseScreen
{
    protected final KeyBind keybind;
    protected final String keybindName;
    protected final OptionListConfig<KeyAction> cfgActivateOn;
    protected final OptionListConfig<Context> cfgContext;
    protected final OptionListConfig<CancelCondition> cfgCancel;
    protected final BooleanConfig cfgAllowEmpty;
    protected final BooleanConfig cfgAllowExtra;
    protected final BooleanConfig cfgOrderSensitive;
    protected final BooleanConfig cfgExclusive;
    protected final BooleanConfig cfgFirstOnly;
    protected final IntegerConfig cfgPriority;
    protected final List<BaseConfigOption<?>> configList;
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

        this.backgroundColor = 0xFF000000;
        this.renderBorder = true;
        this.useTitleHierarchy = false;
        this.title = BaseScreen.TXT_BOLD + StringUtils.translate("malilib.gui.title.keybind_settings.advanced", this.keybindName) + BaseScreen.TXT_RST;

        KeyBindSettings defaultSettings = this.keybind.getDefaultSettings();
        this.cfgActivateOn     = new OptionListConfig<>("", defaultSettings.getActivateOn(), KeyAction.VALUES, "malilib.gui.label.keybind_settings.activate_on",               "malilib.config.comment.keybind_settings.activate_on");
        this.cfgContext        = new OptionListConfig<>("", defaultSettings.getContext(),    Context.VALUES, "malilib.gui.label.keybind_settings.context",                   "malilib.config.comment.keybind_settings.context");
        this.cfgCancel         = new OptionListConfig<>("", defaultSettings.shouldCancel(), CancelCondition.VALUES, "malilib.gui.label.keybind_settings.cancel_further_processing", "malilib.config.comment.keybind_settings.cancel_further");
        this.cfgAllowEmpty     = new BooleanConfig("", defaultSettings.getAllowEmpty(),     "malilib.gui.label.keybind_settings.allow_empty_keybind",       "malilib.config.comment.keybind_settings.allow_empty_keybind");
        this.cfgAllowExtra     = new BooleanConfig("", defaultSettings.getAllowExtraKeys(), "malilib.gui.label.keybind_settings.allow_extra_keys",          "malilib.config.comment.keybind_settings.allow_extra_keys");
        this.cfgOrderSensitive = new BooleanConfig("", defaultSettings.isOrderSensitive(),  "malilib.gui.label.keybind_settings.order_sensitive",           "malilib.config.comment.keybind_settings.order_sensitive");
        this.cfgExclusive      = new BooleanConfig("", defaultSettings.isExclusive(),       "malilib.gui.label.keybind_settings.exclusive",                 "malilib.config.comment.keybind_settings.exclusive");
        this.cfgFirstOnly      = new BooleanConfig("", defaultSettings.getFirstOnly(),      "malilib.gui.label.keybind_settings.first_only",                "malilib.config.comment.keybind_settings.first_only");
        this.cfgPriority       = new IntegerConfig("", defaultSettings.getPriority(), 0, 100, false, "malilib.config.comment.keybind_settings.priority");
        this.cfgPriority.setPrettyNameTranslationKey("malilib.gui.label.keybind_settings.priority");

        KeyBindSettings settings = this.keybind.getSettings();
        this.cfgActivateOn.setValue(settings.getActivateOn());
        this.cfgContext.setValue(settings.getContext());
        this.cfgAllowEmpty.setValue(settings.getAllowEmpty());
        this.cfgAllowExtra.setValue(settings.getAllowExtraKeys());
        this.cfgOrderSensitive.setValue(settings.isOrderSensitive());
        this.cfgExclusive.setValue(settings.isExclusive());
        this.cfgFirstOnly.setValue(settings.getFirstOnly());
        this.cfgCancel.setValue(settings.shouldCancel());
        this.cfgPriority.setValue(settings.getPriority());

        this.cfgActivateOn.setValueChangeCallback((nv, ov) -> this.initGui());
        this.cfgContext.setValueChangeCallback((nv, ov) -> this.initGui());
        ValueChangeCallback<Boolean> cbb = (nv, ov) -> this.initGui();
        this.cfgAllowEmpty.setValueChangeCallback(cbb);
        this.cfgAllowExtra.setValueChangeCallback(cbb);
        this.cfgOrderSensitive.setValueChangeCallback(cbb);
        this.cfgExclusive.setValueChangeCallback(cbb);
        this.cfgFirstOnly.setValueChangeCallback(cbb);
        this.cfgCancel.setValueChangeCallback((nv, ov) -> this.initGui());

        this.configList = ImmutableList.of(this.cfgActivateOn, this.cfgContext, this.cfgAllowEmpty, this.cfgAllowExtra,
                                           this.cfgOrderSensitive, this.cfgExclusive, this.cfgFirstOnly, this.cfgCancel, this.cfgPriority);
        this.labelWidth = this.getMaxDisplayNameLength(this.configList);
        this.configWidth = 100;

        int totalWidth = this.labelWidth + this.configWidth + 30;
        totalWidth = Math.max(totalWidth, this.getStringWidth(this.title) + 20);
        int totalHeight = this.configList.size() * 22 + 30;

        this.setScreenWidthAndHeight(totalWidth, totalHeight);
        this.centerOnScreen();
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        int x = this.x + 10;
        int y = this.y + 24;

        for (BaseConfigOption<?> config : this.configList)
        {
            this.addConfig(x, y, this.labelWidth, this.configWidth, config);
            y += 22;
        }
    }

    public int getMaxDisplayNameLength(List<BaseConfigOption<?>> configs)
    {
        int width = 0;

        for (BaseConfigOption<?> config : configs)
        {
            width = Math.max(width, this.getStringWidth(config.getPrettyName()));
        }

        return width;
    }

    protected void addConfig(int x, int y, int labelWidth, int configWidth, BaseConfigOption<?> config)
    {
        int color = config.isModified() ? 0xFFFFFF55 : 0xFFAAAAAA;
        LabelWidget label = this.addLabel(x, y, labelWidth + 4, 20, color, config.getPrettyName());
        label.addHoverStrings(config.getComment());
        label.getPadding().setTop(5);
        x += labelWidth + 10;

        if (config instanceof BooleanConfig)
        {
            this.addWidget(new BooleanConfigButton(x, y, configWidth, 20, (BooleanConfig) config));
        }
        else if (config instanceof OptionListConfig)
        {
            this.addWidget(new OptionListConfigButton(x, y, configWidth, 20, (OptionListConfig<?>) config));
        }
        else if (config instanceof IntegerConfig)
        {
            IntegerConfig intConfig = (IntegerConfig) config;

            if (intConfig.isSliderActive())
            {
                this.addWidget(new SliderWidget(x, y, 82, 20, intConfig.getSliderCallback(null)));
            }
            else
            {
                BaseTextFieldWidget textField = new BaseTextFieldWidget(x, y, 82, 20);
                textField.setText(intConfig.getStringValue());
                textField.setTextValidator(new IntegerTextFieldWidget.IntValidator(intConfig.getMinIntegerValue(), intConfig.getMaxIntegerValue()));
                textField.setListener(intConfig::setValueFromString);
                this.addWidget(textField);
            }

            GenericButton sliderToggleButton = new GenericButton(x + 84, y + 2, () -> intConfig.isSliderActive() ? DefaultIcons.BTN_TXTFIELD : DefaultIcons.BTN_SLIDER);

            sliderToggleButton.setActionListener((btn, mbtn) -> {
                intConfig.toggleSliderActive();
                this.initGui();
            });

            this.addWidget(sliderToggleButton);
        }
    }

    @Override
    public void onGuiClosed()
    {
        KeyAction activateOn = this.cfgActivateOn.getValue();
        Context context = this.cfgContext.getValue();
        boolean allowEmpty = this.cfgAllowEmpty.getBooleanValue();
        boolean allowExtraKeys = this.cfgAllowExtra.getBooleanValue();
        boolean orderSensitive = this.cfgOrderSensitive.getBooleanValue();
        boolean exclusive = this.cfgExclusive.getBooleanValue();
        boolean firstOnly = this.cfgFirstOnly.getBooleanValue();
        int priority = this.cfgPriority.getIntegerValue();
        CancelCondition cancel = this.cfgCancel.getValue();

        KeyBindSettings settingsNew = KeyBindSettings.create(context, activateOn, allowExtraKeys, orderSensitive,
                                                             exclusive, cancel, allowEmpty, priority, firstOnly);
        this.keybind.setSettings(settingsNew);

        super.onGuiClosed();
    }
}
