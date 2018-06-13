package fi.dy.masa.malilib.config.gui;

import com.mumfrey.liteloader.modconfig.AbstractConfigPanel.ConfigTextField;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import net.minecraft.client.Minecraft;

public class ConfigOptionListenerResetConfig implements IButtonActionListener<ButtonGeneric>
{
    private final ConfigResetterBase reset;
    private final IConfigValue config;
    private final ButtonGeneric buttonReset;

    public ConfigOptionListenerResetConfig(ConfigResetterBase reset, IConfigValue config, ButtonGeneric buttonReset)
    {
        this.reset = reset;
        this.config = config;
        this.buttonReset = buttonReset;
    }

    @Override
    public void actionPerformed(ButtonGeneric control)
    {
        this.config.resetToDefault();
        this.buttonReset.playPressSound(Minecraft.getMinecraft().getSoundHandler());
        this.updateElements();
    }

    @Override
    public void actionPerformedWithButton(ButtonGeneric control, int mouseButton)
    {
        this.actionPerformed(control);
    }

    public void updateElements()
    {
        this.buttonReset.enabled = this.config.isModified();
        this.reset.resetConfigOption();
    }

    public abstract static class ConfigResetterBase
    {
        public abstract void resetConfigOption();
    }

    public static class ConfigResetterButton extends ConfigResetterBase
    {
        private final ButtonBase button;

        public ConfigResetterButton(ButtonBase button)
        {
            this.button = button;
        }

        @Override
        public void resetConfigOption()
        {
            this.button.updateDisplayString();
        }
    }

    public static class ConfigResetterTextField extends ConfigResetterBase
    {
        private final IConfigValue config;
        private final ConfigTextField textField;

        public ConfigResetterTextField(IConfigValue config, ConfigTextField textField)
        {
            this.config = config;
            this.textField = textField;
        }

        @Override
        public void resetConfigOption()
        {
            this.textField.setText(this.config.getStringValue());
        }
    }
}
