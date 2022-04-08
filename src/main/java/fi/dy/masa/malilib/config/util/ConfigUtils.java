package fi.dy.masa.malilib.config.util;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import com.mumfrey.liteloader.core.LiteLoader;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.util.text.TextFormatting;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.action.ActionContext;
import fi.dy.masa.malilib.action.ActionExecutionWidgetManager;
import fi.dy.masa.malilib.config.ConfigManagerImpl;
import fi.dy.masa.malilib.config.group.ExpandableConfigGroup;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.config.ConfigTab;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.input.CustomHotkeyManager;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.render.overlay.OverlayRendererContainer;
import fi.dy.masa.malilib.util.ListUtils;
import fi.dy.masa.malilib.util.data.ConfigOnTab;
import fi.dy.masa.malilib.util.data.ModInfo;

public class ConfigUtils
{
    public static File getConfigDirectory()
    {
        return LiteLoader.getCommonConfigFolder();
    }

    public static Path getConfigDirectoryPath()
    {
        return getConfigDirectory().toPath();
    }

    /**
     * @return The currently active config directory. This takes into account a possible active config profile.
     */
    public static File getActiveConfigDirectory()
    {
        String profile = MaLiLibConfigs.Internal.ACTIVE_CONFIG_PROFILE.getValue();
        return getActiveConfigDirectory(profile);
    }

    /**
     * @return The currently active config directory for the given config profile.
     */
    public static File getActiveConfigDirectory(String profile)
    {
        Path baseConfigDir = getConfigDirectoryPath();

        if (StringUtils.isBlank(profile) == false)
        {
            try
            {
                return baseConfigDir.resolve("config_profiles").resolve(profile).toFile();
            }
            catch (InvalidPathException ignore) {}
        }

        return baseConfigDir.toFile();
    }

    /**
     * @return The currently active config directory. This takes into account a possible active config profile.
     */
    public static Path getActiveConfigDirectoryPath()
    {
        String profile = MaLiLibConfigs.Internal.ACTIVE_CONFIG_PROFILE.getValue();
        return getActiveConfigDirectoryPath(profile);
    }

    /**
     * @return The currently active config directory for the given config profile.
     */
    public static Path getActiveConfigDirectoryPath(String profile)
    {
        Path baseConfigDir = getConfigDirectoryPath();

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
     * stripping away any vanilla text formatting codes first
     */
    public static void sortConfigsByDisplayName(List<ConfigInfo> configs)
    {
        configs.sort(Comparator.comparing((c) -> TextFormatting.getTextWithoutFormattingCodes(c.getDisplayName())));
    }

    /**
     * @return The full list of config options for the given list of configs from a config tab.
     * The normal base list gets appended with any possible extra options that an extension mod
     * wants to show on the same tab with the parent mod's config options.
     */
    public static List<? extends ConfigInfo> getExtendedList(List<? extends ConfigInfo> baseList)
    {
        return Registry.CONFIG_TAB_EXTENSION.getExtendedList(baseList, MaLiLibConfigs.Generic.SORT_EXTENSION_MOD_OPTIONS.getBooleanValue());
    }

    /**
     * Removes the given configs from the given list,
     * and returns them as an expandable config group.
     */
    public static ExpandableConfigGroup extractOptionsToGroup(ArrayList<ConfigInfo> originalList,
                                                              ModInfo mod,
                                                              String groupName,
                                                              ConfigInfo... toExtract)
    {
        List<ConfigInfo> extractedList = Arrays.asList(toExtract);

        originalList.removeAll(extractedList);
        ConfigUtils.sortConfigsByDisplayName(extractedList);

        return new ExpandableConfigGroup(mod, groupName, extractedList);
    }

    /**
     * Removes the configs matching the given Predicate from the given list,
     * and returns them as an expandable config group.
     */
    public static ExpandableConfigGroup extractOptionsToGroup(ArrayList<ConfigInfo> originalList,
                                                              ModInfo mod,
                                                              String groupName,
                                                              Predicate<ConfigInfo> extractTest)
    {
        ArrayList<ConfigInfo> extractedList = new ArrayList<>();

        ListUtils.extractEntriesToSecondList(originalList, extractedList, extractTest, true);
        originalList.removeAll(extractedList);
        ConfigUtils.sortConfigsByDisplayName(extractedList);

        return new ExpandableConfigGroup(mod, groupName, extractedList);
    }

    /**
     * Creates a map of all the configs on the provided config tabs, using
     * an identifier key that is in the form "modId.tabName.configName".
     */
    public static Map<String, ConfigOnTab> getConfigIdToConfigMapFromTabs(List<ConfigTab> tabs)
    {
        Map<String, ConfigOnTab> map = new HashMap<>();

        for (ConfigTab tab : tabs)
        {
            ModInfo mod = tab.getModInfo();
            String modCategory = mod.getModId() + "." + tab.getName() + ".";

            for (ConfigInfo config : tab.getExpandedConfigs())
            {
                String id = modCategory + config.getName();
                map.put(id, new ConfigOnTab(tab, config));
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
            File dir = getActiveConfigDirectory();

            if (dir.exists() == false && dir.mkdirs())
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
