package fi.dy.masa.malilib.gui.interfaces;

import net.minecraft.client.gui.GuiTextField;

public interface ITextFieldListener<T extends GuiTextField>
{
    boolean onGuiClosed(T textField);

    boolean onTextChange(T textField);
}
