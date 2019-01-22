package fi.dy.masa.malilib.config.gui;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.config.IStringRepresentable;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class ConfigOptionListenerResetConfig implements IButtonActionListener<ButtonGeneric>
{
    private final ConfigResetterBase reset;
    private final IConfigResettable config;
    private final ButtonGeneric buttonReset;
    @Nullable
    private final ButtonPressDirtyListenerSimple<ButtonBase> dirtyListener;

    public ConfigOptionListenerResetConfig(IConfigResettable config, ConfigResetterBase reset,
            ButtonGeneric buttonReset, @Nullable ButtonPressDirtyListenerSimple<ButtonBase> dirtyListener)
    {
        this.config = config;
        this.reset = reset;
        this.buttonReset = buttonReset;
        this.dirtyListener = dirtyListener;
    }

    @Override
    public void actionPerformed(ButtonGeneric control)
    {
        this.config.resetToDefault();
        this.buttonReset.playPressedSound(MinecraftClient.getInstance().getSoundLoader());
        this.buttonReset.enabled = this.config.isModified();
        this.reset.resetConfigOption();
    }

    @Override
    public void actionPerformedWithButton(ButtonGeneric control, int mouseButton)
    {
        this.actionPerformed(control);

        if (this.dirtyListener != null)
        {
            this.dirtyListener.actionPerformedWithButton(control, mouseButton);
        }
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
        private final IStringRepresentable config;
        private final TextFieldWidget textField;

        public ConfigResetterTextField(IStringRepresentable config, TextFieldWidget textField)
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
