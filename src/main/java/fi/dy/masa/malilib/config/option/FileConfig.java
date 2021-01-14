package fi.dy.masa.malilib.config.option;

import java.io.File;
import com.google.gson.JsonElement;
import fi.dy.masa.malilib.MaLiLib;

public class FileConfig extends BaseStringConfig<File>
{
    protected File file;

    public FileConfig(String name, File defaultValue)
    {
        this(name, defaultValue, name);
    }

    public FileConfig(String name, File defaultValue, String comment)
    {
        super(name, defaultValue.getAbsolutePath(), comment);

        this.file = defaultValue;
    }

    public File getFile()
    {
        return this.file;
    }

    @Override
    public File getValue()
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
                this.onValueLoaded(this.file);
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
