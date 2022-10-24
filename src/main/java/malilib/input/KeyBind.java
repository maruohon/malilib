package malilib.input;

import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import malilib.input.callback.HotkeyCallback;
import malilib.util.data.ModInfo;

public interface KeyBind
{
    /**
     * Sets the ModInfo owning this keybind. Used for the popup toast rendering.
     */
    void setModInfo(ModInfo modInfo);

    /**
     * Set the name translation key
     */
    void setNameTranslationKey(String nameTranslationKey);

    /**
     * Returns true if the keybind is currently considered as being held down.
     * This takes into consideration the KeybindSettings toggle and inverted values,
     * and thus the actual physical state does not need to be held for this to be
     * considered to be active.
     */
    boolean isKeyBindHeld();

    /**
     * @return true if the keybind is currently actually physically held down.
     * This ignores the KeybindSettings toggle and inverted values.
     */
    boolean isPhysicallyHeld();

    /**
     * Checks and updated the pressed status, and fires the callback, if one is set.
     * Returns true if further processing of the just pressed key should be cancelled.
     * This return value can be determined by the callback, if one has been set.
     * Without a callback, the return value will be false to not cancel further processing.
     * NOTE: This generally should not be called by mods!!
     * @return the update result
     */
    KeyUpdateResult updateIsPressed(boolean isFirst);

    /**
     * @return the current KeybindSettings for this keybind
     */
    KeyBindSettings getSettings();

    /**
     * @return the default KeybindSettings for this keybind
     */
    KeyBindSettings getDefaultSettings();

    /**
     * Set the settings for this keybind.
     * @param settings
     */
    void setSettings(KeyBindSettings settings);

    /**
     * Caches the current value as the "last saved value", which is used
     * for checking if the value is "dirty" and the configs need to be saved to file again.
     */
    void cacheSavedValue();

    /**
     * Clears/empties the keybind sequence.
     */
    void clearKeys();

    /**
     * @return true if the current key combination contains the provided keyCode
     */
    boolean containsKey(int keyCode);

    /**
     * @return true if the keybind has at least some keys bound. false if the keybind is unbound.
     */
    boolean hasKeys();

    /**
     * @return true if the keybind has been modified since last being
     * marked as saved (by {@link #cacheSavedValue()})
     */
    boolean isDirty();

    /**
     * Check if the keybind matches the given keyCode sequence. This is intended
     * for checking if the keybind was modified in the config screen, and this does
     * not take into account anything other than the keyCode sequence itself, in full.
     * @param keys the list of keyCodes to compare with
     * @return true if the keybind matches the given keyCode sequence
     */
    boolean matches(IntArrayList keys);

    /**
     * @return true if the keybind has been modified from the defaults
     */
    boolean isModified();

    /**
     * Resets this keybind back to the default keys. Does not reset the KeybindSettings.
     */
    void resetToDefault();

    /**
     * Sets the keybind keys, parsed from the given "storage string".
     * The storage string is the key names, joined by commas, without spaces.
     */
    void setValueFromString(String str);

    /**
     * Sets the keyCodes for this keybind
     */
    void setKeys(IntArrayList newKeys);

    /**
     * Gets the current keyCodes by adding them to the given list
     */
    void getKeysToList(IntArrayList list);

    /**
     * Check if this keybind is only a single key, matching the given key code.
     * This is mainly meant for checking equality against vanilla keybinds.
     * @return true if this keybind is only a single key, and matches the given keyCode
     */
    boolean matches(int keyCode);

    /**
     * Checks if this keybind overlaps with the given other keybind.
     * This also takes into consideration the KeybindSettings.
     */
    boolean overlaps(KeyBind other);

    /**
     * @return the human-readable display string of the current key combination
     */
    String getKeysDisplayString();

    /**
     * @return the human-readable display string of the default key combination
     */
    String getDefaultKeysDisplayString();

    /**
     * Sets the callback that will get triggered when the keybind activates
     */
    void setCallback(@Nullable HotkeyCallback callback);

    /**
     * @return true if the KeybindSettings have been modified from defaults
     */
    boolean areSettingsModified();

    /**
     * Resets the KeybindSettings back to the default values
     */
    void resetSettingsToDefaults();

    /**
     * Reads both the keybind keys and the KeybindSettings from the provided element
     */
    void setValueFromJsonElement(JsonElement element, String hotkeyName);

    /**
     * @return the keybind keys and the KeybindSettings serialized as a JsonObject
     */
    JsonElement getAsJsonElement();
}
