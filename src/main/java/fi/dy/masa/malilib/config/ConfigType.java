package fi.dy.masa.malilib.config;

public class ConfigType
{
    public static final ConfigType BOOLEAN = new ConfigType("boolean");
    public static final ConfigType INTEGER = new ConfigType("integer");
    public static final ConfigType COLOR = new ConfigType("color");
    public static final ConfigType DOUBLE = new ConfigType("double");
    public static final ConfigType STRING = new ConfigType("string");
    public static final ConfigType FILE = new ConfigType("file");
    public static final ConfigType DIRECTORY = new ConfigType("directory");
    public static final ConfigType HOTKEY = new ConfigType("hotkey");

    public static final ConfigType STRING_LIST = new ConfigType("string_list");
    public static final ConfigType OPTION_LIST = new ConfigType("option_list");

    protected final String name;

    public ConfigType(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }
}
