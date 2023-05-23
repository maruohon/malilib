package malilib.gui.edit.overlay;

import malilib.MaLiLibReference;
import malilib.gui.widget.list.entry.BaseInfoRendererWidgetEntryWidget;
import malilib.overlay.widget.InfoRendererWidget;

public class AllInfoWidgetsListScreen extends InfoRendererWidgetListScreen<InfoRendererWidget>
{
    public AllInfoWidgetsListScreen()
    {
        super(InfoRendererWidgetListScreen.createSupplierFromInfoManagerForSubtypes(InfoRendererWidget.class),
              null,
              BaseInfoRendererWidgetEntryWidget::new);

        this.setTitle("malilib.title.screen.configs.info_renderer_widgets", MaLiLibReference.MOD_VERSION);
    }
}
