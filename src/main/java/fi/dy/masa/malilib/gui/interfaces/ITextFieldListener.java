package fi.dy.masa.malilib.gui.interfaces;

import net.minecraft.client.gui.GuiTextField;

public interface ITextFieldListener<T extends GuiTextField>
{
    default boolean onGuiClosed(T textField)
    {
        return false;
    }

    default void onTextChange(char typedChar, int keyCode, T textField)
    {
        this.onTextChange(textField);
    }

    boolean onTextChange(T textField);
}
