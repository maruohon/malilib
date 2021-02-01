package fi.dy.masa.malilib.gui.config.liteloader;

import fi.dy.masa.malilib.gui.BaseScreen;

public interface DialogHandler
{
    /**
     * Open the provided GUI as a "dialog window"
     * @param gui
     */
    void openDialog(BaseScreen gui);

    /**
     * Close the previously opened "dialog window"
     */
    void closeDialog();
}
