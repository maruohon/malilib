package malilib.overlay;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ArrayListMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import malilib.MaLiLibReference;
import malilib.config.util.ConfigUtils;
import malilib.overlay.widget.InfoRendererWidget;
import malilib.registry.Registry;
import malilib.util.BackupUtils;
import malilib.util.data.json.JsonUtils;

public class InfoWidgetManager
{
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
        JsonUtils.getArrayElementsIfExists(obj, "info_widgets", this::readAndAddWidget);

        // This causes all the widgets to get re-fetched immediately
        Registry.INFO_OVERLAY.tick();
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

    public boolean saveToFile()
    {
        Path configDir = ConfigUtils.getActiveConfigDirectory();
        Path saveFile = configDir.resolve(MaLiLibReference.MOD_ID).resolve("info_widgets.json");
        Path backupDir = configDir.resolve("backups").resolve(MaLiLibReference.MOD_ID);

        if (BackupUtils.createRegularBackup(saveFile, backupDir) &&
            JsonUtils.writeJsonToFile(this.toJson(), saveFile))
        {
            this.dirty = false;
            return true;
        }

        return false;
    }

    public void loadFromFile()
    {
        Path configDir = ConfigUtils.getActiveConfigDirectory();
        Path saveFile = configDir.resolve(MaLiLibReference.MOD_ID).resolve("info_widgets.json");
        JsonUtils.loadFromFile(saveFile, this::fromJson);
    }
}
