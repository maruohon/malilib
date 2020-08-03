package fi.dy.masa.malilib.config.option;

import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import fi.dy.masa.malilib.config.ValueChangeCallback;
import fi.dy.masa.malilib.config.ValueLoadedCallback;
import fi.dy.masa.malilib.util.StringUtils;

public interface ConfigOption<T> extends ConfigInfo
{
    /**
     * Returns the mod ID owning this config.
     * @return
     */
    String getModId();

    /**
     * Sets the mod ID owning this config.
     * This is used for generating the default config localization key
     * strings and also for example for the hotkey toast popups and some config screen tooltips.
     * @param modId
     */
    void setModId(String modId);

    /**
     * Returns the "pretty name" for this config.
     * This is used in the possible toggle messages.
     * @return
     */
    default String getPrettyName()
    {
        return StringUtils.translate(this.getConfigNameTranslationKey());
    }

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
    void setValueChangeCallback(@Nullable ValueChangeCallback<T> callback);

    /**
     * Set the value load callback. Can be null.
     * @param callback
     */
    void setValueLoadCallback(@Nullable ValueLoadedCallback<T> callback);

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
