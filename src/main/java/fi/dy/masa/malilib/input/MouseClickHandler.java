package fi.dy.masa.malilib.input;

import fi.dy.masa.malilib.event.PrioritizedEventHandler;

public interface MouseClickHandler extends PrioritizedEventHandler
{
    /**
     * Called on mouse events with the key or wheel value and whether the key was pressed or released.
     * @param mouseButton the button that was pressed or released
     * @param buttonState the new state of the button
     * @return true if further processing of this event should be cancelled
     */
    boolean onMouseClick(int mouseX, int mouseY, int mouseButton, boolean buttonState);
}
