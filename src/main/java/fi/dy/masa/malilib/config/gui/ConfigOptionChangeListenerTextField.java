package fi.dy.masa.malilib.config.gui;

import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import net.minecraft.client.gui.GuiTextField;

public class ConfigOptionChangeListenerTextField
{
    private final IConfigValue config;
    private final GuiTextField textField;
    private final ButtonBase buttonReset;

    public ConfigOptionChangeListenerTextField(IConfigValue config, GuiTextField textField, ButtonBase buttonReset)
    {
        this.config = config;
        this.textField = textField;
        this.buttonReset = buttonReset;
    }

    public void onKeyTyped(int keyCode)
    {
        this.buttonReset.enabled = this.config.isModified(this.textField.getText());
    }
}
