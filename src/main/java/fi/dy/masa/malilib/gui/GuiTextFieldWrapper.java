package fi.dy.masa.malilib.gui;

import net.minecraft.client.gui.GuiTextField;

public class GuiTextFieldWrapper
{
    private final GuiTextField textField;
    private final ConfigOptionTextFieldChangeListener listener;

    public GuiTextFieldWrapper(GuiTextField textField, ConfigOptionTextFieldChangeListener listener)
    {
        this.textField = textField;
        this.listener = listener;
    }

    public GuiTextField getTextField()
    {
        return this.textField;
    }

    public ConfigOptionTextFieldChangeListener getListener()
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
