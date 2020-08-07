package fi.dy.masa.malilib.gui.button;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.event.dispatch.KeyBindManager;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindCategory;
import fi.dy.masa.malilib.input.KeyBindImpl;
import fi.dy.masa.malilib.listener.EventListener;

public class KeyBindConfigButton extends GenericButton
{
    @Nullable protected final IKeybindConfigGui host;
    @Nullable protected EventListener valueChangeListener;
    protected final List<String> overlapInfo = new ArrayList<>();
    protected final List<Integer> newKeys = new ArrayList<>();
    protected final KeyBind keyBind;
    protected boolean selected;
    protected boolean firstKey;

    public KeyBindConfigButton(int x, int y, int width, int height, KeyBind keyBind, @Nullable IKeybindConfigGui host)
    {
        super(x, y, width, height, "");

        this.host = host;
        this.keyBind = keyBind;

        this.updateDisplayString();
        this.setHoverInfoRequiresShift(true);
    }

    public void setValueChangeListener(@Nullable EventListener valueChangeListener)
    {
        this.valueChangeListener = valueChangeListener;
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
                    this.keyBind.clearKeys();
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
            this.newKeys.clear();
            this.firstKey = false;
        }

        if (this.newKeys.contains(keyCode) == false)
        {
            this.newKeys.add(keyCode);
        }
    }

    public void onSelected()
    {
        this.selected = true;
        this.firstKey = true;
        this.newKeys.clear();
        this.newKeys.addAll(this.keyBind.getKeys());
        this.updateDisplayString();
    }

    public void onClearSelection()
    {
        if (this.firstKey == false)
        {
            this.keyBind.setKeys(this.newKeys);
        }

        this.selected = false;
        this.newKeys.clear();
        this.updateDisplayString();

        if (this.valueChangeListener != null)
        {
            this.valueChangeListener.onEvent();
        }
    }

    public boolean isSelected()
    {
        return this.selected;
    }

    @Override
    protected String generateDisplayString()
    {
        List<Integer> keys = this.selected ? this.newKeys : this.keyBind.getKeys();
        String valueStr = KeyBindImpl.writeKeysToString(keys, " + ");

        if (keys.size() == 0 || StringUtils.isBlank(valueStr))
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
        List<KeyBindCategory> categories = KeyBindManager.INSTANCE.getKeyBindCategories();
        List<Hotkey> overlaps = new ArrayList<>();
        this.overlapInfo.clear();

        for (KeyBindCategory category : categories)
        {
            List<? extends Hotkey> hotkeys = category.getHotkeys();

            for (Hotkey hotkey : hotkeys)
            {
                if (this.keyBind.overlaps(hotkey.getKeyBind()))
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

                for (Hotkey overlap : overlaps)
                {
                    String key = " [ " + BaseScreen.TXT_GOLD + overlap.getKeyBind().getKeysDisplayString() + BaseScreen.TXT_RST + " ]";
                    this.overlapInfo.add("    - " + overlap.getName() + key);
                }

                overlaps.clear();
            }
        }

        boolean modified = this.keyBind.isModified();

        if (modified)
        {
            String label = fi.dy.masa.malilib.util.StringUtils.translate("malilib.gui.button.default");
            String defaultStr = KeyBindImpl.writeKeysToString(this.keyBind.getDefaultKeys(), " + ");

            if (StringUtils.isBlank(defaultStr))
            {
                defaultStr = fi.dy.masa.malilib.util.StringUtils.translate("malilib.gui.button.none.caps");
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
