package fi.dy.masa.malilib.overlay;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ArrayListMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.overlay.widget.ConfigStatusIndicatorContainerWidget;
import fi.dy.masa.malilib.overlay.widget.InfoRendererWidget;
import fi.dy.masa.malilib.overlay.widget.MessageRendererWidget;
import fi.dy.masa.malilib.overlay.widget.StringListRendererWidget;
import fi.dy.masa.malilib.overlay.widget.ToastRendererWidget;
import fi.dy.masa.malilib.util.FileUtils;
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
        this.dirty = true;
    }

    @SuppressWarnings("unchecked")
    public <WIDGET extends InfoRendererWidget> List<WIDGET> getAllWidgetsOfType(Class<WIDGET> clazz)
    {
        return (List<WIDGET>) this.widgets.get(clazz);
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

    public void fromJson(JsonObject obj)
    {
        this.clearWidgets();

        if (JsonUtils.hasArray(obj, "info_widgets") == false)
        {
            return;
        }

        JsonArray arr = obj.get("info_widgets").getAsJsonArray();
        final int count = arr.size();

        for (int i = 0; i < count; i++)
        {
            JsonElement el = arr.get(i);

            if (el.isJsonObject())
            {
                JsonObject entryObj = el.getAsJsonObject();
                InfoRendererWidget widget = InfoRendererWidget.createFromJson(entryObj);

                if (widget != null)
                {
                    this.widgets.put(widget.getClass(), widget);
                    this.infoOverlay.getOrCreateInfoArea(widget.getScreenLocation()).addWidget(widget);
                }
            }
        }
    }

    public void loadFromFile()
    {
        File dir = FileUtils.getConfigDirectory();
        File saveFile = new File(dir, MaLiLibReference.MOD_ID + "_info_widgets.json");

        if (saveFile.exists() && saveFile.isFile() && saveFile.canRead())
        {
            this.loadFromFile(saveFile);
        }
    }

    public void loadFromFile(File saveFile)
    {
        JsonElement element = JsonUtils.parseJsonFile(saveFile);

        if (element != null && element.isJsonObject())
        {
            this.fromJson(element.getAsJsonObject());
        }
    }

    public boolean saveToFileIfDirty()
    {
        if (this.dirty)
        {
            this.dirty = false;
            return this.saveToFile();
        }

        return false;
    }

    public boolean saveToFile()
    {
        File dir = FileUtils.getConfigDirectory();
        File backupDir = new File(dir, "config_backups");
        File saveFile = new File(dir, MaLiLibReference.MOD_ID + "_info_widgets.json");

        return JsonUtils.saveToFile(dir, backupDir, saveFile, 10, this::toJson);
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
