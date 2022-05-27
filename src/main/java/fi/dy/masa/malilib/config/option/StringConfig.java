package fi.dy.masa.malilib.config.option;

public class StringConfig extends BaseGenericConfig<String>
{
    public StringConfig(String name, String defaultValue)
    {
        this(name, defaultValue, name);
    }

    public StringConfig(String name, String defaultValue,
                        String commentTranslationKey, Object... commentArgs)
    {
        super(name, defaultValue, commentTranslationKey, commentArgs);
    }
}
