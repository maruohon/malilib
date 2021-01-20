package fi.dy.masa.malilib.config.option;

import java.io.File;
import com.google.gson.JsonElement;
import fi.dy.masa.malilib.MaLiLib;

public class FileConfig extends BaseStringConfig<File>
{
    public FileConfig(String name, File defaultValue)
    {
        this(name, defaultValue, name);
    }

    public FileConfig(String name, File defaultValue, String comment)
    {
        super(name, defaultValue, comment);

        this.stringValue = defaultValue.getAbsolutePath();
    }

    @Override
    public boolean setValue(File newValue)
    {
        if (this.locked == false)
        {
            this.stringValue = newValue.getAbsolutePath();
            return super.setValue(newValue);
        }

        return false;
    }

    @Override
    public void setValueFromString(String newValue)
    {
        if (this.stringValue.equals(newValue) == false)
        {
            this.setValue(new File(newValue));
        }
    }

    @Override
    public void setValueFromJsonElement(JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.stringValue = element.getAsString();
                this.value = new File(this.stringValue);
                this.onValueLoaded(this.value);
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }

        this.cacheSavedValue();
    }
}
