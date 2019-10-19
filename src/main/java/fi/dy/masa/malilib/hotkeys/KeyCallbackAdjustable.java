package fi.dy.masa.malilib.hotkeys;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.options.IConfigBoolean;

public class KeyCallbackAdjustable implements IHotkeyCallback
{
    protected static boolean valueChanged;

    @Nullable protected final IConfigBoolean config;
    @Nullable protected final IHotkeyCallback callback;

    public static void setValueChanged()
    {
        valueChanged = true;
    }

    /**
     * Creates a wrapper callback, which has special behavior for the ActivateOn value of BOTH, such that
     * it will only call the provided callback on RELEASE, if there was no config value adjusted while
     * the keybind was active. If the ActivateOn value is PRESS or RELEASE, then there is no special behavior
     * and the provided callback is called directly.
     * The hotkey callback has priority over the boolean callback, if both are provided.
     * So it only makes sense to provide one. 
     * @param config
     * @param callback
     */
    public KeyCallbackAdjustable(@Nullable IConfigBoolean config, @Nullable IHotkeyCallback callback)
    {
        this.config = config;
        this.callback = callback;
    }

    @Override
    public boolean onKeyAction(KeyAction action, IKeybind key)
    {
        KeybindSettings settings = key.getSettings();

        // For keybinds that activate on both edges, the press action activates the
        // "adjust mode", and we just cancel further processing of the key presses here.
        if (settings.getActivateOn() == KeyAction.BOTH)
        {
            if (action == KeyAction.PRESS)
            {
                return true;
            }

            // Don't toggle the state if a value was adjusted
            if (valueChanged)
            {
                valueChanged = false;
                return true;
            }
        }

        valueChanged = false;

        if (this.callback != null)
        {
            return this.callback.onKeyAction(action, key);
        }
        else if (this.config != null)
        {
            this.config.toggleBooleanValue();
            return true;
        }

        return false;
    }
}
