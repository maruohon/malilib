package fi.dy.masa.malilib.config.option;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.listener.EventListener;

public interface ConfigInfo
{
    /**
     * Returns the (internal) name of this config, used for example in the config files
     * @return the internal name of this config
     */
    String getName();

    /**
     * Returns the display name used for this config on the config screens
     * @return
     */
    String getDisplayName();

    /**
     * Returns the comment displayed when hovering over the config name on the config screens.
     * Newlines can be added with "\n". Can be null if there is no comment for this config.
     * @return the (localized) comment, or null if no comment has been set
     */
    @Nullable
    String getComment();

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
     * If this config contains any nested options, adds the nested options to the provided list.
     * This is intended for config groups which are currently open/expanded, to add their
     * contained nested options to the list for the config screen.
     */
    default <C extends ConfigInfo> void addNestedOptionsToList(List<C> list, int nestingLevel)
    {
        // NO-OP
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
