package fi.dy.masa.malilib.gui.config;

import java.util.List;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.util.data.ModInfo;

public class PopupConfigGroup extends BaseConfigGroup
{
    public PopupConfigGroup(ModInfo modInfo, String name, List<ConfigInfo> configs)
    {
        super(modInfo, name, configs);
    }

    public PopupConfigGroup(ModInfo modInfo, String name, String nameTranslationKey, String commentTranslationKey, Object... commentArgs)
    {
        super(modInfo, name, nameTranslationKey, commentTranslationKey, commentArgs);
    }
}
