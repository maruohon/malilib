package fi.dy.masa.malilib.config.option;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.util.data.ModInfo;

public interface BaseInfo
{
    /**
     * Returns the (internal) name of this object, used for example in the config files
     * @return the internal name of this config
     */
    String getName();

    /**
     * @return the display name used for this object, for example for config options on the config screens
     */
    String getDisplayName();

    /**
     * @return the ModInfo of the mod owning this object
     */
    ModInfo getModInfo();

    /**
     * Returns the comment or description for this object. It can be for example displayed
     * when hovering over the object's name or other related screen element on some screens.
     * Newlines can be added with "\n". Can be null if there is no comment for this object.
     * @return the (localized) comment, or null if no comment has been set
     */
    @Nullable
    String getComment();
}
