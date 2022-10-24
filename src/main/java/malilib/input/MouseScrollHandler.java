package malilib.input;

import malilib.event.PrioritizedEventHandler;

public interface MouseScrollHandler extends PrioritizedEventHandler
{
    /**
     * Called when the mouse wheel is scrolled
     * @param mouseX the x-position of the mouse cursor
     * @param mouseY the y-position of the mouse cursor
     * @param deltaX the horizontal scroll amount
     * @param deltaY the vertical scroll amount
     * @return true if further processing of this event should be cancelled
     */
    boolean onMouseScroll(int mouseX, int mouseY, double deltaX, double deltaY);
}
