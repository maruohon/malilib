package fi.dy.masa.malilib.gui.widget.button;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.gui.config.KeybindEditingScreen;
import fi.dy.masa.malilib.input.Hotkey;
import fi.dy.masa.malilib.input.HotkeyCategory;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.Keys;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.registry.Registry;
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

    public KeyBindConfigButton(int width, int height, KeyBind keyBind, @Nullable KeybindEditingScreen host)
    {
        super(width, height);

        this.host = host;
        this.keyBind = keyBind;

        this.setHoverStringProvider("overlap_info", this::getKeyBindHoverStrings);
        this.setShouldReceiveOutsideClicks(true);
        this.setHoverInfoRequiresShift(true);
        this.setDisplayStringSupplier(this::getCurrentDisplayString);
        this.updateConflicts();
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
        if (this.isEnabled() == false)
        {
            return false;
        }

        boolean handled = false;

        if (this.isSelected())
        {
            if (mouseButton != 0 || this.isMouseOver(mouseX, mouseY))
            {
                this.addKey(mouseButton - 100);
                this.updateButtonState();
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
            this.updateButtonState();
            this.notifyListener();
            handled = true;
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
        if (this.isEnabled() && this.isSelected())
        {
            int keyCode = mouseWheelDelta < 0 ? -201 : -199;
            this.addKey(keyCode);
            this.updateButtonState();
            return true;
        }

        return false;
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
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

            this.updateButtonState();

            return true;
        }

        return false;
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
        if (MaLiLibConfigs.Hotkeys.IGNORED_KEYS.getKeyBind().getKeys().contains(keyCode))
        {
            String str = Keys.getStorageStringForKeyCode(keyCode, Keys::charAsCharacter);
            MessageDispatcher.warning("malilib.message.error.keybind.attempt_to_bind_ignored_key", str);
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
            this.notifyListener();
        }
    }

    public void onSelected()
    {
        this.selected = true;
        this.firstKey = true;
        this.newKeys.clear();
        this.newKeys.addAll(this.keyBind.getKeys());
        this.setHoverInfoRequiresShift(false);
        this.updateButtonState();
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
        this.updateButtonState();
        this.notifyListener();
    }

    @Override
    public void updateButtonState()
    {
        this.updateConflicts();
        super.updateButtonState();
    }

    protected void notifyListener()
    {
        if (this.valueChangeListener != null)
        {
            this.valueChangeListener.onEvent();
        }
    }

    public boolean isSelected()
    {
        return this.selected && this.isEnabled();
    }

    protected String getCurrentDisplayString()
    {
        List<Integer> keys = this.isSelected() ? this.newKeys : this.keyBind.getKeys();
        String valueStr = Keys.writeKeysToString(keys, " + ", Keys::charAsCharacter);

        if (keys.size() == 0 || org.apache.commons.lang3.StringUtils.isBlank(valueStr))
        {
            valueStr = StringUtils.translate("malilib.button.misc.none.caps");
        }

        if (this.isSelected())
        {
            return StringUtils.translate("malilib.button.config.keybind_button.selected", valueStr);
        }
        else
        {
            if (this.overlapInfoSize > 0)
            {
                return StringUtils.translate("malilib.button.config.keybind_button.overlapping", valueStr);
            }
            else
            {
                return valueStr;
            }
        }
    }

    protected List<String> getKeyBindHoverStrings()
    {
        return this.isSelected() || this.isEnabled() == false ? EMPTY_STRING_LIST : this.hoverStrings;
    }

    protected void updateConflicts()
    {
        if (this.isSelected())
        {
            return;
        }

        List<HotkeyCategory> categories = Registry.HOTKEY_MANAGER.getHotkeyCategories();
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
                    String translationKey = "malilib.hover.button.keybind.overlap_line";
                    String name = overlap.getDisplayName();
                    String keys = overlap.getKeyBind().getKeysDisplayString();
                    overlapInfo.add(StringUtils.translate(translationKey, name, keys));
                }

                overlaps.clear();
            }
        }

        this.overlapInfoSize = overlapInfo.size();

        boolean modified = this.keyBind.isModified();
        boolean nonEmpty = this.keyBind.getKeys().isEmpty() == false;

        //if (modified)
        {
            String defaultStr = Keys.writeKeysToString(this.keyBind.getDefaultKeys(), " + ", Keys::charAsCharacter);

            if (org.apache.commons.lang3.StringUtils.isBlank(defaultStr))
            {
                defaultStr = StringUtils.translate("malilib.button.misc.none.caps");
            }

            hoverStrings.add(StringUtils.translate("malilib.hover.button.keybind.default_value", defaultStr));
        }

        if (nonEmpty)
        {
            hoverStrings.add(StringUtils.translate("malilib.hover.button.keybind.middle_click_to_clear"));
        }

        if (this.overlapInfoSize > 0)
        {
            if (modified || nonEmpty)
            {
                hoverStrings.add("----------------");
            }

            hoverStrings.add(StringUtils.translate("malilib.hover.button.keybind.possible_overlaps"));
            hoverStrings.add("----------------");
            hoverStrings.addAll(overlapInfo);
        }

        this.hoverStrings.clear();
        this.hoverStrings.addAll(hoverStrings);
        this.hoverInfoFactory.updateList();
    }
}
