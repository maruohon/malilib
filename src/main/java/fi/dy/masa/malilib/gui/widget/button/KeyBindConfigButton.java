package fi.dy.masa.malilib.gui.widget.button;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.KeybindEditingScreen;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.HotkeyCategory;
import fi.dy.masa.malilib.input.HotkeyManager;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.Keys;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.overlay.message.MessageUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class KeyBindConfigButton extends GenericButton
{
    @Nullable protected final KeybindEditingScreen host;
    @Nullable protected EventListener valueChangeListener;
    protected final KeyBind keyBind;
    protected final List<Integer> newKeys = new ArrayList<>();
    protected final List<String> hoverStrings = new ArrayList<>();
    protected int overlapInfoSize;
    protected boolean firstKey;
    protected boolean selected;
    protected boolean updateImmediately;

    public KeyBindConfigButton(int x, int y, int width, int height, KeyBind keyBind, @Nullable KeybindEditingScreen host)
    {
        super(x, y, width, height, "");

        this.host = host;
        this.keyBind = keyBind;
        this.setHoverStringProvider("overlap_info", this::getKeyBindHoverStrings);

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
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.enabled == false || this.visible == false)
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
            // Play the click sound
            super.onMouseClicked(mouseX, mouseY, mouseButton);
        }

        return handled;
    }

    @Override
    protected boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (this.enabled && this.visible && this.isSelected())
        {
            int keyCode = mouseWheelDelta < 0 ? -201 : -199;
            this.addKey(keyCode);
            this.updateDisplayString();
            return true;
        }

        return false;
    }

    public void onKeyPressed(int keyCode, int scanCode, int modifiers)
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

    @Override
    public boolean onCharTyped(char charIn, int modifiers)
    {
        // Eat all the characters when the button is active,
        // otherwise they can leak into search bars etc.
        return this.isSelected();
    }

    protected void addKey(int keyCode)
    {
        if (MaLiLibConfigs.Generic.IGNORED_KEYS.getKeyBind().getKeys().contains(keyCode))
        {
            String str = Keys.getStorageStringForKeyCode(keyCode, Keys::charAsCharacter);
            MessageUtils.warning("malilib.message.error.keybind.attempt_to_bind_ignored_key", str);
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
        this.setHoverInfoRequiresShift(false);
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
        this.setHoverInfoRequiresShift(true);
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
        String valueStr = Keys.writeKeysToString(keys, " + ", Keys::charAsCharacter);

        if (keys.size() == 0 || org.apache.commons.lang3.StringUtils.isBlank(valueStr))
        {
            valueStr = StringUtils.translate("malilib.gui.button.none.caps");
        }

        this.updateConflicts();

        if (this.isSelected())
        {
            return "> " + BaseScreen.TXT_YELLOW + valueStr + BaseScreen.TXT_RST + " <";
        }
        else
        {
            if (this.overlapInfoSize > 0)
            {
                return BaseScreen.TXT_GOLD + valueStr + BaseScreen.TXT_RST;
            }
            else
            {
                return valueStr;
            }
        }
    }

    protected List<String> getKeyBindHoverStrings()
    {
        return this.isSelected() || this.enabled == false ? EMPTY_STRING_LIST : this.hoverStrings;
    }

    protected void updateConflicts()
    {
        if (this.isSelected())
        {
            return;
        }

        List<HotkeyCategory> categories = HotkeyManager.INSTANCE.getHotkeyCategories();
        List<Hotkey> overlaps = new ArrayList<>();
        List<String> hoverStrings = new ArrayList<>();
        List<String> overlapInfo = new ArrayList<>();

        for (HotkeyCategory category : categories)
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
                if (overlapInfo.size() > 0)
                {
                    overlapInfo.add("--------");
                }

                overlapInfo.add(category.getModInfo().getModName());
                overlapInfo.add(" > " + category.getCategoryName());

                for (Hotkey overlap : overlaps)
                {
                    String key = " [ " + BaseScreen.TXT_GOLD + overlap.getKeyBind().getKeysDisplayString() + BaseScreen.TXT_RST + " ]";
                    overlapInfo.add("    - " + overlap.getName() + key);
                }

                overlaps.clear();
            }
        }

        this.overlapInfoSize = overlapInfo.size();

        boolean modified = this.keyBind.isModified();

        //if (modified)
        {
            String label = StringUtils.translate("malilib.gui.button.default");
            String defaultStr = Keys.writeKeysToString(this.keyBind.getDefaultKeys(), " + ", Keys::charAsCharacter);

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

        if (this.overlapInfoSize > 0)
        {
            if (modified || nonEmpty)
            {
                hoverStrings.add("================");
            }

            hoverStrings.add(StringUtils.translate("malilib.gui.button.hover.keybind.possible_overlaps"));
            hoverStrings.add("----------------");
            hoverStrings.addAll(overlapInfo);
        }

        this.hoverStrings.clear();
        this.hoverStrings.addAll(hoverStrings);
        this.hoverInfoFactory.updateList();
    }
}
