package malilib.gui.widget.button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

import malilib.MaLiLibConfigs;
import malilib.input.Hotkey;
import malilib.input.HotkeyCategory;
import malilib.input.KeyBind;
import malilib.input.Keys;
import malilib.listener.EventListener;
import malilib.overlay.message.MessageDispatcher;
import malilib.registry.Registry;
import malilib.util.StringUtils;
import malilib.util.game.wrap.GameUtils;

public class KeyBindConfigButton extends GenericButton
{
    @Nullable protected EventListener valueChangeListener;
    protected final KeyBind keyBind;
    protected final IntArrayList newKeys = new IntArrayList();
    protected final List<String> hoverStrings = new ArrayList<>();
    protected int overlapInfoSize;
    protected boolean firstKey;
    protected boolean updateImmediately;

    public KeyBindConfigButton(int width, int height, KeyBind keyBind)
    {
        super(width, height);

        this.canBeFocused = true;
        this.canReceiveMouseScrolls = true;
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
    public boolean getShouldReceiveOutsideClicks()
    {
        return this.isFocused() && super.getShouldReceiveOutsideClicks();
    }

    @Override
    public int getMouseClickHandlingPriority(int mouseX, int mouseY)
    {
        int priority = super.getMouseClickHandlingPriority(mouseX, mouseY);

        if (this.isEnabled() && this.isFocused() && this.isMouseOver(mouseX, mouseY) == false)
        {
            priority += 100;
        }

        return priority;
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isEnabled() == false)
        {
            return false;
        }

        boolean handled = false;

        if (this.isFocused())
        {
            if (this.isMouseOver(mouseX, mouseY))
            {
                this.addKey(mouseButton - 100);
                this.updateButtonState();
            }

            handled = true;
        }
        else if (mouseButton == 0)
        {
            this.setFocused(true);
            handled = true;
        }
        else if (mouseButton == 2)
        {
            this.keyBind.clearKeys();
            this.updateButtonState();
            this.notifyListener();
            handled = true;
        }

        if (handled)
        {
            this.playClickSound();
        }

        return handled;
    }

    @Override
    protected boolean onMouseClickedOutside(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isEnabled() && this.isFocused())
        {
            this.playClickSound();
            return true;
        }

