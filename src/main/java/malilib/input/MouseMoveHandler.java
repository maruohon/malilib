package malilib.input;

import malilib.event.PrioritizedEventHandler;

public interface MouseMoveHandler extends PrioritizedEventHandler
{
    /**
     * Called when the mouse is moved
     */
    void onMouseMove(int mouseX, int mouseY);
}
