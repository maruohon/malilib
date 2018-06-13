package fi.dy.masa.malilib.config.gui;

import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;

public class ConfigOptionDirtyListener<T extends ButtonBase> implements IButtonActionListener<T>
{
    private boolean dirty;

    @Override
    public void actionPerformed(T control)
    {
        this.dirty = true;
    }

    @Override
    public void actionPerformedWithButton(T control, int mouseButton)
    {
        this.actionPerformed(control);
    }

    public boolean isDirty()
    {
        return this.dirty;
    }

    public void resetDirty()
    {
        this.dirty = false;
    }
}
