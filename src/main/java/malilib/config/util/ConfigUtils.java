package malilib.config.util;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;

import net.minecraft.util.text.TextFormatting;

import malilib.MaLiLibConfigs;
import malilib.action.ActionContext;
import malilib.action.ActionExecutionWidgetManager;
import malilib.config.ConfigManagerImpl;
import malilib.config.group.BaseConfigGroup;
import malilib.config.group.BaseConfigGroup.ConfigGroupFactory;
import malilib.config.group.ExpandableConfigGroup;
import malilib.config.option.ConfigInfo;
import malilib.gui.config.ConfigTab;
import malilib.input.ActionResult;
import malilib.input.CustomHotkeyManager;
import malilib.input.Hotkey;
import malilib.overlay.message.MessageDispatcher;
import malilib.registry.Registry;
import malilib.render.overlay.OverlayRendererContainer;
import malilib.util.FileUtils;
import malilib.util.ListUtils;
import malilib.util.data.ConfigOnTab;
import malilib.util.data.ModInfo;

public class ConfigUtils
{
    public static Path getConfigDirectory()
    {
        return FileUtils.getMinecraftDirectory().resolve("config");
    }

    /**
     * Returns a Path to a directory by the given name inside the main config directory.
     * Usually the given name would be the modId of the mod calling this.
     */
    public static Path getConfigDirectory(String directoryName)
    {
        return getConfigDirectory().resolve(directoryName);
    }

    /**
     * Returns a Path to a directory by the given name inside the main config directory.
     * Usually the given name would be the modId of the mod calling this.
     * Tries to create the directory and any missing parent directories, if it doesn't exist yet.
     */
    public static Path createAndGetConfigDirectory(String directoryName)
    {
        Path dir = getConfigDirectory(directoryName);
        FileUtils.createDirectoriesIfMissing(dir);
        return dir;
    }

    /**
     * @return The currently active config directory. This takes into account a possible active config profile.
     */
    public static Path getActiveConfigDirectory()
    {
        String profile = MaLiLibConfigs.Internal.ACTIVE_CONFIG_PROFILE.getValue();
        return getActiveConfigDirectory(profile);
    }

    /**
     * @return The currently active config directory for the given config profile.
     */
    public static Path getActiveConfigDirectory(String profile)
    {
        Path baseConfigDir = getConfigDirectory();

        if (StringUtils.isBlank(profile) == false)
        {
            try
            {
                return baseConfigDir.resolve("config_profiles").resolve(profile);
            }
            catch (InvalidPathException ignore) {}
        }

        return baseConfigDir;
    }

    /**
     * Sort the given list of configs by the config's display name,
     * stripping away any vanilla text formatting codes first.
     * Note: The input list must be modifiable!
     */
    public static List<ConfigInfo> sortConfigsInPlaceByDisplayName(List<ConfigInfo> configs)
    {
        configs.sort(Comparator.comparing((c) -> TextFormatting.getTextWithoutFormattingCodes(c.getDisplayName())));
        return configs;
    }

    /**
     * Removes the given configs from the given list,
     * and returns them as an expandable config group.
     */
    public static BaseConfigGroup extractOptionsToExpandableGroup(ArrayList<ConfigInfo> originalList,
                                                                  ModInfo mod,
                                                                  String groupName,
                                                                  ConfigInfo... toExtract)
    {
        return extractOptionsToGroup(originalList, mod, groupName, ExpandableConfigGroup::new, toExtract);
    }

    /**
     * Removes the given configs from the given list,
     * and returns them as a config group using the provided factory.
     */
    public static BaseConfigGroup extractOptionsToGroup(ArrayList<ConfigInfo> originalList,
                                                        ModInfo mod,
                                                        String groupName,
                                                        ConfigGroupFactory factory,
                                                        ConfigInfo... toExtract)
    {
        List<ConfigInfo> extractedList = Arrays.asList(toExtract);

        originalList.removeAll(extractedList);
        ConfigUtils.sortConfigsInPlaceByDisplayName(extractedList);

        return factory.create(mod, groupName, extractedList);
    }

    /**
     * Removes the configs matching the given Predicate from the given list,
     * and returns them as an expandable config group.
     */
    public static BaseConfigGroup extractOptionsToExpandableGroup(ArrayList<ConfigInfo> originalList,
                                                                  ModInfo mod,
                                                                  String groupName,
                                                                  Predicate<ConfigInfo> extractTest)
    {
        return extractOptionsToGroup(originalList, mod, groupName, extractTest, ExpandableConfigGroup::new);
    }

