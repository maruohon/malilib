package fi.dy.masa.malilib.config.option;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public abstract class CommonDescription implements BaseInfo
{
    protected final String name;
    protected String nameTranslationKey;
    @Nullable protected ModInfo modInfo;
    @Nullable protected String commentTranslationKey;
    protected Object[] commentArgs = new Object[0];

    public CommonDescription(String name, @Nullable ModInfo modInfo)
    {
        this(name);

        this.modInfo = modInfo;
    }

    public CommonDescription(String name)
    {
        this.name = name;
        this.nameTranslationKey = name;
    }

    public CommonDescription(String name, String nameTranslationKey,
                             @Nullable String commentTranslationKey, Object... commentArgs)
    {
        this.name = name;
        this.nameTranslationKey = nameTranslationKey;
        this.commentTranslationKey = commentTranslationKey;
        this.commentArgs = commentArgs;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getDisplayName()
    {
        return BaseConfigOption.getDefaultDisplayName(this.getName(), this.nameTranslationKey);
    }

    @Override
    @Nullable
    public ModInfo getModInfo()
    {
        return this.modInfo;
    }

    @Override
    @Nullable
    public String getComment()
    {
        if (this.commentTranslationKey != null)
        {
            return StringUtils.translate(this.commentTranslationKey, this.commentArgs);
        }

        return null;
    }

    public String getNameTranslationKey()
    {
        return this.nameTranslationKey;
    }

    @Nullable
    public String getCommentTranslationKey()
    {
        return this.commentTranslationKey;
    }

    public void setModInfo(@Nullable ModInfo modInfo)
    {
        this.modInfo = modInfo;
    }

    public void setNameTranslationKey(String translationKey)
    {
        this.nameTranslationKey = translationKey;
    }

    public void setCommentTranslationKey(@Nullable String translationKey)
    {
        this.commentTranslationKey = translationKey;
    }

    public void setCommentArgs(Object... args)
    {
        this.commentArgs = args;
    }
}
