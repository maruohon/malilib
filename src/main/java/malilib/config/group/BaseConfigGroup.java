package malilib.config.group;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableList;
import malilib.MaLiLibConfigs;
import malilib.config.option.CommonDescription;
import malilib.config.option.ConfigInfo;
import malilib.config.util.ConfigUtils;
import malilib.util.data.ModInfo;

public abstract class BaseConfigGroup extends CommonDescription implements ConfigInfo
{
    protected ImmutableList<ConfigInfo> configs = ImmutableList.of();
    protected ImmutableList<String> searchStrings = ImmutableList.of();

    public BaseConfigGroup(ModInfo modInfo, String name, List<ConfigInfo> configs)
    {
        super(name, modInfo);

        String modId = modInfo.getModId();

        this.nameTranslationKey = modId + ".config_group.name." + name;
        this.commentTranslationKey = modId + ".config_group.comment." + name;

        this.setConfigs(configs);
    }

    public BaseConfigGroup(ModInfo modInfo, String name, String nameTranslationKey,
                           String commentTranslationKey, Object... commentArgs)
    {
        super(name, modInfo);

        this.nameTranslationKey = nameTranslationKey;
        this.commentTranslationKey = commentTranslationKey;
        this.commentArgs = commentArgs;
    }

    /**
     * Sets the list of contained configs, overriding any old values
     */
    public BaseConfigGroup setConfigs(List<ConfigInfo> configs)
    {
        ImmutableList.Builder<String> builder = ImmutableList.builder();

        for (ConfigInfo config : configs)
        {
            builder.addAll(config.getSearchStrings());
        }

        builder.add(this.getDisplayName());
        this.searchStrings = builder.build();
        this.configs = ImmutableList.copyOf(configs);

        return this;
    }

    /**
     * @return the list of configs contained within this group
     */
    public ImmutableList<ConfigInfo> getConfigs()
    {
        if (MaLiLibConfigs.Generic.SORT_CONFIGS_BY_NAME.getBooleanValue())
        {
            ArrayList<ConfigInfo> list = new ArrayList<>(this.configs);
            ConfigUtils.sortConfigsByDisplayName(list);
            return ImmutableList.copyOf(list);
        }

        return this.configs;
    }

    @Override
    public List<String> getSearchStrings()
    {
        return this.searchStrings;
    }

    @Override
    public boolean isModified()
    {
        for (ConfigInfo config : this.configs)
        {
            if (config.isModified())
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void resetToDefault()
    {
        for (ConfigInfo config : this.configs)
        {
            config.resetToDefault();
        }
    }

    public interface ConfigGroupFactory
    {
        BaseConfigGroup create(ModInfo modInfo, String name, List<ConfigInfo> configs);
    }
}