    /**
     * Removes the configs matching the given Predicate from the given list,
     * and returns them as a config group using the provided factory.
     */
    public static BaseConfigGroup extractOptionsToGroup(ArrayList<ConfigInfo> originalList,
                                                        ModInfo mod,
                                                        String groupName,
                                                        Predicate<ConfigInfo> extractTest,
                                                        ConfigGroupFactory factory)
    {
        ArrayList<ConfigInfo> extractedList = new ArrayList<>();

        ListUtils.extractEntriesToSecondList(originalList, extractedList, extractTest, true);
        originalList.removeAll(extractedList);
        ConfigUtils.sortConfigsInPlaceByDisplayName(extractedList);

        return factory.create(mod, groupName, extractedList);
    }

    /**
     * Creates a map of all the configs on the provided config tabs, using
     * an identifier key that is in the form "modId.tabName.configName".
     */
    public static Map<String, ConfigOnTab> getConfigIdToConfigMapFromTabs(List<? extends ConfigTab> tabs)
    {
        Map<String, ConfigOnTab> map = new HashMap<>();

        for (ConfigTab tab : tabs)
        {
            String modCategory = tab.getModInfo().getModId() + "." + tab.getName() + ".";

            for (ConfigOnTab config : tab.getTabbedExpandedConfigs())
            {
                String id = modCategory + config.getConfig().getName();
                map.put(id, config);
            }
        }

        return map;
    }

    public static void resetAllKeybindSettingsToDefaults(List<? extends Hotkey> hotkeys)
    {
        hotkeys.forEach(h -> h.getKeyBind().resetSettingsToDefaults());
    }

    /**
     * Loads all configs and other systems from file.<br>
     * <b>Note:</b> You are not supposed to call this from mod code!<br>
     * This is a wrapper for loading all the different systems at once.
     */
    public static void loadAllConfigsFromFile()
    {
        Registry.ICON.loadFromFile();
        ((ConfigManagerImpl) Registry.CONFIG_MANAGER).loadAllConfigs();
        Registry.ACTION_REGISTRY.loadFromFile();
        CustomHotkeyManager.INSTANCE.loadFromFile();
        Registry.INFO_WIDGET_MANAGER.loadFromFile();
        Registry.MESSAGE_REDIRECT_MANAGER.loadFromFile();
        Registry.HOTKEY_MANAGER.updateUsedKeys();
    }

    /**
     * Saves all configs and other systems to file.<br>
     * <b>Note:</b> You are not supposed to call this from mod code!<br>
     * This is a wrapper for saving all the different systems at once.
     */
    public static void saveAllConfigsToFileIfDirty()
    {
        ((ConfigManagerImpl) Registry.CONFIG_MANAGER).saveIfDirty();
        Registry.INFO_WIDGET_MANAGER.saveToFileIfDirty();
        Registry.MESSAGE_REDIRECT_MANAGER.saveToFileIfDirty();
        OverlayRendererContainer.INSTANCE.saveToFile(false);
        ActionExecutionWidgetManager.INSTANCE.clear();

        // These should always already be saved when closing the corresponding config screens
        Registry.ICON.saveToFileIfDirty();
        Registry.ACTION_REGISTRY.saveToFileIfDirty();
        CustomHotkeyManager.INSTANCE.saveToFileIfDirty();
    }

    private static void copyConfigsIfProfileNotExist(String profile)
    {
        if (StringUtils.isBlank(profile) == false)
        {
            Path dir = getActiveConfigDirectory();

            if (FileUtils.createDirectoriesIfMissing(dir))
            {
                Registry.ICON.saveToFile();
                ((ConfigManagerImpl) Registry.CONFIG_MANAGER).saveAllConfigs();
                Registry.ACTION_REGISTRY.saveToFile();
                ActionExecutionWidgetManager.INSTANCE.saveAllLoadedToFile();
                CustomHotkeyManager.INSTANCE.saveToFile();
                Registry.INFO_WIDGET_MANAGER.saveToFile();
                Registry.MESSAGE_REDIRECT_MANAGER.saveToFile();
            }
        }
    }

    public static ActionResult switchConfigProfile(ActionContext ctx, String profile)
    {
        String current = MaLiLibConfigs.Internal.ACTIVE_CONFIG_PROFILE.getValue();

        if ("default".equals(profile))
        {
            profile = "";
        }

        if (current.equals(profile) == false)
        {
            saveAllConfigsToFileIfDirty();
            MaLiLibConfigs.Internal.ACTIVE_CONFIG_PROFILE.setValue(profile);
            copyConfigsIfProfileNotExist(profile);
            loadAllConfigsFromFile();

            MessageDispatcher.success("malilib.message.info.switched_config_profile", profile);

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    public static ActionResult loadAllConfigsFromFileAction(ActionContext ctx)
    {
        loadAllConfigsFromFile();
        MessageDispatcher.success("malilib.message.info.loaded_all_configs_from_file");
        return ActionResult.SUCCESS;
    }
}
