package fi.dy.masa.malilib.gui.config;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.BaseConfig;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public abstract class BaseConfigGroup implements ConfigInfo
{
    protected final ModInfo modInfo;
    protected final String nameTranslationKey;
    protected final String commentTranslationKey;
    protected final Object[] commentArgs;
    protected ImmutableList<ConfigInfo> configs = ImmutableList.of();
    protected ImmutableList<String> searchStrings = ImmutableList.of();

    public BaseConfigGroup(ModInfo modInfo, String name, List<ConfigInfo> configs)
    {
        String modId = modInfo.getModId();

        this.modInfo = modInfo;
        this.nameTranslationKey = modId + ".config_group.name." + name;
        this.commentTranslationKey = modId + ".config_group.comment." + name;
        this.commentArgs = new Object[0];

        this.setConfigs(configs);
    }

    public BaseConfigGroup(ModInfo modInfo, String nameTranslationKey, String commentTranslationKey, Object... commentArgs)
    {
        this.modInfo = modInfo;
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

        this.searchStrings = builder.build();
        this.configs = ImmutableList.copyOf(configs);

        return this;
    }

    /**
     * Returns the list of configs contained within this group
     * @return
     */
    public ImmutableList<ConfigInfo> getConfigs()
    {
        return this.configs;
    }

    @Override
    public ModInfo getModInfo()
    {
        return this.modInfo;
    }

    @Override
    public String getName()
    {
        return this.getDisplayName();
    }

    @Override
    public String getDisplayName()
    {
        return BaseConfig.getDefaultDisplayName(this, this.nameTranslationKey);
    }

    @Nullable
    @Override
    public String getComment()
    {
        return StringUtils.translate(this.commentTranslationKey, this.commentArgs);
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
}
