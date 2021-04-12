package fi.dy.masa.malilib.input.callback;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.BooleanConfig;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindSettings;

public class AdjustableKeyCallback implements HotkeyCallback
{
    protected static boolean valueChanged;

    @Nullable protected final BooleanConfig config;
    @Nullable protected final HotkeyCallback callback;

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
    public AdjustableKeyCallback(@Nullable BooleanConfig config, @Nullable HotkeyCallback callback)
    {
        this.config = config;
        this.callback = callback;
    }

    @Override
    public ActionResult onKeyAction(KeyAction action, KeyBind key)
    {
        KeyBindSettings settings = key.getSettings();

        // For keybinds that activate on both edges, the press action activates the
        // "adjust mode", and we just cancel further processing of the key presses here.
        if (settings.getActivateOn() == KeyAction.BOTH)
        {
            if (action == KeyAction.PRESS)
            {
                return ActionResult.SUCCESS;
            }

            // Don't toggle the state if a value was adjusted
            if (valueChanged)
            {
                valueChanged = false;
                return ActionResult.SUCCESS;
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
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }
}
