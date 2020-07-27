package fi.dy.masa.malilib.config.option;

import java.io.File;
import com.google.gson.JsonElement;
import fi.dy.masa.malilib.LiteModMaLiLib;
import fi.dy.masa.malilib.config.ConfigType;

public class ConfigFile extends ConfigStringBase<File>
{
    protected File file;

    public ConfigFile(String name, File defaultValue, String comment)
    {
        this(ConfigType.FILE, name, defaultValue, comment);
    }

    protected ConfigFile(ConfigType type, String name, File defaultValue, String comment)
    {
        super(type, name, defaultValue.getAbsolutePath(), comment);

        this.file = defaultValue;
    }

    public File getFile()
    {
        return this.file;
    }

    @Override
    public void setValueFromString(String value)
    {
        if (this.value.equals(value) == false)
        {
            File oldFile = this.file;
            this.value = value;
            this.file = new File(value);
            this.onValueChanged(this.file, oldFile);
        }
    }

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.value = element.getAsString();
                this.file = new File(this.value);
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
}
