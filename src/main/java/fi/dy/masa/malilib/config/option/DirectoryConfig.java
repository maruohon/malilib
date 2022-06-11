package fi.dy.masa.malilib.config.option;

import java.nio.file.Path;

public class DirectoryConfig extends FileConfig
{
    public DirectoryConfig(String name, Path defaultValue)
    {
        super(name, defaultValue, name);
    }
}
