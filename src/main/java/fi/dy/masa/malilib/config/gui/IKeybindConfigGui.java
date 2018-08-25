package fi.dy.masa.malilib.config.gui;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;

public interface IKeybindConfigGui
{
    ConfigOptionDirtyListener<ButtonBase> getConfigListener();

    void setActiveKeybindButton(@Nullable ConfigButtonKeybind button);
}
