package malilib.config.group;

import java.util.List;
import javax.annotation.Nullable;

import malilib.config.option.ConfigInfo;
import malilib.gui.config.ConfigTab;
import malilib.util.data.ConfigOnTab;
import malilib.util.data.ModInfo;

public class ExpandableConfigGroup extends BaseConfigGroup
{
    protected boolean isExpanded;

    public ExpandableConfigGroup(ModInfo modInfo, String name, List<ConfigInfo> configs)
    {
        super(modInfo, name, configs);
    }

    public ExpandableConfigGroup(ModInfo modInfo, String name, String nameTranslationKey,
                                 @Nullable String commentTranslationKey, Object... commentArgs)
    {
        super(modInfo, name, nameTranslationKey, commentTranslationKey, commentArgs);
    }

    public boolean isExpanded()
    {
        return this.isExpanded;
    }

    public boolean toggleIsExpanded()
    {
        this.isExpanded = ! this.isExpanded;
        return this.isExpanded;
    }

    @Override
    public void addNestedOptionsToList(List<ConfigOnTab> list, ConfigTab tab, int nestingLevel, boolean expandAlways)
    {
        if (this.isExpanded || expandAlways)
        {
            for (ConfigInfo config : this.getConfigs())
            {
                list.add(new ConfigOnTab(tab, config, nestingLevel));
                config.addNestedOptionsToList(list, tab, nestingLevel + 1, expandAlways);
            }
        }
    }
}
