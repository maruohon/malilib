package malilib.config.option;

import java.nio.file.Path;

public class FileConfig extends BaseGenericConfig<Path>
{
    public FileConfig(String name, Path defaultValue)
    {
        this(name, defaultValue, name);
    }

    public FileConfig(String name, Path defaultValue, String commentTranslationKey, Object... commentArgs)
    {
        super(name, defaultValue, commentTranslationKey, commentArgs);
    }

    public String getStringValue()
    {
        return this.value.toAbsolutePath().toString();
    }
}
