package fi.dy.masa.malilib.input;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.value.BaseOptionListConfigValue;
import fi.dy.masa.malilib.util.JsonUtils;

public class KeyBindSettings
{
    public static final KeyBindSettings DEFAULT                     = new KeyBindSettings(Context.INGAME, KeyAction.PRESS, false, true, false, true);
    public static final KeyBindSettings EXCLUSIVE                   = new KeyBindSettings(Context.INGAME, KeyAction.PRESS, false, true, true, true);
    public static final KeyBindSettings RELEASE                     = new KeyBindSettings(Context.INGAME, KeyAction.RELEASE, false, true, false, false);
    public static final KeyBindSettings RELEASE_ALLOW_EXTRA         = new KeyBindSettings(Context.INGAME, KeyAction.RELEASE, true, true, false, false);
    public static final KeyBindSettings RELEASE_EXCLUSIVE           = new KeyBindSettings(Context.INGAME, KeyAction.RELEASE, false, true, true, true);
    public static final KeyBindSettings NOCANCEL                    = new KeyBindSettings(Context.INGAME, KeyAction.PRESS, false, true, false, false);
    public static final KeyBindSettings PRESS_ALLOWEXTRA            = new KeyBindSettings(Context.INGAME, KeyAction.PRESS, true, true, false, true);
    public static final KeyBindSettings PRESS_ALLOWEXTRA_EMPTY      = new KeyBindSettings(Context.INGAME, KeyAction.PRESS, true, true, false, true, true);
    public static final KeyBindSettings PRESS_NON_ORDER_SENSITIVE   = new KeyBindSettings(Context.INGAME, KeyAction.PRESS, false, false, false, true);
    public static final KeyBindSettings INGAME_BOTH                 = new KeyBindSettings(Context.INGAME, KeyAction.BOTH, false, true, false, true);
    public static final KeyBindSettings MODIFIER_INGAME             = new KeyBindSettings(Context.INGAME, KeyAction.PRESS, true, false, false, false);
    public static final KeyBindSettings MODIFIER_INGAME_EMPTY       = new KeyBindSettings(Context.INGAME, KeyAction.PRESS, true, false, false, false, true);
    public static final KeyBindSettings MODIFIER_GUI                = new KeyBindSettings(Context.GUI, KeyAction.PRESS, true, false, false, false);
    public static final KeyBindSettings GUI                         = new KeyBindSettings(Context.GUI, KeyAction.PRESS, false, true, false, true);

    private final Context context;
    private final KeyAction activateOn;
    private final boolean allowEmpty;
    private final boolean allowExtraKeys;
    private final boolean orderSensitive;
    private final boolean exclusive;
    private final boolean cancel;

    private KeyBindSettings(Context context, KeyAction activateOn, boolean allowExtraKeys, boolean orderSensitive, boolean exclusive, boolean cancel)
    {
        this(context, activateOn, allowExtraKeys, orderSensitive, exclusive, cancel, false);
    }

    private KeyBindSettings(Context context, KeyAction activateOn, boolean allowExtraKeys, boolean orderSensitive, boolean exclusive, boolean cancel, boolean allowEmpty)
    {
        this.context = context;
        this.activateOn = activateOn;
        this.allowExtraKeys = allowExtraKeys;
        this.orderSensitive = orderSensitive;
        this.exclusive = exclusive;
        this.cancel = cancel;
        this.allowEmpty = allowEmpty;
    }

    public static KeyBindSettings create(Context context, KeyAction activateOn, boolean allowExtraKeys, boolean orderSensitive, boolean exclusive, boolean cancel)
    {
        return create(context, activateOn, allowExtraKeys, orderSensitive, exclusive, cancel, false);
    }

    public static KeyBindSettings create(Context context, KeyAction activateOn, boolean allowExtraKeys, boolean orderSensitive, boolean exclusive, boolean cancel, boolean allowEmpty)
    {
        return new KeyBindSettings(context, activateOn, allowExtraKeys, orderSensitive, exclusive, cancel, allowEmpty);
    }

    public Context getContext()
    {
        return this.context;
    }

    public KeyAction getActivateOn()
    {
        return this.activateOn;
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

    public boolean shouldCancel()
    {
        return this.cancel;
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("activate_on", this.activateOn.getName());
        obj.addProperty("context", this.context.getName());
        obj.addProperty("allow_empty", this.allowEmpty);
        obj.addProperty("allow_extra_keys", this.allowExtraKeys);
        obj.addProperty("order_sensitive", this.orderSensitive);
        obj.addProperty("exclusive", this.exclusive);
        obj.addProperty("cancel", this.cancel);

        return obj;
    }

    public static KeyBindSettings fromJson(JsonObject obj)
    {
        Context context = Context.INGAME;
        KeyAction activateOn = KeyAction.PRESS;
        String contextStr = JsonUtils.getString(obj, "context");
        String activateStr = JsonUtils.getString(obj, "activate_on");

        if (contextStr != null)
        {
            context = BaseOptionListConfigValue.findValueByName(contextStr, Context.VALUES);
        }

        if (activateStr != null)
        {
            activateOn = BaseOptionListConfigValue.findValueByName(activateStr, KeyAction.VALUES);
        }

        boolean allowEmpty = JsonUtils.getBoolean(obj, "allow_empty");
        boolean allowExtraKeys = JsonUtils.getBoolean(obj, "allow_extra_keys");
        boolean orderSensitive = JsonUtils.getBooleanOrDefault(obj, "order_sensitive", true);
        boolean exclusive = JsonUtils.getBooleanOrDefault(obj, "exclusive", true);
        boolean cancel = JsonUtils.getBooleanOrDefault(obj, "cancel", true);

        return create(context, activateOn, allowExtraKeys, orderSensitive, exclusive, cancel, allowEmpty);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        KeyBindSettings other = (KeyBindSettings) obj;
        if (this.activateOn != other.activateOn)
            return false;
        if (this.context != other.context)
            return false;
        if (this.allowEmpty != other.allowEmpty)
            return false;
        if (this.allowExtraKeys != other.allowExtraKeys)
            return false;
        if (this.cancel != other.cancel)
            return false;
        if (this.exclusive != other.exclusive)
            return false;
        return this.orderSensitive == other.orderSensitive;
    }

    public static class Context extends BaseOptionListConfigValue
    {
        public static final Context INGAME = new Context("ingame",  "malilib.label.key_context.ingame", 0);
        public static final Context GUI    = new Context("gui",     "malilib.label.key_context.gui", 1);
        public static final Context ANY    = new Context("any",     "malilib.label.key_context.any", 2);

        public static final ImmutableList<Context> VALUES = ImmutableList.of(INGAME, GUI, ANY);

        protected final int iconIndex;

        private Context(String name, String translationKey, int iconIndex)
        {
            super(name, translationKey);

            this.iconIndex = iconIndex;
        }

        public int getIconIndex()
        {
            return this.iconIndex;
        }
    }
}
