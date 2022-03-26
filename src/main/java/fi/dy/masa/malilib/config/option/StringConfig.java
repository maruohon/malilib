package fi.dy.masa.malilib.config.option;

public class StringConfig extends BaseGenericConfig<String>
{
    public StringConfig(String name, String defaultValue)
    {
        this(name, defaultValue, name);
    }

    public StringConfig(String name, String defaultValue, String comment)
    {
        super(name, defaultValue, comment);
    }
}
