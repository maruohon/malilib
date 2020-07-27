package fi.dy.masa.malilib.input;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.value.ConfigOptionListEntry;
import fi.dy.masa.malilib.config.value.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;

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

        obj.addProperty("activate_on", this.activateOn.name());
        obj.addProperty("context", this.context.name());
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
            for (Context ctx : Context.values())
            {
                if (ctx.name().equalsIgnoreCase(contextStr))
                {
                    context = ctx;
                    break;
                }
            }
        }

        if (activateStr != null)
        {
            for (KeyAction act : KeyAction.values())
            {
                if (act.name().equalsIgnoreCase(activateStr))
                {
                    activateOn = act;
                    break;
                }
            }
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
        if (getClass() != obj.getClass())
            return false;
        KeyBindSettings other = (KeyBindSettings) obj;
        if (activateOn != other.activateOn)
            return false;
        if (context != other.context)
            return false;
        if (allowEmpty != other.allowEmpty)
            return false;
        if (allowExtraKeys != other.allowExtraKeys)
            return false;
        if (cancel != other.cancel)
            return false;
        if (exclusive != other.exclusive)
            return false;
        if (orderSensitive != other.orderSensitive)
            return false;
        return true;
    }

    public enum Context implements IConfigOptionListEntry<Context>
    {
        INGAME  ("ingame",  "malilib.label.key_context.ingame"),
        GUI     ("gui",     "malilib.label.key_context.gui"),
        ANY     ("any",     "malilib.label.key_context.any");

        public static final ImmutableList<Context> VALUES = ImmutableList.copyOf(values());

        private final String configString;
        private final String translationKey;

        Context(String configString, String translationKey)
        {
            this.configString = configString;
            this.translationKey = translationKey;
        }

        @Override
        public String getStringValue()
        {
            return this.configString;
        }

        @Override
        public String getDisplayName()
        {
            return StringUtils.translate(this.translationKey);
        }

        @Override
        public Context cycle(boolean forward)
        {
            return ConfigOptionListEntry.cycleValue(VALUES, this.ordinal(), forward);
        }

        @Override
        public Context fromString(String name)
        {
            return ConfigOptionListEntry.findValueByName(name, VALUES);
        }
    }
}
