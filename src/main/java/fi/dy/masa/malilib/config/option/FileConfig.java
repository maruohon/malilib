package fi.dy.masa.malilib.config.option;

import java.io.File;

public class FileConfig extends BaseGenericConfig<File>
{
    public FileConfig(String name, File defaultValue)
    {
        this(name, defaultValue, name);
    }

    public FileConfig(String name, File defaultValue, String comment)
    {
        super(name, defaultValue, comment);
    }

    public String getStringValue()
    {
        return this.value.getAbsolutePath();
    }
}
