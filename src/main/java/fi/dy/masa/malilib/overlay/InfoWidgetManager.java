package fi.dy.masa.malilib.overlay;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ArrayListMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.util.ConfigUtils;
import fi.dy.masa.malilib.overlay.widget.ConfigStatusIndicatorContainerWidget;
import fi.dy.masa.malilib.overlay.widget.InfoRendererWidget;
import fi.dy.masa.malilib.overlay.widget.MessageRendererWidget;
import fi.dy.masa.malilib.overlay.widget.StringListRendererWidget;
import fi.dy.masa.malilib.overlay.widget.ToastRendererWidget;
import fi.dy.masa.malilib.util.JsonUtils;

public class InfoWidgetManager
{
    public static final InfoWidgetManager INSTANCE = new InfoWidgetManager(InfoOverlay.INSTANCE);

    protected final ArrayListMultimap<Class<? extends InfoRendererWidget>, InfoRendererWidget> widgets = ArrayListMultimap.create();
    protected final InfoOverlay infoOverlay;
    protected boolean dirty;

    public InfoWidgetManager(InfoOverlay infoOverlay)
    {
        this.infoOverlay = infoOverlay;
    }

    public void addWidget(InfoRendererWidget widget)
    {
        if (this.widgets.containsEntry(widget.getClass(), widget) == false)
        {
            this.widgets.put(widget.getClass(), widget);
            this.infoOverlay.getOrCreateInfoArea(widget.getScreenLocation()).addWidget(widget);
            this.dirty = true;
        }
    }

    public void removeWidget(InfoRendererWidget widget)
    {
        this.widgets.remove(widget.getClass(), widget);
        this.infoOverlay.getOrCreateInfoArea(widget.getScreenLocation()).removeWidget(widget);
        widget.invalidate();
        this.dirty = true;
    }

    @SuppressWarnings("unchecked")
    public <WIDGET extends InfoRendererWidget> List<WIDGET> getAllWidgetsOfExactType(Class<WIDGET> clazz)
    {
        return (List<WIDGET>) this.widgets.get(clazz);
    }

    public <WIDGET extends InfoRendererWidget> List<WIDGET> getAllWidgetsExtendingType(Class<WIDGET> clazz)
    {
        ArrayList<WIDGET> list = new ArrayList<>();

        for (InfoRendererWidget widget : this.widgets.values())
        {
            if (clazz.isInstance(widget))
            {
                list.add(clazz.cast(widget));
            }
        }

        return list;
    }

    protected void clearWidgets()
    {
        for (InfoRendererWidget widget : this.widgets.values())
        {
            this.infoOverlay.getOrCreateInfoArea(widget.getScreenLocation()).removeWidget(widget);
        }

        this.widgets.clear();
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();

        for (InfoRendererWidget widget : this.widgets.values())
        {
            if (widget.getShouldSerialize())
            {
                arr.add(widget.toJson());
            }
        }

        obj.add("info_widgets", arr);

        return obj;
    }

    public void fromJson(JsonElement el)
    {
        this.clearWidgets();

        if (el.isJsonObject() == false)
        {
            return;
        }

        JsonObject obj = el.getAsJsonObject();
        JsonUtils.readArrayElementsIfPresent(obj, "info_widgets", this::readAndAddWidget);

        // This causes all the widgets to get re-fetched immediately
        InfoOverlay.INSTANCE.tick();
    }

    protected void readAndAddWidget(JsonElement el)
    {
        InfoRendererWidget widget = InfoRendererWidget.createFromJson(el);

        if (widget != null)
        {
            this.widgets.put(widget.getClass(), widget);
            this.infoOverlay.getOrCreateInfoArea(widget.getScreenLocation()).addWidget(widget);
        }
    }

    public boolean saveToFileIfDirty()
    {
        if (this.dirty)
        {
            return this.saveToFile();
        }

        return false;
    }

    public void loadFromFile()
    {
        File dir = ConfigUtils.getActiveConfigDirectory();
        JsonUtils.loadFromFile(dir, MaLiLibReference.MOD_ID + "_info_widgets.json", this::fromJson);
    }

    public boolean saveToFile()
    {
        File dir = ConfigUtils.getActiveConfigDirectory();
        File backupDir = new File(dir, "config_backups");
        File saveFile = new File(dir, MaLiLibReference.MOD_ID + "_info_widgets.json");
        boolean antiDuplicate = MaLiLibConfigs.Generic.CONFIG_BACKUP_ANTI_DUPLICATE.getBooleanValue();

        this.dirty = false;

        return JsonUtils.saveToFile(dir, backupDir, saveFile, 10, antiDuplicate, this::toJson);
    }

    private static final HashMap<String, InfoWidgetFactory> WIDGET_FACTORIES_BY_TYPE = new HashMap<>();

    public static void registerWidgetFactory(Class<? extends InfoRendererWidget> clazz, InfoWidgetFactory factory)
    {
        WIDGET_FACTORIES_BY_TYPE.put(clazz.getName(), factory);
    }

    @Nullable
    public static InfoWidgetFactory getWidgetFactory(String type)
    {
        return WIDGET_FACTORIES_BY_TYPE.get(type);
    }

    private static void registerDefaultFactories()
    {
        registerWidgetFactory(ConfigStatusIndicatorContainerWidget.class,   ConfigStatusIndicatorContainerWidget::new);
        registerWidgetFactory(MessageRendererWidget.class,                  MessageRendererWidget::new);
        registerWidgetFactory(StringListRendererWidget.class,               StringListRendererWidget::new);
        registerWidgetFactory(ToastRendererWidget.class,                    ToastRendererWidget::new);
    }

    static
    {
        registerDefaultFactories();
    }

    public interface InfoWidgetFactory
    {
        InfoRendererWidget create();
    }
}
