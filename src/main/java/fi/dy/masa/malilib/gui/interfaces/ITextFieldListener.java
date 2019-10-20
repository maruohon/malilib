package fi.dy.masa.malilib.gui.interfaces;

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;

public interface ITextFieldListener<T extends GuiTextFieldGeneric>
{
    default boolean onGuiClosed(T textField)
    {
        return false;
    }

    boolean onTextChange(T textField);
}
