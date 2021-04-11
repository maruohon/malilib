package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.overlay.widget.ConfigStatusIndicatorContainerWidget;

public class ConfigStatusIndicatorContainerEntryWidget extends BaseInfoRendererWidgetEntryWidget<ConfigStatusIndicatorContainerWidget>
{
    public ConfigStatusIndicatorContainerEntryWidget(int x, int y, int width, int height,
                                                     int listIndex, int originalListIndex,
                                                     ConfigStatusIndicatorContainerWidget data,
                                                     @Nullable DataListWidget<? extends ConfigStatusIndicatorContainerWidget> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        this.canConfigure = true;
        this.canRemove = true;
        this.canToggle = true;
    }
}
