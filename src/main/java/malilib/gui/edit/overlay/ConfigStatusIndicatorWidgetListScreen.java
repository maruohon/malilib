package malilib.gui.edit.overlay;

import malilib.MaLiLibReference;
import malilib.gui.widget.list.entry.BaseInfoRendererWidgetEntryWidget;
import malilib.overlay.widget.ConfigStatusIndicatorContainerWidget;
import malilib.registry.Registry;

public class ConfigStatusIndicatorWidgetListScreen extends InfoRendererWidgetListScreen<ConfigStatusIndicatorContainerWidget>
{
    public ConfigStatusIndicatorWidgetListScreen()
    {
        super(InfoRendererWidgetListScreen.createSupplierFromInfoManagerForExactType(ConfigStatusIndicatorContainerWidget.class),
              ConfigStatusIndicatorContainerWidget::new,
              BaseInfoRendererWidgetEntryWidget::new);

        this.setTitle("malilib.title.screen.configs.config_status_indicator_configuration", MaLiLibReference.MOD_VERSION);
        this.canCreateNewWidgets = true;
    }

    @Override
    protected void importOverwriteRemoveOldWidgets()
    {
        Registry.INFO_WIDGET_MANAGER.removeMatchingWidgets(w -> w.getClass() == ConfigStatusIndicatorContainerWidget.class);
    }
}
