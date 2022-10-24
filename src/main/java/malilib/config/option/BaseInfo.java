package malilib.config.option;

import java.util.Optional;

import malilib.util.data.ModInfo;

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
     * @return the (localized) comment, if one exists
     */
    Optional<String> getComment();

    /**
     * Sets the ModInfo owning this config.
     * This is automatically called in {@link malilib.config.ConfigManager#registerConfigHandler(malilib.config.ModConfig)}
     * using the ModInfo from {@link malilib.config.ModConfig#getModInfo()}.
     * @param modInfo
     */
    default void setModInfo(ModInfo modInfo)
    {
    }
}
