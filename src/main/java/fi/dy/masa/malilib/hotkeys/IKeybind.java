package fi.dy.masa.malilib.hotkeys;

import java.util.Collection;
import javax.annotation.Nullable;

public interface IKeybind
{
    boolean isValid();

    /**
     * Returns true if the keybind was pressed down during the current game tick.
     */
    boolean isPressed();

    /**
     * Returns true if the keybind is currently being held down.
     */
    boolean isKeybindHeld();

    /**
     * Checks and updated the pressed status, and fires the callback, if one is set.
     * Returns true if further processing of the just pressed key should be cancelled.
     * This return value can be determined by the callback, if one has been set.
     * Without a callback, the return value will be false to not cancel further processing.
     * @return
     */
    boolean updateIsPressed();

    void clearKeys();

    void addKey(int keyCode);

    void removeKey(int keyCode);

    void tick();

    String getKeysDisplayString();

    String getStorageString();

    void setKeysFromStorageString(String key);

    /**
     * Returns true if the keybind has been changed from the default value
     */
    boolean isModified();

    Collection<Integer> getKeys();

    void setCallback(@Nullable IHotkeyCallback callback);
}
