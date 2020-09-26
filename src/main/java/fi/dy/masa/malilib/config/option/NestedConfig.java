package fi.dy.masa.malilib.config.option;

import javax.annotation.Nullable;

/**
 * This is a wrapper used for the config screens, to embed the nesting level information
 * of configs contained within a ConfigGroup, without having to add that data to the ConfigInfo interface.
 */
public class NestedConfig implements ConfigInfo
{
    protected final ConfigInfo config;
    protected final int nestinglevel;

    public NestedConfig(ConfigInfo config, int nestinglevel)
    {
        this.config = config;
        this.nestinglevel = nestinglevel;
    }

    public ConfigInfo getConfig()
    {
        return this.config;
    }

    public int getNestingLevel()
    {
        return this.nestinglevel;
    }

    @Override
    public String getName()
    {
        return this.config.getName();
    }

    @Override
    public String getConfigNameTranslationKey()
    {
        return this.config.getConfigNameTranslationKey();
    }

    @Nullable
    @Override
    public String getCommentTranslationKey()
    {
        return this.config.getCommentTranslationKey();
    }

    @Override
    public boolean isModified()
    {
        return this.config.isModified();
    }

    @Override
    public void resetToDefault()
    {
        this.config.resetToDefault();
    }
}
