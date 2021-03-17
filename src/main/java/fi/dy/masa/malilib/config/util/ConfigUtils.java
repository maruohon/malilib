package fi.dy.masa.malilib.config.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.text.TextFormatting;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.config.ConfigTab;
import fi.dy.masa.malilib.util.data.ConfigOnTab;
import fi.dy.masa.malilib.util.data.ModInfo;

public class ConfigUtils
{
    public static void sortConfigsByDisplayName(ArrayList<ConfigInfo> configs)
    {
        configs.sort(Comparator.comparing((c) -> TextFormatting.getTextWithoutFormattingCodes(c.getDisplayName())));
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
}
