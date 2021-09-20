package fi.dy.masa.malilib.overlay;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.overlay.widget.ConfigStatusIndicatorContainerWidget;
import fi.dy.masa.malilib.overlay.widget.InfoRendererWidget;
import fi.dy.masa.malilib.overlay.widget.MessageRendererWidget;
import fi.dy.masa.malilib.overlay.widget.StringListRendererWidget;
import fi.dy.masa.malilib.overlay.widget.ToastRendererWidget;

public class InfoWidgetRegistry
{
    protected final Map<String, InfoWidgetFactory> widgetFactoriesByType = new HashMap<>();

    public InfoWidgetRegistry()
    {
        this.registerDefaultFactories();
    }

    public void registerWidgetFactory(InfoWidgetFactory factory, String id)
    {
        this.widgetFactoriesByType.put(id, factory);
    }

    @Nullable
    public InfoWidgetFactory getWidgetFactory(String type)
    {
        return this.widgetFactoriesByType.get(type);
    }

    private void registerDefaultFactories()
    {
        this.registerWidgetFactory(ConfigStatusIndicatorContainerWidget::new,   MaLiLibReference.MOD_ID + ":csi_container");
        this.registerWidgetFactory(MessageRendererWidget::new,                  MaLiLibReference.MOD_ID + ":message_renderer");
        this.registerWidgetFactory(StringListRendererWidget::new,               MaLiLibReference.MOD_ID + ":string_list_renderer");
        this.registerWidgetFactory(ToastRendererWidget::new,                    MaLiLibReference.MOD_ID + ":toast_renderer");
    }

    public interface InfoWidgetFactory
    {
        InfoRendererWidget create();
    }
}
