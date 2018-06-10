package fi.dy.masa.malilib.config;

import javax.annotation.Nullable;

public interface IConfigBase
{
    /**
     * Returns the config name to display in the config GUIs
     * @return the name of this config
     */
    String getName();

    /**
     * Returns the comment displayed when hovering over the config name in the config GUI.
     * Newlines can be added with "\n". Can be null if there is no comment for this config.
     * @return the comment, or null if no comment has been set
     */
    @Nullable
    String getComment();
}
