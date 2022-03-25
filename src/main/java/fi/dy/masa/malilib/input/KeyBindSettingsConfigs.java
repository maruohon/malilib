package fi.dy.masa.malilib.input;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.ValueChangeCallback;
import fi.dy.masa.malilib.config.option.BaseConfigOption;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.config.option.IntegerConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.overlay.message.MessageOutput;

public class KeyBindSettingsConfigs
{
    protected final OptionListConfig<KeyAction> cfgActivateOn;
    protected final OptionListConfig<Context> cfgContext;
    protected final OptionListConfig<CancelCondition> cfgCancel;
    protected final OptionListConfig<MessageOutput> cfgMessageType;
    protected final BooleanConfig cfgAllowEmpty;
    protected final BooleanConfig cfgAllowExtra;
    protected final BooleanConfig cfgOrderSensitive;
    protected final BooleanConfig cfgExclusive;
    protected final BooleanConfig cfgFirstOnly;
    protected final IntegerConfig cfgPriority;
    protected final BooleanConfig cfgShowToast;
    protected final ImmutableList<BaseConfigOption<?>> configList;
    protected final EventListener changeListener;

    public KeyBindSettingsConfigs(KeyBind keybind, EventListener changeListener)
    {
        this.changeListener = changeListener;

        KeyBindSettings defaultSettings = keybind.getDefaultSettings();
        this.cfgActivateOn     = new OptionListConfig<>("", defaultSettings.getActivateOn(), KeyAction.VALUES, "malilib.label.keybind_settings.activate_on",    "malilib.config.comment.keybind_settings.activate_on");
        this.cfgContext        = new OptionListConfig<>("", defaultSettings.getContext(),    Context.VALUES, "malilib.label.keybind_settings.context",          "malilib.config.comment.keybind_settings.context");
        this.cfgCancel         = new OptionListConfig<>("", defaultSettings.getCancelCondition(), CancelCondition.VALUES, "malilib.label.keybind_settings.cancel_further",  "malilib.config.comment.keybind_settings.cancel_further");
        this.cfgMessageType    = new OptionListConfig<>("", defaultSettings.getMessageType(), MessageOutput.getValues(), "malilib.label.keybind_settings.message_output", "malilib.config.comment.keybind_settings.message_output");
        this.cfgAllowEmpty     = new BooleanConfig("", defaultSettings.getAllowEmpty(),     "malilib.label.keybind_settings.allow_empty_keybind",       "malilib.config.comment.keybind_settings.allow_empty_keybind");
        this.cfgAllowExtra     = new BooleanConfig("", defaultSettings.getAllowExtraKeys(), "malilib.label.keybind_settings.allow_extra_keys",          "malilib.config.comment.keybind_settings.allow_extra_keys");
        this.cfgOrderSensitive = new BooleanConfig("", defaultSettings.isOrderSensitive(),  "malilib.label.keybind_settings.order_sensitive",           "malilib.config.comment.keybind_settings.order_sensitive");
        this.cfgExclusive      = new BooleanConfig("", defaultSettings.isExclusive(),       "malilib.label.keybind_settings.exclusive",                 "malilib.config.comment.keybind_settings.exclusive");
        this.cfgFirstOnly      = new BooleanConfig("", defaultSettings.getFirstOnly(),      "malilib.label.keybind_settings.first_only",                "malilib.config.comment.keybind_settings.first_only");
        this.cfgPriority       = new IntegerConfig("", defaultSettings.getPriority(), 0, 100, false, "malilib.config.comment.keybind_settings.priority");
        this.cfgShowToast      = new BooleanConfig("", defaultSettings.getShowToast(),      "malilib.label.keybind_settings.show_toast",                "malilib.config.comment.keybind_settings.show_toast");
        this.cfgPriority.setPrettyNameTranslationKey("malilib.label.keybind_settings.priority");

        KeyBindSettings settings = keybind.getSettings();
        this.cfgActivateOn.setValue(settings.getActivateOn());
        this.cfgAllowEmpty.setValue(settings.getAllowEmpty());
        this.cfgAllowExtra.setValue(settings.getAllowExtraKeys());
        this.cfgCancel.setValue(settings.getCancelCondition());
        this.cfgContext.setValue(settings.getContext());
        this.cfgExclusive.setValue(settings.isExclusive());
        this.cfgFirstOnly.setValue(settings.getFirstOnly());
        this.cfgOrderSensitive.setValue(settings.isOrderSensitive());
        this.cfgMessageType.setValue(settings.getMessageType());
        this.cfgPriority.setValue(settings.getPriority());
        this.cfgShowToast.setValue(settings.getShowToast());

        this.cfgActivateOn.setValueChangeCallback((nv, ov) -> this.onValueChanged());
        this.cfgContext.setValueChangeCallback((nv, ov) -> this.onValueChanged());
        this.cfgMessageType.setValueChangeCallback((nv, ov) -> this.onValueChanged());
        ValueChangeCallback<Boolean> cbb = (nv, ov) -> this.onValueChanged();
        this.cfgAllowEmpty.setValueChangeCallback(cbb);
        this.cfgAllowExtra.setValueChangeCallback(cbb);
        this.cfgOrderSensitive.setValueChangeCallback(cbb);
        this.cfgExclusive.setValueChangeCallback(cbb);
        this.cfgFirstOnly.setValueChangeCallback(cbb);
        this.cfgShowToast.setValueChangeCallback(cbb);
        this.cfgCancel.setValueChangeCallback((nv, ov) -> this.onValueChanged());

        this.configList = ImmutableList.of(this.cfgActivateOn,
                                           this.cfgContext,
                                           this.cfgCancel,
                                           this.cfgAllowEmpty,
                                           this.cfgAllowExtra,
                                           this.cfgOrderSensitive,
                                           this.cfgExclusive,
                                           this.cfgFirstOnly,
                                           this.cfgPriority,
                                           this.cfgMessageType,
                                           this.cfgShowToast);
    }

    protected void onValueChanged()
    {
        this.changeListener.onEvent();
    }

    public ImmutableList<BaseConfigOption<?>> getConfigList()
    {
        return this.configList;
    }

    public KeyBindSettings getCurrentSettings()
    {
        return KeyBindSettings.builder()
                .context(this.cfgContext.getValue())
                .activateOn(this.cfgActivateOn.getValue())
                .allowExtraKeys(this.cfgAllowExtra.getBooleanValue())
                .orderSensitive(this.cfgOrderSensitive.getBooleanValue())
                .cancel(this.cfgCancel.getValue())
                .exclusive(this.cfgExclusive.getBooleanValue())
                .firstOnly(this.cfgFirstOnly.getBooleanValue())
                .priority(this.cfgPriority.getIntegerValue())
                .allowEmpty(this.cfgAllowEmpty.getBooleanValue())
                .showToast(this.cfgShowToast.getBooleanValue())
                .messageOutput(this.cfgMessageType.getValue())
                .build();
    }
}
