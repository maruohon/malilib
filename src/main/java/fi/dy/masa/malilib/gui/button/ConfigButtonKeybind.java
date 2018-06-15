package fi.dy.masa.malilib.gui.button;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.config.gui.ConfigPanelSub;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import net.minecraft.util.text.TextFormatting;

public class ConfigButtonKeybind extends ButtonBase
{
    private final ConfigPanelSub host;
    private final IKeybind keybind;
    private boolean selected;
    private boolean firstKey;

    public ConfigButtonKeybind(int id, int x, int y, int width, int height, IKeybind keybind, ConfigPanelSub host)
    {
        super(id, x, y, width, height);

        this.host = host;
        this.keybind = keybind;

        this.updateDisplayString();
    }

    @Override
    public void onMouseButtonClicked(int mouseButton)
    {
        if (this.selected)
        {
            this.addKey(mouseButton - 100);
            this.updateDisplayString();
        }
        else if (mouseButton == 0)
        {
            this.selected = true;
            this.host.setActiveKeybindButton(this);
        }
    }

    public void onKeyPressed(int keyCode)
    {
        if (this.selected)
        {
            if (keyCode == Keyboard.KEY_ESCAPE)
            {
                this.keybind.clearKeys();
                this.host.setActiveKeybindButton(null);
            }
            else
            {
                this.addKey(keyCode);
            }

            this.updateDisplayString();
        }
    }

    private void addKey(int keyCode)
    {
        if (this.firstKey)
        {
            this.keybind.clearKeys();
            this.firstKey = false;
        }

        this.keybind.addKey(keyCode);
    }

    public void onSelected()
    {
        this.selected = true;
        this.firstKey = true;
        this.updateDisplayString();
    }

    public void onClearSelection()
    {
        this.selected = false;
        this.updateDisplayString();
    }

    public void updateDisplayString()
    {
        String valueStr = this.keybind.getKeysDisplayString();

        if (this.keybind.isValid() == false || StringUtils.isBlank(valueStr))
        {
            valueStr = "NONE";
        }

        if (this.selected)
        {
            this.displayString = "> " + TextFormatting.YELLOW + valueStr + TextFormatting.RESET + " <";
        }
        else
        {
            this.displayString = valueStr;
        }
    }
}
