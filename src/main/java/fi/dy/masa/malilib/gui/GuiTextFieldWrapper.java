package fi.dy.masa.malilib.gui;

import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerTextField;
import net.minecraft.client.gui.GuiTextField;

public class GuiTextFieldWrapper
{
    private final GuiTextField textField;
    private final ConfigOptionChangeListenerTextField listener;

    public GuiTextFieldWrapper(GuiTextField textField, ConfigOptionChangeListenerTextField listener)
    {
        this.textField = textField;
        this.listener = listener;
    }

    public GuiTextField getTextField()
    {
        return this.textField;
    }

    public ConfigOptionChangeListenerTextField getListener()
    {
        return this.listener;
    }

    public boolean keyTyped(char typedChar, int keyCode)
    {
        if (this.textField.isFocused())
        {
            boolean ret = this.textField.textboxKeyTyped(typedChar, keyCode);

            this.listener.onKeyTyped(keyCode);

            return ret;
        }

        return false;
    }
}
