package fi.dy.masa.malilib.config;

import javax.annotation.Nullable;
import com.google.gson.JsonElement;

public interface ConfigOption<T> extends ConfigInfo
{
    /**
     * Returns the mod ID owning this config.
     * @return
     */
    String getModId();

    /**
     * Sets the mod ID owning this config.
     * This is used for generating the default config localization keys.
     * This is automatically called in {@link ConfigManager#registerConfigHandler(ModConfig)}
     * using the mod ID from {@link ModConfig#getModId()}.
     * @param modId
     */
    void setModId(String modId);

    /**
     * Returns the mod name owning this config.
     * @return
     */
    String getModName();

    /**
     * Sets the mod name owning this config.
     * This is used for the hotkey toast popups.
     * This is automatically called in {@link ConfigManager#registerConfigHandler(ModConfig)}
     * using the mod ID from {@link ModConfig#getModName()}
     * @param modName
     */
    void setModName(String modName);

    /**
     * 
     * Returns the "pretty name" for this config.
     * This is used in the possible toggle messages.
     * @return
     */
    default String getPrettyName()
    {
        return this.getDisplayName();
    }

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
     * Returns the value of this config.
     * Note that for primitive config types you should generally prefer the
     * type-specific methods to avoid boxing/unboxing.
     * This method is meant more for some of the config screen and other config
     * system related code, to make things a bit more generic there.
     * @return
     */
    T getValue();

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

    /**
     * Whether or not this config is currently locked to its current value,
     * and can not be changed without unlocking.
     *
     * @return true if the config is locked
     */
    boolean isLocked();

    /**
     * Set whether or not this config should be locked to its current value.
     * Note: This does not prevent the {@link #setValueFromJsonElement(JsonElement, String)} method
     * from changing the value! It only prevents the normal setValue() methods
     * from changing it.
     * @param isLocked
     */
    void setLocked(boolean isLocked);
}
