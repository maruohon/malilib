package fi.dy.masa.malilib.gui.button;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.event.dispatch.InputEventDispatcher;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.input.IHotkey;
import fi.dy.masa.malilib.input.IKeyBind;
import fi.dy.masa.malilib.input.KeyBindCategory;

public class ConfigButtonKeyBind extends ButtonGeneric
{
    @Nullable protected final IKeybindConfigGui host;
    protected final IKeyBind keybind;
    protected final List<String> overlapInfo = new ArrayList<>();
    protected boolean selected;
    protected boolean firstKey;

    public ConfigButtonKeyBind(int x, int y, int width, int height, IKeyBind keybind, @Nullable IKeybindConfigGui host)
    {
        super(x, y, width, height, "");

        this.host = host;
        this.keybind = keybind;

        this.updateDisplayString();
        this.setHoverInfoRequiresShift(true);
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        super.onMouseClickedImpl(mouseX, mouseY, mouseButton);

        if (this.selected)
        {
            this.addKey(mouseButton - 100);
            this.updateDisplayString();
        }
        else if (mouseButton == 0)
        {
            this.selected = true;

            if (this.host != null)
            {
                this.host.setActiveKeyBindButton(this);
            }
        }

        return true;
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

                if (this.host != null)
                {
                    this.host.setActiveKeyBindButton(null);
                }
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

    public boolean isSelected()
    {
        return this.selected;
    }

    @Override
    protected String generateDisplayString()
    {
        String valueStr = this.keybind.getKeysDisplayString();

        if (this.keybind.getKeys().size() == 0 || StringUtils.isBlank(valueStr))
        {
            valueStr = fi.dy.masa.malilib.util.StringUtils.translate("malilib.gui.button.none.caps");
        }

        this.clearHoverStrings();

        if (this.selected)
        {
            return "> " + BaseScreen.TXT_YELLOW + valueStr + BaseScreen.TXT_RST + " <";
        }
        else
        {
            this.updateConflicts();

            if (this.overlapInfo.size() > 0)
            {
                return BaseScreen.TXT_GOLD + valueStr + BaseScreen.TXT_RST;
            }
            else
            {
                return valueStr;
            }
        }
    }

    protected void updateConflicts()
    {
        List<KeyBindCategory> categories = InputEventDispatcher.getKeyBindManager().getKeyBindCategories();
        List<IHotkey> overlaps = new ArrayList<>();
        this.overlapInfo.clear();

        for (KeyBindCategory category : categories)
        {
            List<? extends IHotkey> hotkeys = category.getHotkeys();

            for (IHotkey hotkey : hotkeys)
            {
                if (this.keybind.overlaps(hotkey.getKeyBind()))
                {
                    overlaps.add(hotkey);
                }
            }

            if (overlaps.size() > 0)
            {
                if (this.overlapInfo.size() > 0)
                {
                    this.overlapInfo.add("--------");
                }

                this.overlapInfo.add(category.getModName());
                this.overlapInfo.add(" > " + category.getCategoryName());

                for (IHotkey overlap : overlaps)
                {
                    String key = " [ " + BaseScreen.TXT_GOLD + overlap.getKeyBind().getKeysDisplayString() + BaseScreen.TXT_RST + " ]";
                    this.overlapInfo.add("    - " + overlap.getName() + key);
                }

                overlaps.clear();
            }
        }

        boolean modified = this.keybind.isModified();

        if (modified)
        {
            String label = fi.dy.masa.malilib.util.StringUtils.translate("malilib.gui.button.default");
            String defaultStr = this.keybind.getDefaultStringValue();

            if (StringUtils.isBlank(defaultStr))
            {
                defaultStr = fi.dy.masa.malilib.util.StringUtils.translate("malilib.gui.button.none.caps");
            }
            else
            {
                defaultStr = defaultStr.replaceAll(",", " + ");
            }

            this.addHoverStrings(label + ": " + defaultStr);
        }

        if (this.overlapInfo.size() > 0)
        {
            if (modified)
            {
                this.addHoverStrings("--------");
            }

            this.addHoverStrings(this.overlapInfo);
        }
    }
}
