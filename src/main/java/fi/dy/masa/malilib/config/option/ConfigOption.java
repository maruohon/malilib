package fi.dy.masa.malilib.config.option;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.ValueChangeCallback;
import fi.dy.masa.malilib.config.ValueLoadCallback;
import fi.dy.masa.malilib.util.data.ModInfo;

public interface ConfigOption<T> extends ConfigInfo
{
    /**
     * Sets the ModInfo owning this config.
     * This is automatically called in {@link fi.dy.masa.malilib.config.ConfigManager#registerConfigHandler(fi.dy.masa.malilib.config.ModConfig)}
     * using the ModInfo from {@link fi.dy.masa.malilib.config.ModConfig#getModInfo()}.
     * @param modInfo
     */
    void setModInfo(ModInfo modInfo);

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
     * Returns a list of old internal names this config used to go by, if any.
     * This allows reading the user-set values from config files that still use
     * the old name, if the config has since been renamed.
     * @return
     */
    default List<String> getOldNames()
    {
        return Collections.emptyList();
    }

    /**
     * Set the value change callback. Can be null.
     * @param callback
     */
    void setValueChangeCallback(@Nullable ValueChangeCallback<T> callback);

    /**
     * Set the value load callback. Can be null.
     * @param callback
     */
    void setValueLoadCallback(@Nullable ValueLoadCallback<T> callback);

    /**
     * Whether or not this config is currently locked to its current value,
     * and can not be changed without unlocking.
     *
     * @return true if the config is locked
     */
    boolean isLocked();

    /**
     * Set whether or not this config should be locked to its current value.
     * Note: This does not prevent the methods that load the values from configs
     * from changing the value! It only prevents the normal setValue() methods
     * from changing it.
     * @param isLocked
     */
    void setLocked(boolean isLocked);
}
