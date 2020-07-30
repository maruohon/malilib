package fi.dy.masa.malilib.config.option;

import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IValueChangeCallback;
import fi.dy.masa.malilib.config.IValueLoadedCallback;

public interface ConfigOption<T>
{
    /**
     * Returns the type of this config. Used by the config GUI to determine what kind of control
     * to use for this config.
     * @return the type of this config
     */
    ConfigType getType();

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

    /**
     * Returns the raw translation key for the comment.
     * @return
     */
    @Nullable
    String getCommentTranslationKey();

    /**
     * Returns the "pretty name" for this config.
     * This is used in the possible toggle messages.
     * @return
     */
    default String getPrettyName()
    {
        return this.getName();
    }

    /**
     * Returns the mod name owning this config.
     * @return
     */
    String getModName();

    /**
     * Sets the mod name owning this config.
     * This is used for example for the hotkey toast popups.
     * @param modName
     */
    void setModName(String modName);

    /**
     * Returns the display name used for this config in the config GUIs
     * @return
     */
    default String getConfigGuiDisplayName()
    {
        return this.getName();
    }

    /**
     * Returns the current value of the config.<br><br>
     * <b>NOTE:</b> The primitive config types will have separate
     * type-specific getters and setters that you should use instead.
     * This generic getter is mostly used for some config menu things,
     * where the boxing overhead doesn't matter.
     * @return
     */
    //T getValue();

    /**
     * Sets the current config value.<br><br>
     * <b>NOTE:</b> The primitive config types will have separate
     * type-specific getters and setters that you should use instead.
     * This generic setter is mostly used for some config menu things,
     * where the boxing overhead doesn't matter.
     * @param newValue
     */
    //void setValue(T newValue);

    /**
     * Returns true if the value has been changed from the default value
     * @return
     */
    boolean isModified();

    /**
     * Resets the config value back to the default value
     */
    void resetToDefault();

    /**
     * Returns true if the value of this config has been changed since
     * it was last saved to file.
     * @return
     */
    boolean isDirty();

    /**
     * Cache the current value as the last saved value for the dirty checks
     */
    void cacheSavedValue();

    /**
     * Called after the config value changes
     * @param newValue the new value that was set to the config
     * @param oldValue the old value before the change happened
     */
    void onValueChanged(T newValue, T oldValue);

    /**
     * Called when the config value is read from file (or rather from JSON,
     * which might also come from somewhere else)
     * @param newValue the new value that was set from JSON
     */
    void onValueLoaded(T newValue);

    /**
     * Set the value change callback. Can be null.
     * @param callback
     */
    void setValueChangeCallback(@Nullable IValueChangeCallback<T> callback);

    /**
     * Set the value load callback. Can be null.
     * @param callback
     */
    void setValueLoadCallback(@Nullable IValueLoadedCallback<T> callback);

    /**
     * Set the value of this config option from a JSON element (is possible)
     * @param element
     * @param configName
     */
    void setValueFromJsonElement(JsonElement element, String configName);

    /**
     * Return the value of this config option as a JSON element
     * (for saving into a config file or otherwise serializing)
     * @return
     */
    JsonElement getAsJsonElement();
}
