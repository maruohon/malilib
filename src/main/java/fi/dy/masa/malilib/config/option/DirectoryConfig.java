package fi.dy.masa.malilib.config.option;

import java.io.File;
import fi.dy.masa.malilib.config.ConfigType;

public class DirectoryConfig extends FileConfig
{
    public DirectoryConfig(String name, File defaultValue, String comment)
    {
        super(ConfigType.DIRECTORY, name, defaultValue, comment);
    }
}
