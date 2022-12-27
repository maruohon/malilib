package malilib.config.option;

import java.util.Optional;
import javax.annotation.Nullable;

import malilib.util.StringUtils;
import malilib.util.data.ModInfo;

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
        return getDefaultDisplayName(this.getName(), this.nameTranslationKey);
    }

    @Override
    @Nullable
    public ModInfo getModInfo()
    {
        return this.modInfo;
    }

    @Override
    public Optional<String> getComment()
    {
        if (org.apache.commons.lang3.StringUtils.isBlank(this.commentTranslationKey) == false)
        {
            return Optional.of(StringUtils.translate(this.commentTranslationKey, this.commentArgs));
        }

        return Optional.empty();
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

    @Override
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

    /**
     * Sets the comment translation key and args if a translation exists for the provided key
     */
    public void setCommentIfTranslationExists(String key, Object... args)
    {
        String translated = StringUtils.translate(key, args);

        if (key.equals(translated) == false)
        {
            this.setCommentTranslationKey(key);
            this.setCommentArgs(args);
        }
    }

    public static String getDefaultDisplayName(String baseName, String nameTranslationKey)
    {
        String translatedName = StringUtils.translate(nameTranslationKey);

        // If there is no translation for the config name, then show the actual base name
        return translatedName.equals(nameTranslationKey) ? baseName : translatedName;
    }
}
