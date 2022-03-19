package fi.dy.masa.malilib.config.option;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.util.data.Int2BooleanFunction;

public class GenericButtonConfig extends BaseConfig 
{
    protected final String buttonText;
    protected final Int2BooleanFunction buttonListener;

    public GenericButtonConfig(String name, String buttonText, Int2BooleanFunction buttonListener)
    {
        super(name);

        this.buttonText = buttonText;
        this.buttonListener = buttonListener;
    }

    public GenericButtonConfig(String name, String buttonText, Int2BooleanFunction buttonListener,
                               String commentTranslationKey, Object... commentArgs)
    {
        super(name, commentTranslationKey, commentArgs);

        this.buttonText = buttonText;
        this.buttonListener = buttonListener;
    }

    public GenericButtonConfig(String name, String buttonText, Int2BooleanFunction buttonListener,
                               String nameTranslationKey, @Nullable String commentTranslationKey, Object... commentArgs)
    {
        super(name, nameTranslationKey, commentTranslationKey, commentArgs);

        this.buttonText = buttonText;
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

    public String getButtonText()
    {
        return this.buttonText;
    }

    public Int2BooleanFunction getButtonActionListener()
    {
        return this.buttonListener;
    }
}
