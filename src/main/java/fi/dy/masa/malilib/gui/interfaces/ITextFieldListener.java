package fi.dy.masa.malilib.gui.interfaces;

import net.minecraft.client.gui.widget.TextFieldWidget;

public interface ITextFieldListener<T extends TextFieldWidget>
{
    boolean onGuiClosed(T textField);

    boolean onTextChange(T textField);
}
