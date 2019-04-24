package fi.dy.masa.malilib.gui.button;

import org.apache.commons.lang3.StringUtils;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.util.KeyCodes;

public class ConfigButtonKeybind extends ButtonBase
{
    private final IKeybindConfigGui host;
    private final IKeybind keybind;
    private boolean selected;
    private boolean firstKey;

    public ConfigButtonKeybind(int id, int x, int y, int width, int height, IKeybind keybind, IKeybindConfigGui host)
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
            if (keyCode == KeyCodes.KEY_ESCAPE)
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
            this.setMessage("> " + GuiBase.TXT_YELLOW + valueStr + GuiBase.TXT_RST + " <");
        }
        else
        {
            this.setMessage(valueStr);
        }
    }
}
