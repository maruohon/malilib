package fi.dy.masa.malilib.action;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.util.ConfigUtils;
import fi.dy.masa.malilib.gui.action.ActionExecutionWidget;
import fi.dy.masa.malilib.util.JsonUtils;

public class ActionExecutionWidgetManager
{
    public static final ActionExecutionWidgetManager INSTANCE = new ActionExecutionWidgetManager();

    protected final Map<String, ImmutableList<ActionExecutionWidget>> widgetMap = new HashMap<>();
    protected boolean dirty;

    @Nullable
    public ImmutableList<ActionExecutionWidget> getWidgetList(String name)
    {
        return this.widgetMap.get(name);
    }

    public void putWidgetList(String name, ImmutableList<ActionExecutionWidget> list)
    {
        this.widgetMap.put(name, list);
        this.dirty = true;
    }

    public void removeWidgetList(String name)
    {
        this.widgetMap.remove(name);
        this.dirty = true;
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        for (Map.Entry<String, ImmutableList<ActionExecutionWidget>> entry : this.widgetMap.entrySet())
        {
            JsonArray arr = new JsonArray();

            for (ActionExecutionWidget widget : entry.getValue())
            {
                arr.add(widget.toJson());
            }

            obj.add(entry.getKey(), arr);
        }

        return obj;
    }

    public void fromJson(JsonElement el)
    {
        if (el.isJsonObject() == false)
        {
            return;
        }

        JsonObject obj = el.getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : obj.entrySet())
        {
            JsonElement e = entry.getValue();

            if (e.isJsonArray())
            {
                ImmutableList.Builder<ActionExecutionWidget> builder = ImmutableList.builder();
                JsonArray arr = e.getAsJsonArray();
                int size = arr.size();

                for (int i = 0; i < size; ++i)
                {
                    JsonElement ae = arr.get(i);

                    if (ae.isJsonObject())
                    {
                        ActionExecutionWidget widget = ActionExecutionWidget.fromJson(ae.getAsJsonObject());

                        if (widget != null)
                        {
                            builder.add(widget);
                        }
                    }
                }

                this.widgetMap.put(entry.getKey(), builder.build());
            }
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
        File dir = ConfigUtils.getActiveConfigDirectory();
        File backupDir = new File(dir, "config_backups");
        File saveFile = new File(dir, MaLiLibReference.MOD_ID + "_action_widgets.json");
        boolean antiDuplicate = MaLiLibConfigs.Generic.CONFIG_BACKUP_ANTI_DUPLICATE.getBooleanValue();

        return JsonUtils.saveToFile(dir, backupDir, saveFile, 10, antiDuplicate, this::toJson);
    }

    public void loadFromFile()
    {
        File dir = ConfigUtils.getActiveConfigDirectory();
        JsonUtils.loadFromFile(dir, MaLiLibReference.MOD_ID + "_action_widgets.json", this::fromJson);
    }
}
