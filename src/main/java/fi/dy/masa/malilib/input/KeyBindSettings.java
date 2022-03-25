package fi.dy.masa.malilib.input;

import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.value.BaseOptionListConfigValue;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import fi.dy.masa.malilib.util.JsonUtils;

public class KeyBindSettings
{
    public static final KeyBindSettings INGAME_DEFAULT              = new KeyBindSettings(Context.INGAME, KeyAction.PRESS, true, true, CancelCondition.ALWAYS);
    public static final KeyBindSettings INGAME_SUCCESS              = new KeyBindSettings(Context.INGAME, KeyAction.PRESS, true, true, CancelCondition.ON_SUCCESS);
    public static final KeyBindSettings INGAME_BOTH                 = new KeyBindSettings(Context.INGAME, KeyAction.BOTH, true, true, CancelCondition.ALWAYS);
    public static final KeyBindSettings INGAME_MODIFIER             = builderInGameModifier().build();
    public static final KeyBindSettings INGAME_MODIFIER_EMPTY       = builderInGameModifier().empty().build();
    public static final KeyBindSettings INGAME_MODIFIER_BOTH        = new KeyBindSettings(Context.INGAME, KeyAction.BOTH, true, false, CancelCondition.NEVER);
    public static final KeyBindSettings INGAME_RELEASE              = new KeyBindSettings(Context.INGAME, KeyAction.RELEASE, true, true, CancelCondition.NEVER);
    public static final KeyBindSettings INGAME_RELEASE_EXCLUSIVE    = builder().release().extra().order().exclusive().cancel(CancelCondition.NEVER).build();
    public static final KeyBindSettings GUI_DEFAULT                 = new KeyBindSettings(Context.GUI, KeyAction.PRESS, true, true, CancelCondition.ALWAYS);
    public static final KeyBindSettings GUI_MODIFIER                = new KeyBindSettings(Context.GUI, KeyAction.PRESS, true, false, CancelCondition.NEVER);

    protected final KeyAction activateOn;
    protected final Context context;
    protected final CancelCondition cancel;
    protected final MessageOutput messageOutput;
    protected final boolean allowEmpty;
    protected final boolean allowExtraKeys;
    protected final boolean exclusive;
    protected final boolean firstOnly;
    protected final boolean orderSensitive;
    protected final boolean showToast;
    protected final int priority;

    public KeyBindSettings(Context context, KeyAction activateOn,
                           boolean allowExtraKeys, boolean orderSensitive,
                           CancelCondition cancel)
    {
        this(context, activateOn, allowExtraKeys, orderSensitive, cancel,
             false, false, 50, false, true, MessageOutput.CUSTOM_HOTBAR);
    }

    protected KeyBindSettings(Context context, KeyAction activateOn,
                              boolean allowExtraKeys, boolean orderSensitive,
                              CancelCondition cancel,
                              boolean exclusive, boolean firstOnly, int priority,
                              boolean allowEmpty, boolean showToast, MessageOutput messageOutput)
    {
        this.context = context;
        this.activateOn = activateOn;
        this.allowExtraKeys = allowExtraKeys;
        this.orderSensitive = orderSensitive;
        this.exclusive = exclusive;
        this.cancel = cancel;
        this.allowEmpty = allowEmpty;
        this.priority = priority;
        this.firstOnly = firstOnly;
        this.showToast = showToast;
        this.messageOutput = messageOutput;
    }

    public Context getContext()
    {
        return this.context;
    }

    public KeyAction getActivateOn()
    {
        return this.activateOn;
    }

    public CancelCondition getCancelCondition()
    {
        return this.cancel;
    }

    public boolean getAllowEmpty()
    {
        return this.allowEmpty;
    }

    public boolean getAllowExtraKeys()
    {
        return this.allowExtraKeys;
    }

    public boolean isOrderSensitive()
    {
        return this.orderSensitive;
    }

    public boolean isExclusive()
    {
        return this.exclusive;
    }

