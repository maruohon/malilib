package fi.dy.masa.malilib.input;

import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.action.ActionType;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.action.SimpleNamedAction;
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
        obj.add("hotkey", this.keyBind.getAsJsonElement());
        obj.add("action", this.action.toJson());

        return obj;
    }

    @Nullable
    public static CustomHotkeyDefinition fromJson(JsonElement el)
    {
        if (el.isJsonObject() == false)
        {
            return null;
        }

        JsonObject obj = el.getAsJsonObject();
        String name = JsonUtils.getStringOrDefault(obj, "name", "?");
        KeyBind keyBind = KeyBindImpl.fromStorageString("", KeyBindSettings.INGAME_DEFAULT);

        if (JsonUtils.hasObject(obj, "hotkey"))
        {
            keyBind.setValueFromJsonElement(obj.get("hotkey"), "?");
        }

        NamedAction action = null;

        if (JsonUtils.hasObject(obj, "action"))
        {
            action = ActionType.loadActionFromJson(JsonUtils.getNestedObject(obj, "action", true));
        }

        if (action == null)
        {
            action = new SimpleNamedAction("?", "?", ModInfo.NO_MOD, (ctx) -> ActionResult.PASS);
        }

        return new CustomHotkeyDefinition(name, keyBind, action);
    }
}
