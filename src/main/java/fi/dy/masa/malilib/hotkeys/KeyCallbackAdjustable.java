package fi.dy.masa.malilib.hotkeys;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.options.IConfigBoolean;

public class KeyCallbackAdjustable implements IHotkeyCallback
{
    protected static boolean valueChanged;

    protected final IConfigBoolean config;
    @Nullable protected final IHotkeyCallback callback;

    public static void setValueChanged()
    {
        valueChanged = true;
    }

    public KeyCallbackAdjustable(IConfigBoolean config, @Nullable IHotkeyCallback callback)
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
        else if (valueChanged)
        {
            valueChanged = false;
        }

        if (this.callback != null)
        {
            return this.callback.onKeyAction(action, key);
        }
        else
        {
            this.config.toggleBooleanValue();

            return true;
        }
    }
}
