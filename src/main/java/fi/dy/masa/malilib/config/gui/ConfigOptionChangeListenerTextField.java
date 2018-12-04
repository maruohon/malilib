package fi.dy.masa.malilib.config.gui;

import fi.dy.masa.malilib.config.IStringRepresentable;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import net.minecraft.client.gui.GuiTextField;

public class ConfigOptionChangeListenerTextField
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

    public void onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        this.buttonReset.enabled = this.config.isModified(this.textField.getText());
    }
}
