package fi.dy.masa.malilib.config.option;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.widget.button.ButtonActionListener;

public class GenericButtonConfig extends BaseConfig 
{
    protected final String buttonText;
    protected final ButtonActionListener buttonListener;

    public GenericButtonConfig(String name, String buttonText, ButtonActionListener buttonListener)
    {
        super(name);

        this.buttonText = buttonText;
        this.buttonListener = buttonListener;
    }

    public GenericButtonConfig(String name, String buttonText, ButtonActionListener buttonListener,
                               String commentTranslationKey, Object... commentArgs)
    {
        super(name, commentTranslationKey, commentArgs);

        this.buttonText = buttonText;
        this.buttonListener = buttonListener;
    }

    public GenericButtonConfig(String name, String buttonText, ButtonActionListener buttonListener,
                               String nameTranslationKey, @Nullable String commentTranslationKey, Object... commentArgs)
    {
        super(name, nameTranslationKey, commentTranslationKey, commentArgs);

        this.buttonText = buttonText;
        this.buttonListener = buttonListener;
    }

    public String getButtonText()
    {
        return this.buttonText;
    }

    public ButtonActionListener getButtonActionListener()
    {
        return this.buttonListener;
    }
}
