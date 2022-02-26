package fi.dy.masa.malilib.gui.widget.button;

public interface ButtonActionListener
{
    /**
     * Called when a button is clicked with the mouse
     * @param mouseButton the mouse button that was clicked. 0 = left click, 1 = right click, 2 = middle click
     * @param buttonWidget the button widget this listener is tied to
     * @return true if the action was successful
     */
    boolean actionPerformedWithButton(int mouseButton, GenericButton buttonWidget);
}
