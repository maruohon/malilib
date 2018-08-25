package fi.dy.masa.malilib.config.gui;

import com.mumfrey.liteloader.modconfig.AbstractConfigPanel.ConfigTextField;

public class ConfigTextFieldWrapper
{
    private final ConfigTextField textField;
    private final ConfigOptionChangeListenerTextField listener;

    public ConfigTextFieldWrapper(ConfigTextField textField, ConfigOptionChangeListenerTextField listener)
    {
        this.textField = textField;
        this.listener = listener;
    }

    public ConfigTextField getConfigTextField()
    {
        return this.textField;
    }

    public ConfigOptionChangeListenerTextField getListener()
    {
        return this.listener;
    }

    public boolean keyTyped(char typedChar, int keyCode)
    {
        if (this.textField.getNativeTextField().isFocused())
        {
            //boolean ret = this.textField.getNativeTextField().textboxKeyTyped(typedChar, keyCode);
            this.listener.onKeyTyped(keyCode);
            //return ret;
        }

        return false;
    }
}
