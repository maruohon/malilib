package fi.dy.masa.malilib.hotkeys;

import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class KeyCallbackToggleBooleanConfigWithMessage implements IHotkeyCallback
{
    protected final IConfigBoolean config;

    public KeyCallbackToggleBooleanConfigWithMessage(IConfigBoolean config)
    {
        this.config = config;
    }

    @Override
    public boolean onKeyAction(KeyAction action, IKeybind key)
    {
        if (action == KeyAction.PRESS)
        {
            this.config.setBooleanValue(this.config.getBooleanValue() == false);

            final boolean enabled = this.config.getBooleanValue();
            String pre = enabled ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString();
            String status = I18n.format("malilib.message.value." + (enabled ? "on" : "off"));
            String message = I18n.format("malilib.message.toggled", this.config.getPrettyName(), pre + status + TextFormatting.RESET);
            StringUtils.printActionbarMessage(message);
            return true;
        }

        return false;
    }
}
