package fi.dy.masa.malilib.gui.button;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.InputEventHandler.KeybindCategory;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;

public class ConfigButtonKeybind extends ButtonGeneric
{
    protected final IKeybindConfigGui host;
    protected final IKeybind keybind;
    protected final List<String> overlapInfo = new ArrayList<>();
    protected boolean selected;
    protected boolean firstKey;

    public ConfigButtonKeybind(int id, int x, int y, int width, int height, IKeybind keybind, IKeybindConfigGui host)
    {
        super(id, x, y, width, height, "");

        this.host = host;
        this.keybind = keybind;

        this.updateDisplayString();
    }

    @Override
    public void onMouseButtonClicked(int mouseButton)
    {
        super.onMouseButtonClicked(mouseButton);

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
                if (this.firstKey)
                {
                    this.keybind.clearKeys();
                }

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

        if (this.keybind.getKeys().size() == 0 || StringUtils.isBlank(valueStr))
        {
            valueStr = "NONE";
        }

        this.clearHoverStrings();

        if (this.selected)
        {
            this.displayString = "> " + GuiBase.TXT_YELLOW + valueStr + GuiBase.TXT_RST + " <";
        }
        else
        {
            this.updateConflicts();

            if (this.overlapInfo.size() > 0)
            {
                this.displayString = GuiBase.TXT_GOLD + valueStr + GuiBase.TXT_RST;
            }
            else
            {
                this.displayString = valueStr;
            }
        }
    }

    protected void updateConflicts()
    {
        List<KeybindCategory> categories = InputEventHandler.getInstance().getKeybindCategories();
        List<String> names = new ArrayList<>();
        this.overlapInfo.clear();

        for (KeybindCategory category : categories)
        {
            List<? extends IHotkey> hotkeys = category.getHotkeys();

            for (IHotkey hotkey : hotkeys)
            {
                if (this.keybind.overlaps(hotkey.getKeybind()))
                {
                    names.add(hotkey.getName());
                }
            }

            if (names.size() > 0)
            {
                if (this.overlapInfo.size() > 0)
                {
                    this.overlapInfo.add("-----");
                }

                this.overlapInfo.add(category.getModName());
                this.overlapInfo.add(" > " + category.getCategory());

                for (String name : names)
                {
                    this.overlapInfo.add("    - " + name);
                }

                names.clear();
            }
        }

        if (this.overlapInfo.size() > 0)
        {
            this.setHoverStrings(this.overlapInfo);
        }
    }
}
