package malilib.config.option;

import javax.annotation.Nullable;

import malilib.util.data.Int2BooleanFunction;

public class GenericButtonConfig extends BaseConfig 
{
    protected final String buttonTextTranslationKey;
    protected final Int2BooleanFunction buttonListener;

    public GenericButtonConfig(String name, String buttonTextTranslationKey, Int2BooleanFunction buttonListener)
    {
        super(name);

        this.buttonTextTranslationKey = buttonTextTranslationKey;
        this.buttonListener = buttonListener;
    }

    public GenericButtonConfig(String name, String buttonTextTranslationKey, Int2BooleanFunction buttonListener,
                               String commentTranslationKey, Object... commentArgs)
    {
        super(name, commentTranslationKey, commentArgs);

        this.buttonTextTranslationKey = buttonTextTranslationKey;
        this.buttonListener = buttonListener;
    }

    public GenericButtonConfig(String name, String buttonTextTranslationKey, Int2BooleanFunction buttonListener,
                               String nameTranslationKey, @Nullable String commentTranslationKey, Object... commentArgs)
    {
        super(name, nameTranslationKey, commentTranslationKey, commentArgs);

        this.buttonTextTranslationKey = buttonTextTranslationKey;
        this.buttonListener = buttonListener;
    }

    @Override
    public boolean isModified()
    {
        return false;
    }

    @Override
    public void resetToDefault()
    {
        // NO-OP
    }

    public String getButtonTextTranslationKey()
    {
        return this.buttonTextTranslationKey;
    }

    public Int2BooleanFunction getButtonActionListener()
    {
        return this.buttonListener;
    }
}
