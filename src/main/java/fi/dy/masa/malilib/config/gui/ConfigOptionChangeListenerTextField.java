package fi.dy.masa.malilib.config.gui;

import com.mumfrey.liteloader.modconfig.AbstractConfigPanel.ConfigTextField;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.gui.button.ButtonBase;

public class ConfigOptionChangeListenerTextField
{
    private final IConfigValue config;
    private final ConfigTextField textField;
    private final ButtonBase buttonReset;

    public ConfigOptionChangeListenerTextField(IConfigValue config, ConfigTextField textField, ButtonBase buttonReset)
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
