package fi.dy.masa.malilib.gui.widget.button;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.event.dispatch.KeyBindManager;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.KeybindEditingScreen;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindCategory;
import fi.dy.masa.malilib.input.KeyBindImpl;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.message.MessageType;
import fi.dy.masa.malilib.render.message.MessageUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class KeyBindConfigButton extends GenericButton
{
    @Nullable protected final KeybindEditingScreen host;
    @Nullable protected EventListener valueChangeListener;
    protected final List<String> overlapInfo = new ArrayList<>();
    protected final List<Integer> newKeys = new ArrayList<>();
    protected final KeyBind keyBind;
    protected boolean firstKey;
    protected boolean selected;
    protected boolean updateImmediately;

    public KeyBindConfigButton(int x, int y, int width, int height, KeyBind keyBind, @Nullable KeybindEditingScreen host)
    {
        super(x, y, width, height, "");

        this.host = host;
        this.keyBind = keyBind;

        this.setShouldReceiveOutsideClicks(true);
        this.updateDisplayString();
        this.setHoverInfoRequiresShift(true);
    }

    public void setValueChangeListener(@Nullable EventListener valueChangeListener)
    {
        this.valueChangeListener = valueChangeListener;
    }

    /**
     * Makes the keybind be updated immediately after each new kay is added,
     * rather than only updating once the button is unselected.
     */
    public void setUpdateKeyBindImmediately()
    {
        this.updateImmediately = true;
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.enabled == false)
        {
            return false;
        }

        boolean handled = false;

        if (this.isSelected())
        {
            if (mouseButton != 0 || this.isMouseOver(mouseX, mouseY))
            {
                this.addKey(mouseButton - 100);
                this.updateDisplayString();
                handled = true;
            }
        }
        else if (mouseButton == 0 && this.isMouseOver(mouseX, mouseY))
        {
            if (this.host != null)
            {
                this.host.setActiveKeyBindButton(this);
            }

            handled = true;
        }
        else if (mouseButton == 2 && this.isMouseOver(mouseX, mouseY))
        {
            this.keyBind.clearKeys();
            this.updateDisplayString();
            handled = true;

            if (this.valueChangeListener != null)
            {
                this.valueChangeListener.onEvent();
            }
        }

        if (handled)
        {
            super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
        }

        return handled;
    }

    public void onKeyPressed(int keyCode)
    {
        if (this.isSelected())
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

    protected void addKey(int keyCode)
    {
        if (MaLiLibConfigs.Generic.IGNORED_KEYS.getKeyBind().getKeys().contains(keyCode))
        {
            String str = KeyBindImpl.getStorageStringForKeyCode(keyCode);
            MessageUtils.showGuiMessage(MessageType.WARNING, "malilib.error.keybind.attempt_to_bind_ignored_key", str);
            return;
        }

        if (this.firstKey)
        {
            this.newKeys.clear();
            this.firstKey = false;
        }

        if (this.newKeys.contains(keyCode) == false)
        {
            this.newKeys.add(keyCode);
        }

        if (this.updateImmediately)
        {
            this.keyBind.setKeys(this.newKeys);

            if (this.valueChangeListener != null)
            {
                this.valueChangeListener.onEvent();
            }
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
        return this.selected && this.enabled;
    }

    @Override
    protected String generateDisplayString()
    {
        List<Integer> keys = this.isSelected() ? this.newKeys : this.keyBind.getKeys();
        String valueStr = KeyBindImpl.writeKeysToString(keys, " + ");

        if (keys.size() == 0 || org.apache.commons.lang3.StringUtils.isBlank(valueStr))
        {
            valueStr = StringUtils.translate("malilib.gui.button.none.caps");
        }

        if (this.isSelected())
        {
            this.clearHoverStrings();
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
        List<String> hoverStrings = new ArrayList<>();

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
            String label = StringUtils.translate("malilib.gui.button.default");
            String defaultStr = KeyBindImpl.writeKeysToString(this.keyBind.getDefaultKeys(), " + ");

            if (org.apache.commons.lang3.StringUtils.isBlank(defaultStr))
            {
                defaultStr = StringUtils.translate("malilib.gui.button.none.caps");
            }

            hoverStrings.add(label + ": " + defaultStr);
        }

        boolean nonEmpty = this.keyBind.getKeys().isEmpty() == false;

        if (nonEmpty)
        {
            hoverStrings.add(StringUtils.translate("malilib.gui.button.hover.keybind.middle_click_to_clear"));
        }

        if (this.overlapInfo.size() > 0)
        {
            if (modified || nonEmpty)
            {
                hoverStrings.add("================");
            }

            hoverStrings.add(StringUtils.translate("malilib.gui.button.hover.keybind.possible_overlaps"));
            hoverStrings.add("----------------");
            hoverStrings.addAll(this.overlapInfo);
        }

        this.setHoverStrings(hoverStrings);
    }
}
