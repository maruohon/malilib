package fi.dy.masa.malilib.gui.config.elementplacer;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseDataListEntryWidget;

public interface ConfigElementPlacer<C extends ConfigInfo>
{
    void addScreenElements(C config, BaseDataListEntryWidget<C> containerWidget, BaseConfigScreen gui);

    @Nullable
    default BaseDataListEntryWidget<C> createContainerWidget(int x, int y, int width, int height, int listIndex, C config, BaseConfigScreen gui)
    {
        BaseDataListEntryWidget<C> widget = new BaseDataListEntryWidget<>(x, y, width, height, listIndex, config);

        this.addScreenElements(config, widget, gui);

        return widget;
    }

    default void updateElementsForCurrentValues(C config, BaseDataListEntryWidget<C> containerWidget, BaseConfigScreen gui)
    {
    }

    default void updateElementPositions(C config, BaseDataListEntryWidget<C> containerWidget, BaseConfigScreen gui)
    {
    }
}
