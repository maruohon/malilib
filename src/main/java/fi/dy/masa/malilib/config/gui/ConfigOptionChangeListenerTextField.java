package fi.dy.masa.malilib.config.gui;

import fi.dy.masa.malilib.config.IStringRepresentable;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import net.minecraft.client.gui.GuiTextField;

public class ConfigOptionChangeListenerTextField implements ITextFieldListener<GuiTextField>
{
    protected final IStringRepresentable config;
    protected final GuiTextField textField;
    protected final ButtonBase buttonReset;

    public ConfigOptionChangeListenerTextField(IStringRepresentable config, GuiTextField textField, ButtonBase buttonReset)
    {
        this.config = config;
        this.textField = textField;
        this.buttonReset = buttonReset;
    }

    @Override
    public boolean onTextChange(GuiTextField textField)
    {
        this.buttonReset.setEnabled(this.config.isModified(this.textField.getText()));
        return false;
    }
}