    public boolean getFirstOnly()
    {
        return this.firstOnly;
    }

    public int getPriority()
    {
        return this.priority;
    }

    public boolean getShowToast()
    {
        return this.showToast;
    }

    public MessageOutput getMessageType()
    {
        return this.messageOutput;
    }

    public Builder asBuilder()
    {
        return new Builder(this.context, this.activateOn, this.allowExtraKeys,
                           this.orderSensitive, this.exclusive, this.cancel,
                           this.allowEmpty, this.priority, this.firstOnly,
                           this.showToast, this.messageOutput);
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("activate_on", this.activateOn.getName());
        obj.addProperty("allow_empty", this.allowEmpty);
        obj.addProperty("allow_extra_keys", this.allowExtraKeys);
        obj.addProperty("cancel", this.cancel.getName());
        obj.addProperty("context", this.context.getName());
        obj.addProperty("exclusive", this.exclusive);
        obj.addProperty("first_only", this.firstOnly);
        obj.addProperty("order_sensitive", this.orderSensitive);
        obj.addProperty("priority", this.priority);
        obj.addProperty("show_toast", this.showToast);
        obj.addProperty("message_output", this.messageOutput.getName());

        return obj;
    }

    public static KeyBindSettings fromJson(JsonObject obj)
    {
        Context context = Context.INGAME;
        KeyAction activateOn = KeyAction.PRESS;
        MessageOutput messageOutput = MessageOutput.CUSTOM_HOTBAR;
        String contextStr = JsonUtils.getString(obj, "context");
        String activateStr = JsonUtils.getString(obj, "activate_on");
        String messageTypeStr = JsonUtils.getString(obj, "message_output");

        if (contextStr != null)
        {
            context = BaseOptionListConfigValue.findValueByName(contextStr, Context.VALUES);
        }

        if (activateStr != null)
        {
            activateOn = BaseOptionListConfigValue.findValueByName(activateStr, KeyAction.VALUES);
        }

        if (messageTypeStr != null)
        {
            messageOutput = BaseOptionListConfigValue.findValueByName(messageTypeStr, MessageOutput.getValues());
        }

        String cancelName = JsonUtils.getStringOrDefault(obj, "cancel", "false");
        CancelCondition cancel;

        // Backwards compatibility with the old boolean value
        if (cancelName.equalsIgnoreCase("true"))
        {
            cancel = CancelCondition.ALWAYS;
        }
        else if (cancelName.equalsIgnoreCase("false"))
        {
            cancel = CancelCondition.NEVER;
        }
        else
        {
            cancel = BaseOptionListConfigValue.findValueByName(cancelName, CancelCondition.VALUES);
        }

        return builder()
                .context(context)
                .activateOn(activateOn)
                .cancel(cancel)
                .messageOutput(messageOutput)
                .allowExtraKeys(JsonUtils.getBoolean(obj, "allow_extra_keys"))
                .orderSensitive(JsonUtils.getBooleanOrDefault(obj, "order_sensitive", true))
                .exclusive(JsonUtils.getBoolean(obj, "exclusive"))
                .firstOnly(JsonUtils.getBoolean(obj, "first_only"))
                .priority(JsonUtils.getIntegerOrDefault(obj, "priority", 50))
                .allowEmpty(JsonUtils.getBoolean(obj, "allow_empty"))
                .showToast(JsonUtils.getBooleanOrDefault(obj, "show_toast", true))
                .build();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) { return true; }
        if (obj == null || this.getClass() != obj.getClass()) { return false; }

