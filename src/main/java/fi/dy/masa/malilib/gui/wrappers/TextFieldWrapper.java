package fi.dy.masa.malilib.gui.wrappers;

import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class TextFieldWrapper<T extends TextFieldWidget>
{
    private final T textField;
    private final ITextFieldListener<T> listener;
    
    public TextFieldWrapper(T textField, ITextFieldListener<T> listener)
    {
        this.textField = textField;
        this.listener = listener;
    }

    public T getTextField()
    {
        return this.textField;
    }

    public ITextFieldListener<T> getListener()
    {
        return this.listener;
    }

    public void onGuiClosed()
    {
        if (this.listener != null)
        {
            this.listener.onGuiClosed(this.textField);
        }
    }

    public void draw(int mouseX, int mouseY)
    {
        this.textField.render(mouseX, mouseY, 0f);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.textField.mouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        return false;
    }

    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (this.textField.keyPressed(keyCode, scanCode, modifiers))
        {
            if (this.listener != null)
            {
                this.listener.onTextChange(this.textField);
            }

            return true;
        }

        return false;
    }

    public boolean onCharTyped(char charIn, int modifiers)
    {
        if (this.textField.charTyped(charIn, modifiers))
        {
            if (this.listener != null)
            {
                this.listener.onTextChange(this.textField);
            }

            return true;
        }

        return false;
    }
}
