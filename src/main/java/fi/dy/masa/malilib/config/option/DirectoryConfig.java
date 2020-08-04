package fi.dy.masa.malilib.config.option;

import java.io.File;

public class DirectoryConfig extends FileConfig
{
    public DirectoryConfig(String name, File defaultValue)
    {
        super(name, defaultValue, name);
    }
}
