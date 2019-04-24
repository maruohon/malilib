package fi.dy.masa.malilib.gui;

import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerTextField;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class GuiTextFieldWrapper
{
    private final TextFieldWidget textField;
    private final ConfigOptionChangeListenerTextField listener;

    public GuiTextFieldWrapper(TextFieldWidget textField, ConfigOptionChangeListenerTextField listener)
    {
        this.textField = textField;
        this.listener = listener;
    }

    public TextFieldWidget getTextField()
    {
        return this.textField;
    }

    public ConfigOptionChangeListenerTextField getListener()
    {
        return this.listener;
    }

    public void setFocused(boolean focused)
    {
        this.textField.method_1876(focused); // setFocused
    }

    public boolean onKeyTyped(int key, int scanCode, int modifiers)
    {
        if (this.textField.isFocused())
        {
            boolean ret = this.textField.keyPressed(key, scanCode, modifiers);
            this.listener.onKeyTyped(key, scanCode, modifiers);
            return ret;
        }

        return false;
    }

    public boolean onCharTyped(char charIn, int modifiers)
    {
        if (this.textField.isFocused())
        {
            boolean ret = this.textField.charTyped(charIn, modifiers);
            this.listener.onKeyTyped(0, 0, 0); // FIXME?
            return ret;
        }

        return false;
    }
}
