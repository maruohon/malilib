package fi.dy.masa.malilib.input;

import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.action.ActionRegistry;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.input.callback.HotkeyCallback;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class CustomHotkeyDefinition implements Hotkey
{
    protected final String name;
    protected final KeyBind keyBind;
    protected final NamedAction action;

    public CustomHotkeyDefinition(String name, KeyBind keyBind, NamedAction action)
    {
        this.name = name;
        this.keyBind = keyBind;
        this.action = action;

        String custom = StringUtils.translate("malilib.label.custom");
        this.keyBind.setModInfo(new ModInfo(custom, custom));
        this.keyBind.setCallback(HotkeyCallback.of(action));
        this.keyBind.setNameTranslationKey(action.getDisplayName());
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getDisplayName()
    {
        return this.name;
    }

    @Override
    public KeyBind getKeyBind()
    {
        return this.keyBind;
    }

    public NamedAction getAction()
    {
        return this.action;
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("name", this.name);
        obj.addProperty("action", this.action.getRegistryName());
        obj.add("hotkey", this.keyBind.getAsJsonElement());

        return obj;
    }

    @Nullable
    public static CustomHotkeyDefinition fromJson(JsonElement el)
    {
        if (el.isJsonObject())
        {
            return null;
        }

        JsonObject obj = el.getAsJsonObject();
        KeyBind keyBind = KeyBindImpl.fromStorageString("", KeyBindSettings.INGAME_DEFAULT);

        if (JsonUtils.hasObject(obj, "hotkey"))
        {
            keyBind.setValueFromJsonElement(obj.get("hotkey"), "?");
        }

        String name = JsonUtils.getStringOrDefault(obj, "name", "?");
        String actionName = JsonUtils.getStringOrDefault(obj, "action", "");
        NamedAction action = ActionRegistry.INSTANCE.getAction(actionName);

        if (action == null)
        {
            action = new NamedAction(ModInfo.NO_MOD, actionName, actionName, actionName, (ctx) -> ActionResult.PASS);
        }

        return new CustomHotkeyDefinition(name, keyBind, action);
    }
}
