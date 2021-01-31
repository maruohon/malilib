package fi.dy.masa.malilib.input;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import fi.dy.masa.malilib.input.callback.HotkeyCallback;

public interface KeyBind
{
    /**
     * Sets the mod name owning this keybind. Used for the popup toast rendering.
     * @param modName
     */
    void setModName(String modName);

    void setNameTranslationKey(String nameTranslationKey);

    boolean isValid();

    /**
     * Returns true if the keybind was pressed down during the current game tick.
     */
    boolean isPressed();

    /**
     * Returns true if the keybind is currently being held down.
     */
    boolean isKeyBindHeld();

    /**
     * Checks and updated the pressed status, and fires the callback, if one is set.
     * Returns true if further processing of the just pressed key should be cancelled.
     * This return value can be determined by the callback, if one has been set.
     * Without a callback, the return value will be false to not cancel further processing.
     * @return
     */
    KeyUpdateResult updateIsPressed(boolean isFirst);

    KeyBindSettings getSettings();

    KeyBindSettings getDefaultSettings();

    /**
     * Set the settings for this keybind.
     * @param settings
     */
    void setSettings(KeyBindSettings settings);

    boolean isModified();

    boolean isDirty();

    void cacheSavedValue();

    void clearKeys();

    void resetToDefault();

    void setValueFromString(String str);

    void setKeys(List<Integer> newKeys);

    /**
     * Check if this keybind is only a single key, matching the given key code.
     * This is mainly meant for checking equality against vanilla keybinds.
     * @param keyCode
     * @return
     */
    boolean matches(int keyCode);

    boolean overlaps(KeyBind other);

    void tick();

    String getKeysDisplayString();

    ImmutableList<Integer> getKeys();

    ImmutableList<Integer> getDefaultKeys();

    void setCallback(@Nullable HotkeyCallback callback);

    boolean areSettingsModified();

    void resetSettingsToDefaults();

    void setValueFromJsonElement(JsonElement element, String hotkeyName);

    JsonElement getAsJsonElement();
}