        KeyBindSettings other = (KeyBindSettings) obj;
        return this.activateOn == other.activateOn &&
               this.context == other.context &&
               this.allowEmpty == other.allowEmpty &&
               this.allowExtraKeys == other.allowExtraKeys &&
               this.cancel == other.cancel &&
               this.exclusive == other.exclusive &&
               this.firstOnly == other.firstOnly &&
               this.priority == other.priority &&
               this.showToast == other.showToast &&
               this.messageOutput == other.messageOutput &&
               this.orderSensitive == other.orderSensitive;
    }

    public static class Builder
    {
        protected KeyAction activateOn = KeyAction.PRESS;
        protected Context context = Context.INGAME;
        protected CancelCondition cancel = CancelCondition.ON_SUCCESS;
        protected MessageOutput messageOutput = MessageOutput.CUSTOM_HOTBAR;
        protected boolean allowEmpty;
        protected boolean allowExtraKeys = true;
        protected boolean exclusive;
        protected boolean firstOnly;
        protected boolean orderSensitive = true;
        protected boolean showToast;
        protected int priority = 50;

        public Builder()
        {
        }

        public Builder(Context context, KeyAction activateOn,
                       boolean allowExtraKeys, boolean orderSensitive, boolean exclusive,
                       CancelCondition cancel, boolean allowEmpty,
                       int priority, boolean firstOnly, boolean showToast, MessageOutput messageOutput)
        {
            this.context = context;
            this.activateOn = activateOn;
            this.allowExtraKeys = allowExtraKeys;
            this.orderSensitive = orderSensitive;
            this.exclusive = exclusive;
            this.cancel = cancel;
            this.allowEmpty = allowEmpty;
            this.priority = priority;
            this.firstOnly = firstOnly;
            this.showToast = showToast;
            this.messageOutput = messageOutput;
        }

        public Builder activateOn(KeyAction activateOn)
        {
            this.activateOn = activateOn;
            return this;
        }

        public Builder context(Context context)
        {
            this.context = context;
            return this;
        }

        public Builder cancel(CancelCondition cancel)
        {
            this.cancel = cancel;
            return this;
        }

        public Builder messageOutput(MessageOutput messageOutput)
        {
            this.messageOutput = messageOutput;
            return this;
        }

        public Builder allowEmpty(boolean allowEmpty)
        {
            this.allowEmpty = allowEmpty;
            return this;
        }

        public Builder allowExtraKeys(boolean allowExtraKeys)
        {
            this.allowExtraKeys = allowExtraKeys;
            return this;
        }

        public Builder exclusive(boolean exclusive)
        {
            this.exclusive = exclusive;
            return this;
        }

        public Builder firstOnly(boolean firstOnly)
        {
            this.firstOnly = firstOnly;
            return this;
        }

        public Builder orderSensitive(boolean orderSensitive)
        {
            this.orderSensitive = orderSensitive;
            return this;
        }

        public Builder showToast(boolean showToast)
        {
            this.showToast = showToast;
            return this;
        }

        public Builder priority(int priority)
        {
            this.priority = priority;
            return this;
        }

        public Builder press()
        {
            this.activateOn = KeyAction.PRESS;
            return this;
        }

        public Builder release()
        {
            this.activateOn = KeyAction.RELEASE;
            return this;
        }

        public Builder order()
        {
            this.orderSensitive = true;
            return this;
        }

        public Builder empty()
        {
            this.allowEmpty = true;
            return this;
        }

        public Builder extra()
        {
            this.allowExtraKeys = true;
            return this;
        }

        public Builder exclusive()
        {
            this.exclusive = true;
            return this;
        }

        public KeyBindSettings build()
        {
            return new KeyBindSettings(this.context, this.activateOn,
                                       this.allowExtraKeys, this.orderSensitive,
                                       this.cancel,
                                       this.exclusive, this.firstOnly, this.priority, this.allowEmpty,
                                       this.showToast, this.messageOutput);
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static Builder builderInGameModifier()
    {
        Builder builder = new Builder();
        builder.allowExtraKeys(true)
               .orderSensitive(false)
               .cancel(CancelCondition.NEVER)
               .messageOutput(MessageOutput.NONE);
        return builder;
    }

    public static Builder builderGuiModifier()
    {
        return builderInGameModifier().context(Context.GUI);
    }
}
