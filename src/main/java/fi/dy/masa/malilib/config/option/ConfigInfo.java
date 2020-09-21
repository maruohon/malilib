package fi.dy.masa.malilib.config.option;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;

public interface ConfigInfo
{
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
        String key = this.getConfigNameTranslationKey();
        String name = StringUtils.translate(key);

        // If there is no translation for the config name, then show the actual base name
        return name.equals(key) ? this.getName() : name;
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

    /**
     * Returns a list of strings that the config screen search bar is matching against.
     * Each string is tried until a match is found.
     * The match is by default checked by stringInList.contains(searchTerm)
     * where both values are first converted to lower case.
     * @return
     */
    default List<String> getSearchStrings()
    {
        return Collections.singletonList(this.getDisplayName());
    }

    /**
     * Returns a click handler for the config name label widget.
     * If this returns a non-null value, then the hover info will
     * by default get a prefix saying "Click for more information"
     * and the label widget will get this event handler set as the click action.
     * @return
     */
    @Nullable
    default EventListener getLabelClickHandler()
    {
        return null;
    }

    /**
     * 
     * Returns true if the value has been changed from the default value
     * @return
     */
    boolean isModified();

    /**
     * Resets the config value back to the default value
     */
    void resetToDefault();
}
