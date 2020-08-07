package fi.dy.masa.malilib.gui.listener;

import fi.dy.masa.malilib.gui.button.BaseButton;
import fi.dy.masa.malilib.gui.button.ButtonActionListener;

public class ButtonPressDirtyListenerSimple implements ButtonActionListener
{
    private boolean dirty;

    @Override
    public void actionPerformedWithButton(BaseButton button, int mouseButton)
    {
        this.dirty = true;
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
