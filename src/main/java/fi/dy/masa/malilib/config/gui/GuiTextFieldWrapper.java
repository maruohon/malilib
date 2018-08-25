package fi.dy.masa.malilib.config.gui;

import net.minecraft.client.gui.GuiTextField;

public class GuiTextFieldWrapper
{
    private final GuiTextField textField;
    private final GuiTextFieldChangeListener listener;

    public GuiTextFieldWrapper(GuiTextField textField, GuiTextFieldChangeListener listener)
    {
        this.textField = textField;
        this.listener = listener;
    }

    public GuiTextField getTextField()
    {
        return this.textField;
    }

    public GuiTextFieldChangeListener getListener()
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
