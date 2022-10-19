package malilib.config.group;

import java.util.List;
import malilib.config.option.ConfigInfo;
import malilib.config.option.NestedConfig;
import malilib.util.data.ModInfo;

public class ExpandableConfigGroup extends BaseConfigGroup
{
    protected boolean isExpanded;

    public ExpandableConfigGroup(ModInfo modInfo, String name, List<ConfigInfo> configs)
    {
        super(modInfo, name, configs);
    }

    public ExpandableConfigGroup(ModInfo modInfo, String name, String nameTranslationKey, String commentTranslationKey, Object... commentArgs)
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
    @SuppressWarnings("unchecked")
    public <C extends ConfigInfo> void addNestedOptionsToList(List<C> list, int nestingLevel)
    {
        if (this.isExpanded)
        {
            for (ConfigInfo config : this.getConfigs())
            {
                list.add((C) new NestedConfig(config, nestingLevel));
                config.addNestedOptionsToList(list, nestingLevel + 1);
            }
        }
    }
}
