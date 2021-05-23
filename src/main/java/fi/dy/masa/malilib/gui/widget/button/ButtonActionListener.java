package fi.dy.masa.malilib.gui.widget.button;

public interface ButtonActionListener
{
    /**
     * Called when a button is clicked with the mouse
     * @param mouseButton
     * @return true if the action was successful
     */
    boolean actionPerformedWithButton(int mouseButton);
}
