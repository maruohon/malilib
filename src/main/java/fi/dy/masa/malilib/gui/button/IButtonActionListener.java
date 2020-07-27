package fi.dy.masa.malilib.gui.button;

public interface IButtonActionListener
{
    /**
     * Called when a button is clicked with the mouse
     * @param button
     * @param mouseButton
     */
    void actionPerformedWithButton(ButtonBase button, int mouseButton);
}
