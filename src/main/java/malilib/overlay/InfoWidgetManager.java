package malilib.overlay;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
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
    protected final List<InfoRendererWidget> widgets = new ArrayList<>();
    protected final InfoOverlay infoOverlay;
    protected boolean dirty;

    public InfoWidgetManager(InfoOverlay infoOverlay)
    {
        this.infoOverlay = infoOverlay;
    }

    public void addWidget(InfoRendererWidget widget)
    {
        if (this.widgets.contains(widget) == false)
        {
            this.widgets.add(widget);
            this.infoOverlay.getOrCreateInfoArea(widget.getScreenLocation()).addWidget(widget);
            this.dirty = true;
        }
    }

    public void removeWidget(InfoRendererWidget widget)
    {
        this.widgets.remove(widget);
        this.infoOverlay.getOrCreateInfoArea(widget.getScreenLocation()).removeWidget(widget);
        widget.invalidate();
        this.dirty = true;
    }

    public <WIDGET extends InfoRendererWidget> List<WIDGET> getAllWidgetsOfExactType(Class<WIDGET> clazz)
    {
        ArrayList<WIDGET> list = new ArrayList<>();

        for (InfoRendererWidget widget : this.widgets)
        {
            if (widget.getClass() == clazz)
            {
                list.add(clazz.cast(widget));
            }
        }

        return list;
    }

    public <WIDGET extends InfoRendererWidget> List<WIDGET> getAllWidgetsExtendingType(Class<WIDGET> clazz)
    {
        ArrayList<WIDGET> list = new ArrayList<>();

        for (InfoRendererWidget widget : this.widgets)
        {
            if (clazz.isInstance(widget))
            {
                list.add(clazz.cast(widget));
            }
        }

        return list;
    }

    public void removeMatchingWidgets(Predicate<InfoRendererWidget> filter)
    {
        Iterator<InfoRendererWidget> it = this.widgets.iterator();

        while (it.hasNext())
        {
            InfoRendererWidget widget = it.next();

            if (filter.test(widget))
            {
                this.infoOverlay.getOrCreateInfoArea(widget.getScreenLocation()).removeWidget(widget);
                it.remove();
            }
        }

        this.dirty = true;
    }

    public void clearWidgets()
    {
        for (InfoRendererWidget widget : this.widgets)
        {
            this.infoOverlay.getOrCreateInfoArea(widget.getScreenLocation()).removeWidget(widget);
        }

        this.widgets.clear();
        this.dirty = true;
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();

        for (InfoRendererWidget widget : this.widgets)
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
            this.widgets.add(widget);
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
