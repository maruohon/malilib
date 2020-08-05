package fi.dy.masa.malilib.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.LiteModMaLiLib;
import fi.dy.masa.malilib.input.IHotkey;
import fi.dy.masa.malilib.input.IKeyBind;
import fi.dy.masa.malilib.input.KeyBindMulti;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.input.KeyCallbackToggleBooleanConfigWithMessage;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class HotkeyedBooleanConfig extends BooleanConfig implements IHotkey
{
    protected final IKeyBind keyBind;

    public HotkeyedBooleanConfig(String name, boolean defaultValue, String defaultHotkey)
    {
        this(name, defaultValue, defaultHotkey, name);
    }

    public HotkeyedBooleanConfig(String name, boolean defaultValue, String defaultHotkey, String comment)
    {
        this(name, defaultValue, defaultHotkey, StringUtils.splitCamelCase(name), comment);
    }

    public HotkeyedBooleanConfig(String name, boolean defaultValue, String defaultHotkey, String prettyName, String comment)
    {
        this(name, defaultValue, defaultHotkey, KeyBindSettings.DEFAULT, prettyName, comment);
    }

    public HotkeyedBooleanConfig(String name, boolean defaultValue, String defaultHotkey, KeyBindSettings settings, String prettyName, String comment)
    {
        super(name, defaultValue, prettyName, comment);

        this.keyBind = KeyBindMulti.fromStorageString(name, defaultHotkey, settings);
        this.keyBind.setCallback(new KeyCallbackToggleBooleanConfigWithMessage(this));

        this.cacheSavedValue();
    }

    @Override
    public IKeyBind getKeyBind()
    {
        return this.keyBind;
    }

    @Override
    public boolean isModified()
    {
        return super.isModified() || this.keyBind.isModified();
    }

    @Override
    public boolean isDirty()
    {
        return super.isDirty() || this.keyBind.isDirty();
    }

    @Override
    public void cacheSavedValue()
    {
        super.cacheSavedValue();

        // Tis method unfortunately gets called already from the super constructor,
        // before the field is set in this class's constructor.
        if (this.keyBind != null)
        {
            this.keyBind.cacheSavedValue();
        }
    }

    @Override
    public void resetToDefault()
    {
        super.resetToDefault();
        this.keyBind.resetToDefault();
    }

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();
                this.value = JsonUtils.getBooleanOrDefault(obj, "enabled", false);

                if (JsonUtils.hasObject(obj, "hotkey"))
                {
                    this.keyBind.setValueFromJsonElement(JsonUtils.getNestedObject(obj, "hotkey", false), configName);
                }

                this.onValueLoaded(this.value);
            }
            else
            {
                LiteModMaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element);
            }
        }
        catch (Exception e)
        {
            LiteModMaLiLib.logger.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }

        this.cacheSavedValue();
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        JsonObject obj = new JsonObject();
        obj.add("enabled", new JsonPrimitive(this.value));
        obj.add("hotkey", this.keyBind.getAsJsonElement());
        return obj;
    }
}
