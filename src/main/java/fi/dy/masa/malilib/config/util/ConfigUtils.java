package fi.dy.masa.malilib.config.util;

import java.util.ArrayList;
import java.util.Comparator;
import net.minecraft.util.text.TextFormatting;
import fi.dy.masa.malilib.config.option.ConfigInfo;

public class ConfigUtils
{
    public static void sortConfigsByDisplayName(ArrayList<ConfigInfo> configs)
    {
        configs.sort(Comparator.comparing((c) -> TextFormatting.getTextWithoutFormattingCodes(c.getDisplayName())));
    }
}