        return false;
    }

    @Override
    protected boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (this.isEnabled() && this.isFocused())
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
        if (this.isFocused())
        {
            if (keyCode == Keys.KEY_ESCAPE)
            {
                if (this.firstKey)
                {
                    this.keyBind.clearKeys();
                }

                this.setFocused(false);
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
        return this.isFocused();
    }

    protected void addKey(int keyCode)
    {
        if (MaLiLibConfigs.Hotkeys.IGNORED_KEYS.getKeyBind().containsKey(keyCode))
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

    @Override
    public void setFocused(boolean isFocused)
    {
        super.setFocused(isFocused);

        if (isFocused)
        {
            this.onSelected();
        }
        else
        {
            this.onClearSelection();
        }
    }

    public void onSelected()
    {
        this.firstKey = true;
        this.newKeys.clear();
        this.keyBind.getKeysToList(this.newKeys);
        this.setHoverInfoRequiresShift(false);
        this.updateButtonState();
    }

    public void onClearSelection()
    {
        if (this.firstKey == false)
        {
            this.keyBind.setKeys(this.newKeys);
        }

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

    protected String getCurrentDisplayString()
    {
        String valueStr;
        boolean isEmpty;

        if (this.isFocused())
        {
            valueStr = Keys.writeKeysToString(this.newKeys, " + ", Keys::charAsCharacter);
            isEmpty = this.newKeys.isEmpty();
        }
        else
        {
            valueStr = this.keyBind.getKeysDisplayString();
            isEmpty = this.keyBind.hasKeys() == false;
        }

        if (isEmpty || org.apache.commons.lang3.StringUtils.isBlank(valueStr))
        {
            valueStr = StringUtils.translate("malilib.button.misc.none.caps");
        }

        if (this.isFocused())
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
        return this.isFocused() || this.isEnabled() == false ? EMPTY_STRING_LIST : this.hoverStrings;
    }

    protected void updateConflicts()
    {
        if (this.isFocused())
        {
            return;
        }

        List<String> overlapInfo = new ArrayList<>();

        this.getMalilibHotkeyOverlaps(overlapInfo);
        this.getVanillaKeybindOverlaps(overlapInfo);
        this.buildOverlapInfoHoverStrings(overlapInfo);

        this.hoverInfoFactory.updateList();
    }

    protected void getMalilibHotkeyOverlaps(List<String> overlapInfoOut)
    {
        ImmutableList<HotkeyCategory> categories = Registry.HOTKEY_MANAGER.getHotkeyCategories();
        List<Hotkey> overlaps = new ArrayList<>();

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
                if (overlapInfoOut.size() > 0)
                {
                    overlapInfoOut.add("--------");
                }

                overlapInfoOut.add(category.getModInfo().getModName());
                overlapInfoOut.add(StringUtils.translate("malilib.hover.button.keybind.overlap.category",
                                                         category.getCategoryName()));

                for (Hotkey overlap : overlaps)
                {
                    String translationKey = "malilib.hover.button.keybind.overlap.keybind";
                    String name = overlap.getDisplayName();
                    String keys = overlap.getKeyBind().getKeysDisplayString();
                    overlapInfoOut.add(StringUtils.translate(translationKey, name, keys));
                }

                overlaps.clear();
            }
        }
    }

    protected void getVanillaKeybindOverlaps(List<String> overlapInfoOut)
    {
        IntArrayList keyList = new IntArrayList();
        Map<String, List<KeyBinding>> overlaps = new HashMap<>();

        this.keyBind.getKeysToList(keyList);

        for (KeyBinding vanillaKey : GameUtils.getOptions().keyBindings)
        {
            if (keyList.contains(vanillaKey.getKeyCode()))
            {
                overlaps.computeIfAbsent(vanillaKey.getKeyCategory(), k -> new ArrayList<>()).add(vanillaKey);
            }
        }

        if (overlaps.size() > 0)
        {
            if (overlapInfoOut.size() > 0)
            {
                overlapInfoOut.add("--------");
            }

            overlapInfoOut.add(StringUtils.translate("malilib.hover.button.keybind.overlap.category.vanilla"));

            for (String category : overlaps.keySet())
            {
                overlapInfoOut.add(StringUtils.translate("malilib.hover.button.keybind.overlap.category",
                                                         StringUtils.translate(category)));

                for (KeyBinding overlap : overlaps.get(category))
                {
                    String translationKey = "malilib.hover.button.keybind.overlap.keybind";
                    String name = StringUtils.translate(overlap.getKeyDescription());
                    String keys = GameSettings.getKeyDisplayString(overlap.getKeyCode());
                    overlapInfoOut.add(StringUtils.translate(translationKey, name, keys));
                }
            }
        }
    }

    protected void buildOverlapInfoHoverStrings(List<String> overlapInfo)
    {
        List<String> hoverStrings = new ArrayList<>();
        boolean modified = this.keyBind.isModified();
        boolean nonEmpty = this.keyBind.hasKeys();

        //if (modified)
        {
            String defaultStr = this.keyBind.getDefaultKeysDisplayString();

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

        this.overlapInfoSize = overlapInfo.size();

        if (this.overlapInfoSize > 0)
        {
            if (modified || nonEmpty)
            {
                hoverStrings.add("------------------------------------");
            }

            hoverStrings.add(StringUtils.translate("malilib.hover.button.keybind.possible_overlaps"));
            hoverStrings.add("");
            hoverStrings.addAll(overlapInfo);
        }

        this.hoverStrings.clear();
        this.hoverStrings.addAll(hoverStrings);
    }
}
