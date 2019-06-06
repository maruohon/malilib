package fi.dy.masa.malilib.config.gui;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;

public class ConfigOptionChangeListenerButton implements IButtonActionListener
{
    private final IConfigResettable config;
    private final ButtonBase buttonReset;
    @Nullable
    private final ButtonPressDirtyListenerSimple dirtyListener;

    public ConfigOptionChangeListenerButton(IConfigResettable config, ButtonBase buttonReset, @Nullable ButtonPressDirtyListenerSimple dirtyListener)
    {
        this.config = config;
        this.dirtyListener = dirtyListener;
        this.buttonReset = buttonReset;
    }

    @Override
    public void actionPerformedWithButton(ButtonBase button, int mouseButton)
    {
        this.buttonReset.setEnabled(this.config.isModified());

        if (this.dirtyListener != null)
        {
            // Call the dirty listener to know if the configs should be saved when the GUI is closed
            this.dirtyListener.actionPerformedWithButton(button, mouseButton);
        }
    }
}
