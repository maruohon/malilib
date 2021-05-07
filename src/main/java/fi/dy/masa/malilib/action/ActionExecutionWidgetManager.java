package fi.dy.masa.malilib.action;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.util.ConfigUtils;
import fi.dy.masa.malilib.gui.action.ActionWidgetScreenData;
import fi.dy.masa.malilib.overlay.message.MessageUtils;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;

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
        name = FileUtils.generateSimpleSafeFileName(name);
        File configDir = ConfigUtils.getActiveConfigDirectory();
        File backupDir = configDir.toPath().resolve("config_backups").resolve(MaLiLibReference.MOD_ID).resolve("action_screens").toFile();
        Path saveDir = configDir.toPath().resolve(MaLiLibReference.MOD_ID).resolve("action_screens");
        File saveFile = saveDir.resolve(name + ".json").toFile();
        boolean antiDuplicate = MaLiLibConfigs.Generic.CONFIG_BACKUP_ANTI_DUPLICATE.getBooleanValue();

        return JsonUtils.saveToFile(saveDir.toFile(), backupDir, saveFile, 20, antiDuplicate, data::toJson);
    }

    protected void loadFromFile(String name)
    {
        final String safeName = FileUtils.generateSimpleSafeFileName(name);
        File configDir = ConfigUtils.getActiveConfigDirectory();
        Path saveDir = configDir.toPath().resolve(MaLiLibReference.MOD_ID).resolve("action_screens");
        JsonUtils.loadFromFile(saveDir.toFile(), name + ".json", (el) -> this.loadDataFromJson(safeName, el));
    }

    public static boolean createActionWidgetScreen(String arg)
    {
        ActionWidgetScreenData data = INSTANCE.getOrLoadWidgetScreenData(arg);

        if (data == null)
        {
            String name = FileUtils.generateSimpleSafeFileName(arg);
            INSTANCE.saveWidgetScreenData(name, ActionWidgetScreenData.createEmpty());
            MessageUtils.success("malilib.message.action_screen_created_by_name", name);
            return true;
        }
        else
        {
            MessageUtils.warning("malilib.message.error.action_screen_already_exists_by_name", arg);
            return false;
        }
    }
}
