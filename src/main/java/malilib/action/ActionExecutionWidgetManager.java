package malilib.action;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import malilib.MaLiLibReference;
import malilib.config.util.ConfigUtils;
import malilib.gui.action.ActionWidgetScreenData;
import malilib.overlay.message.MessageDispatcher;
import malilib.util.BackupUtils;
import malilib.util.FileNameUtils;
import malilib.util.data.json.JsonUtils;

public class ActionExecutionWidgetManager
{
    public static final ActionExecutionWidgetManager INSTANCE = new ActionExecutionWidgetManager();

    protected final Map<String, ActionWidgetScreenData> widgetScreens = new HashMap<>();

    @Nullable
    public ActionWidgetScreenData getWidgetScreenData(String name)
    {
        return this.widgetScreens.get(name);
    }

    @Nullable
    public ActionWidgetScreenData getOrLoadWidgetScreenData(String name)
    {
        if (this.widgetScreens.containsKey(name) == false)
        {
            this.loadFromFile(name);
        }

        return this.getWidgetScreenData(name);
    }

    public void saveWidgetScreenData(String name, ActionWidgetScreenData data)
    {
        this.widgetScreens.put(name, data);
        this.saveDataToFile(name, data);
    }

    public void removeWidgetScreenData(String name)
    {
        this.widgetScreens.remove(name);
    }

    public void clear()
    {
        this.widgetScreens.clear();
    }

    public void saveAllLoadedToFile()
    {
        for (Map.Entry<String, ActionWidgetScreenData> entry : this.widgetScreens.entrySet())
        {
            this.saveDataToFile(entry.getKey(), entry.getValue());
        }
    }

    protected void loadDataFromJson(String name, JsonElement el)
    {
        ActionWidgetScreenData data = ActionWidgetScreenData.fromJson(el);

        if (data != null)
        {
            this.widgetScreens.put(name, data);
        }
    }

    protected boolean saveDataToFile(String name, ActionWidgetScreenData data)
    {
        name = FileNameUtils.generateSimpleSafeFileName(name);
        Path configDir = ConfigUtils.getActiveConfigDirectory();
        Path saveFile = configDir.resolve(MaLiLibReference.MOD_ID).resolve("action_screens").resolve(name + ".json");
        Path backupDir = configDir.resolve("backups").resolve(MaLiLibReference.MOD_ID).resolve("action_screens");

        if (BackupUtils.createRegularBackup(saveFile, backupDir))
        {
            return JsonUtils.writeJsonToFile(data.toJson(), saveFile);
        }

        return false;
    }

    protected void loadFromFile(String name)
    {
        final String safeName = FileNameUtils.generateSimpleSafeFileName(name);
        Path configDir = ConfigUtils.getActiveConfigDirectory();
        Path saveFile = configDir.resolve(MaLiLibReference.MOD_ID).resolve("action_screens").resolve(name + ".json");
        JsonUtils.loadFromFile(saveFile, (el) -> this.loadDataFromJson(safeName, el));
    }

    public static boolean createActionWidgetScreen(String arg)
    {
        ActionWidgetScreenData data = INSTANCE.getOrLoadWidgetScreenData(arg);

        if (data == null)
        {
            String name = FileNameUtils.generateSimpleSafeFileName(arg);
            INSTANCE.saveWidgetScreenData(name, ActionWidgetScreenData.createEmpty());
            MessageDispatcher.success("malilibdev.message.info.action_screen_created_by_name", name);
            return true;
        }
        else
        {
            MessageDispatcher.warning("malilibdev.message.error.action_screen_already_exists_by_name", arg);
            return false;
        }
    }
}
