package malilib.gui.action;

public interface ActionWidgetContainer
{
    /**
     * @return true if the widget edit mode is currently active
     */
    boolean isEditMode();

    /**
     * @return The current grid size in the edit mode. Returns -1 if the grid is disabled.
     */
    int getGridSize();

    /**
     * @return true if the current screen is configured to close when an action is triggered
     */
    boolean shouldCloseScreenOnExecute();

    /**
     * Can be called to notify that the internal state/settings of a widget were modified.
     * This can be used to detect when the screen state needs to be saved again.
     */
    void notifyWidgetEdited();
}
