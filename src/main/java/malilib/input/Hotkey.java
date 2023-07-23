package malilib.input;

import java.util.List;
import javax.annotation.Nullable;

public interface Hotkey
{
    /**
     * Returns the name of this hotkey.
     * This is mostly used in the keybind conflict/overlap hover info
     * @return
     */
    String getName();

    /**
     * Returns the pretty/human readable display name for this hotkey.
     * This is used in the overlap info.
     * @return
     */
    String getDisplayName();

    /**
     * Returns the keybind used by this hotkey
     * @return
     */
    KeyBind getKeyBind();

    /**
     * @return true if the hotkey has been locked and isn't currently functional
     */
    boolean isLocked();

    /**
     * Sets the locked state of the hotkey.
     * Note that this state is only used for the config menu widgets, and doesn't affect
     * the actual functionality of the hotkey! The actual locking happens in the HotkeyManagerImpl class.
     */
    void setLocked(boolean isLocked);

    /**
     * @return the message that appears on the config screens if this hotkey has been locked
     */
    List<String> getLockAndOverrideMessages();

    /**
     * Sets the message that appears on the config screens if this hotkey has been locked
     */
    void setLockMessage(@Nullable String translationKey);

    /**
     * Convenience method for checking if the keybind is currently held/active
     * @return
     */
    default boolean isHeld()
    {
        return this.getKeyBind().isKeyBindHeld();
    }

    /**
     * Returns true if the value has been changed from the default value
     * @return
     */
    default boolean isModified()
    {
        return this.getKeyBind().isModified();
    }

    /**
     * Resets the value back to the default value
     */
    default void resetToDefault()
    {
        this.getKeyBind().resetToDefault();
    }
}
