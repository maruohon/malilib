package fi.dy.masa.malilib.config.gui;

import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;

public class ConfigOptionChangeListenerButton<T extends ButtonBase> implements IButtonActionListener<T>
{
    private final IConfigValue config;
    private final ConfigOptionDirtyListener<T> dirtyListener;
    private final ButtonBase buttonReset;

    public ConfigOptionChangeListenerButton(IConfigValue config, ConfigOptionDirtyListener<T> dirtyListener, T buttonReset)
    {
        this.config = config;
        this.dirtyListener = dirtyListener;
        this.buttonReset = buttonReset;
    }

    @Override
    public void actionPerformed(T control)
    {
        this.buttonReset.enabled = this.config.isModified();
    }

    @Override
    public void actionPerformedWithButton(T control, int mouseButton)
    {
        this.actionPerformed(control);

        // Call the dirty listener to know if the configs should be saved when the GUI is closed
        this.dirtyListener.actionPerformedWithButton(control, mouseButton);
    }
}
