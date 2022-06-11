package fi.dy.masa.malilib.gui.icon;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.util.ConfigUtils;
import fi.dy.masa.malilib.util.BackupUtils;
import fi.dy.masa.malilib.util.data.json.JsonUtils;

public class IconRegistry
{
    protected final List<Icon> modIcons = new ArrayList<>();
    protected final List<Icon> userIcons = new ArrayList<>();
    protected final Map<String, Icon> iconMap = new HashMap<>();
    protected ImmutableList<Icon> allIcons = ImmutableList.of();
    protected ImmutableList<Icon> userIconsImmutable = ImmutableList.of();
    protected boolean dirty;
    protected boolean needsRebuild = true;

    public void registerModIcon(Icon icon)
    {
        if (this.modIcons.contains(icon) == false)
        {
            this.modIcons.add(icon);
            this.needsRebuild = true;
        }
    }

    public void registerUserIcon(Icon icon)
    {
        if (this.userIcons.contains(icon) == false)
        {
            this.userIcons.add(icon);
            this.markDirty();
        }
    }

    public void unregisterModIcon(Icon icon)
    {
        this.modIcons.remove(icon);
        this.needsRebuild = true;
    }

    public void unregisterUserIcon(Icon icon)
    {
        this.userIcons.remove(icon);
        this.markDirty();
    }

    protected void markDirty()
    {
        this.dirty = true;
        this.needsRebuild = true;
    }

    public ImmutableList<Icon> getAllIcons()
    {
        this.updateLists();
        return this.allIcons;
    }

    public ImmutableList<Icon> getUserIcons()
    {
        this.updateLists();
        return this.userIconsImmutable;
    }

    public Icon getIconByKeyOrEmpty(String key)
    {
        this.updateLists();
        return this.iconMap.getOrDefault(key, DefaultIcons.EMPTY);
    }

    @Nullable
    public Icon getIconByKeyOrNull(String key)
    {
        this.updateLists();
        return this.iconMap.get(key);
    }

    protected void updateLists()
    {
        if (this.needsRebuild)
        {
            List<Icon> icons = new ArrayList<>(this.userIcons);
            icons.sort(Comparator.comparing(i -> i.getTexture().toString()));

            this.userIconsImmutable = ImmutableList.copyOf(icons);

            icons.addAll(this.modIcons);
            icons.sort(Comparator.comparing(i -> i.getTexture().toString()));

            this.allIcons = ImmutableList.copyOf(icons);

            this.iconMap.clear();

            for (Icon icon : this.allIcons)
            {
                String key = getKeyForIcon(icon);
                this.iconMap.put(key, icon);
            }

            this.needsRebuild = false;
        }
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();

        for (Icon icon : this.userIcons)
        {
            arr.add(icon.toJson());
        }

        obj.add("custom_icons", arr);

        return obj;
    }

    protected void loadDataFromJson(JsonElement el)
    {
        if (el.isJsonObject() == false)
        {
            return;
        }

        this.userIcons.clear();
        this.needsRebuild = true;
        this.dirty = false;

        JsonObject obj = el.getAsJsonObject();
        JsonUtils.readArrayElementsIfExists(obj, "custom_icons", this::readAndAddIcon);
    }

    protected void readAndAddIcon(JsonElement el)
    {
        Icon icon = Icon.fromJson(el);

        if (icon != null && this.userIcons.contains(icon) == false)
        {
            this.userIcons.add(icon);
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
        Path saveFile = configDir.resolve(MaLiLibReference.MOD_ID).resolve("custom_icons.json");
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
        Path saveFile = configDir.resolve(MaLiLibReference.MOD_ID).resolve("custom_icons.json");
        JsonUtils.loadFromFile(saveFile, this::loadDataFromJson);
    }

    public static String getKeyForIcon(Icon icon)
    {
        return String.format("%s_%d_%d_%d_%d", icon.getTexture().toString(),
                             icon.getU(), icon.getV(), icon.getWidth(), icon.getHeight());
    }
}
