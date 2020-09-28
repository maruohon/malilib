package fi.dy.masa.malilib.gui.config;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class BaseConfigGroup implements ConfigInfo
{
    protected final String nameTranslationKey;
    protected final String commentTranslationKey;
    protected final Object[] commentArgs;
    protected ImmutableList<ConfigInfo> configs = ImmutableList.of();
    protected ImmutableList<String> searchStrings = ImmutableList.of();

    public BaseConfigGroup(String name, String modId, List<ConfigInfo> configs)
    {
        this(modId + ".config_group.name." + name, modId + ".config_group.comment." + name);

        this.setConfigs(configs);
    }

    public BaseConfigGroup(String nameTranslationKey, String commentTranslationKey, Object... commentArgs)
    {
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
    public String getName()
    {
        return StringUtils.translate(this.getConfigNameTranslationKey());
    }

    @Override
    public String getConfigNameTranslationKey()
    {
        return this.nameTranslationKey;
    }

    @Override
    @Nullable
    public String getCommentTranslationKey()
    {
        return this.commentTranslationKey;
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
