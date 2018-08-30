package fi.dy.masa.malilib.gui.interfaces;

import java.util.List;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.gui.ButtonPressDirtyListenerSimple;
import fi.dy.masa.malilib.gui.button.ButtonBase;

public interface IConfigGui
{
    /**
     * When called, the implementer should clear all the stored config options
     * and any associated change listeners etc.
     */
    void clearOptions();

    /**
     * Returns a list of all currently visible/available/selected/whatever config options
     * that the widget list can use.
     * @return
     */
    List<? extends IConfigValue> getConfigs();

    /**
     * Returns a simple dirty listener for button presses. The configs will be interpreted
     * as dirty, if the listener saw even one button press.
     * @return
     */
    ButtonPressDirtyListenerSimple<ButtonBase> getButtonPressListener();
}
