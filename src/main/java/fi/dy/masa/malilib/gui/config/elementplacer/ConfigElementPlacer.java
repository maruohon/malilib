package fi.dy.masa.malilib.gui.config.elementplacer;

import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.gui.config.GuiModConfigs;
import fi.dy.masa.malilib.gui.widget.WidgetContainer;

public interface ConfigElementPlacer<C extends ConfigOption<?>, T extends WidgetContainer>
{
    T createContainerWidget(GuiModConfigs gui, C config);

    void addScreenElements(GuiModConfigs gui, C config, T containerWidget);

    void updateElementsForCurrentValues(GuiModConfigs gui, C config, T containerWidget);

    void updateElementPositions(GuiModConfigs gui, C config, T containerWidget);
}
