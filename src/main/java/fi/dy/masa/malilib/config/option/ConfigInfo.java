package fi.dy.masa.malilib.config.option;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.util.StringUtils;

public interface ConfigInfo
{
    /**
     * Returns the type of this config. Used by the config GUI to determine what kind of control
     * to use for this config.
     * @return the type of this config
     */
    ConfigType getType();

    /**
     * Returns the name of this config option, used in the config files
     * @return the internal name of this config
     */
    String getName();

    /**
     * Returns the translation key for the localization file
     * @return
     */
    String getConfigNameTranslationKey();

    /**
     * Returns the display name used for this config on the config screens
     * @return
     */
    default String getDisplayName()
    {
        return StringUtils.translate(this.getConfigNameTranslationKey());
    }

    /**
     * Returns the raw translation key for the comment.
     * @return
     */
    @Nullable
    String getCommentTranslationKey();

    /**
     * Returns the comment displayed when hovering over the config name in the config GUI.
     * Newlines can be added with "\n". Can be null if there is no comment for this config.
     * @return the comment, or null if no comment has been set
     */
    @Nullable
    default String getComment()
    {
        String key = this.getCommentTranslationKey();
        return key != null ? StringUtils.translate(key) : null;
    }
}
