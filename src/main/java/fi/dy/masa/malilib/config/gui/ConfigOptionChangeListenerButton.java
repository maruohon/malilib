package fi.dy.masa.malilib.config.gui;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;

public class ConfigOptionChangeListenerButton<T extends ButtonBase> implements IButtonActionListener<T>
{
    private final IConfigResettable config;
    private final ButtonBase buttonReset;
    @Nullable
    private final ButtonPressDirtyListenerSimple<T> dirtyListener;

    public ConfigOptionChangeListenerButton(IConfigResettable config, T buttonReset, @Nullable ButtonPressDirtyListenerSimple<T> dirtyListener)
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

        if (this.dirtyListener != null)
        {
            // Call the dirty listener to know if the configs should be saved when the GUI is closed
            this.dirtyListener.actionPerformedWithButton(control, mouseButton);
        }
    }
}
