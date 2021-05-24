package fi.dy.masa.malilib.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MinecraftClientAccessor;
import fi.dy.masa.malilib.config.value.KeybindDisplayMode;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.input.callback.AdjustableValueHotkeyCallback;
import fi.dy.masa.malilib.input.callback.HotkeyCallback;
import fi.dy.masa.malilib.overlay.message.MessageUtils;
import fi.dy.masa.malilib.render.text.StyledText;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class KeyBindImpl implements KeyBind
{
    private static final List<Integer> PRESSED_KEYS = new ArrayList<>();

    public static final KeyUpdateResult NO_ACTION = new KeyUpdateResult(false, false);

    private static int triggeredCount;

    private final KeyBindSettings defaultSettings;
    private final ImmutableList<Integer> defaultKeyCodes;
    private ImmutableList<Integer> keyCodes = ImmutableList.of();
    private ImmutableList<Integer> lastSavedKeyCodes;
    private KeyBindSettings settings;
    private KeyBindSettings lastSavedSettings;
    private ModInfo modInfo;
    private String nameTranslationKey = "";
    private boolean pressed;
    private boolean pressedLast;
    private int heldTime;
    @Nullable private HotkeyCallback callback;

    private KeyBindImpl(String defaultStorageString, KeyBindSettings settings)
    {
        this.defaultSettings = settings;
        this.defaultKeyCodes = Keys.readKeysFromStorageString(defaultStorageString);
        this.settings = settings;

        this.cacheSavedValue();
    }

    @Override
    public void setModInfo(ModInfo modInfo)
    {
        this.modInfo = modInfo;
    }

    @Override
    public void setNameTranslationKey(String nameTranslationKey)
    {
        this.nameTranslationKey = nameTranslationKey;
    }

    @Override
    public KeyBindSettings getSettings()
    {
        return this.settings;
    }

    @Override
    public KeyBindSettings getDefaultSettings()
    {
        return this.defaultSettings;
    }

    @Override
    public void setSettings(KeyBindSettings settings)
    {
        this.settings = settings;
    }

    @Override
    public void setCallback(@Nullable HotkeyCallback callback)
    {
        this.callback = callback;
    }

    @Override
    public boolean isValid()
    {
        return this.keyCodes.isEmpty() == false || this.settings.getAllowEmpty();
    }

    /**
     * Checks if this keybind is now active but previously was not active,
     * and then updates the cached state.
     * @return true if this keybind just became pressed
     */
    @Override
    public boolean isPressed()
    {
        return this.pressed && this.pressedLast == false && this.heldTime == 0;
    }

    @Override
    public boolean isKeyBindHeld()
    {
        return this.pressed || (this.settings.getAllowEmpty() && this.keyCodes.isEmpty());
    }

    /**
     * NOT PUBLIC API - DO NOT CALL FROM MOD CODE!!!
     */
    @Override
    public KeyUpdateResult updateIsPressed(boolean isFirst)
    {
        if (this.keyCodes.isEmpty() ||
            (this.settings.getContext() != Context.ANY &&
            ((this.settings.getContext() == Context.INGAME) != (GuiUtils.getCurrentScreen() == null))))
        {
            this.pressed = false;
            return NO_ACTION;
        }

        final boolean allowExtraKeys = this.settings.getAllowExtraKeys();
        final boolean allowOutOfOrder = this.settings.isOrderSensitive() == false;
        final boolean pressedLast = this.pressed;
        final int sizePressed = PRESSED_KEYS.size();
        final int sizeRequired = this.keyCodes.size();

        if (sizePressed >= sizeRequired && (allowExtraKeys || sizePressed == sizeRequired))
        {
            int keyCodeIndex = 0;
            this.pressed = PRESSED_KEYS.containsAll(this.keyCodes);

            for (Integer keyCodeObj : PRESSED_KEYS)
            {
                if (this.keyCodes.get(keyCodeIndex).equals(keyCodeObj))
                {
                    // Fully matched keybind
                    if (++keyCodeIndex >= sizeRequired)
                    {
                        break;
                    }
                }
                else if ((allowOutOfOrder == false && (keyCodeIndex > 0 || sizePressed == sizeRequired)) ||
                         (this.keyCodes.contains(keyCodeObj) == false && allowExtraKeys == false))
                {
                    /*
                    System.out.printf("km fail: key: %s, ae: %s, aoo: %s, cont: %s, keys: %s, pressed: %s, triggeredCount: %d\n",
                            keyCodeObj, allowExtraKeys, allowOutOfOrder, this.keyCodes.contains(keyCodeObj), this.keyCodes, pressedKeys, triggeredCount);
                    */
                    this.pressed = false;
                    break;
                }
            }
        }
        else
        {
            this.pressed = false;
        }

        KeyAction activateOn = this.settings.getActivateOn();

        if (this.pressed != pressedLast &&
            (isFirst || this.settings.getFirstOnly() == false) &&
            (triggeredCount == 0 || this.settings.isExclusive() == false) &&
            (activateOn == KeyAction.BOTH || this.pressed == (activateOn == KeyAction.PRESS)))
        {
            KeyUpdateResult result = this.triggerKeyAction(pressedLast);
            //System.out.printf("triggered, cancel: %s, triggeredCount: %d\n", cancel, triggeredCount);

            if (result.triggered)
            {
                ++triggeredCount;
            }

            return result;
        }

        return NO_ACTION;
    }

    private KeyUpdateResult triggerKeyAction(boolean pressedLast)
    {
        KeyAction activateOn = this.settings.getActivateOn();

        if (this.pressed == false)
        {
            this.heldTime = 0;

            if (pressedLast && (activateOn == KeyAction.RELEASE || activateOn == KeyAction.BOTH))
            {
                return this.triggerKeyCallback(KeyAction.RELEASE);
            }
        }
        else if (pressedLast == false && this.heldTime == 0)
        {
            if (this.keyCodes.contains(Keyboard.KEY_F3))
            {
                // Prevent the debug GUI from opening after the F3 key is released
                ((MinecraftClientAccessor) Minecraft.getMinecraft()).setActionKeyF3(true);
            }

            if (activateOn == KeyAction.PRESS || activateOn == KeyAction.BOTH)
            {
                return this.triggerKeyCallback(KeyAction.PRESS);
            }
        }

        return NO_ACTION;
    }

    private KeyUpdateResult triggerKeyCallback(KeyAction action)
    {
        boolean cancel;

        if (this.callback == null)
        {
            cancel = action == KeyAction.PRESS && this.settings.getCancelCondition() == CancelCondition.ALWAYS;
        }
        else
        {
            ActionResult result = this.callback.onKeyAction(action, this);
            CancelCondition condition = this.settings.getCancelCondition();
            cancel = action == KeyAction.PRESS && (condition == CancelCondition.ALWAYS
                     || (result == ActionResult.SUCCESS && condition == CancelCondition.ON_SUCCESS)
                     || (result == ActionResult.FAIL    && condition == CancelCondition.ON_FAILURE));
        }

        this.addToastMessage(action, this.callback != null, cancel);

        return new KeyUpdateResult(cancel, true);
    }

    private void addToastMessage(KeyAction action, boolean hasCallback, boolean cancelled)
    {
        KeybindDisplayMode mode = MaLiLibConfigs.Generic.KEYBIND_DISPLAY.getValue();
        boolean showCallbackOnly = MaLiLibConfigs.Generic.KEYBIND_DISPLAY_CALLBACK_ONLY.getBooleanValue();
        boolean showCancelledOnly = MaLiLibConfigs.Generic.KEYBIND_DISPLAY_CANCEL_ONLY.getBooleanValue();
        boolean isBoth = this.settings.activateOn == KeyAction.BOTH;

        // FIXME This check is not great here, but should reduce some spam from the scroll adjustable hotkeys
        boolean isAdjustable = isBoth && this.callback instanceof AdjustableValueHotkeyCallback;
        boolean canShowAdjustable = isAdjustable == false || action == KeyAction.RELEASE;

        if (this.settings.getShowToast() &&
            mode != KeybindDisplayMode.NONE &&
            (showCancelledOnly == false || cancelled || (isAdjustable && action == KeyAction.RELEASE)) &&
            (showCallbackOnly == false || hasCallback) &&
            canShowAdjustable)
        {
            List<String> lines = new ArrayList<>();

            if (mode == KeybindDisplayMode.KEYS || mode == KeybindDisplayMode.KEYS_ACTIONS)
            {
                lines.add(StringUtils.translate("malilib.toast.keybind_display.keys", this.getKeysDisplayString()));
            }

            if ((mode == KeybindDisplayMode.ACTIONS || mode == KeybindDisplayMode.KEYS_ACTIONS) && this.modInfo != null)
            {
                String name = StringUtils.translate(this.nameTranslationKey);
                lines.add(StringUtils.translate("malilib.toast.keybind_display.action", this.modInfo.getModName(), name));
            }

            StyledText text = StyledText.ofStrings(lines);
            int lifeTimeMs = MaLiLibConfigs.Generic.KEYBIND_DISPLAY_DURATION.getIntegerValue();

            MessageUtils.addToastMessage(text, lifeTimeMs, "keybind_display", true);
        }
    }

    @Override
    public void clearKeys()
    {
        this.keyCodes = ImmutableList.of();
        this.pressed = false;
        this.heldTime = 0;
    }

    @Override
    public void setKeys(List<Integer> newKeys)
    {
        this.keyCodes = ImmutableList.copyOf(newKeys);
    }

    @Override
    public void tick()
    {
        if (this.pressed)
        {
            this.heldTime++;
        }

        this.pressedLast = this.pressed;
    }

    @Override
    public ImmutableList<Integer> getKeys()
    {
        return this.keyCodes;
    }

    @Override
    public ImmutableList<Integer> getDefaultKeys()
    {
        return this.defaultKeyCodes;
    }

    @Override
    public String getKeysDisplayString()
    {
        return Keys.writeKeysToString(this.keyCodes, " + ", Keys::charAsCharacter);
    }

    /**
     * Returns true if the keybind has been changed from the default value
     */
    @Override
    public boolean isModified()
    {
        return this.keyCodes.equals(this.defaultKeyCodes) == false;
    }

    @Override
    public boolean isDirty()
    {
        return this.lastSavedKeyCodes.equals(this.keyCodes) == false ||
               this.lastSavedSettings.equals(this.settings) == false;
    }

    @Override
    public void cacheSavedValue()
    {
        this.lastSavedKeyCodes = this.keyCodes;
        this.lastSavedSettings = this.settings;
    }

    @Override
    public void resetToDefault()
    {
        this.keyCodes = this.defaultKeyCodes;
    }

    @Override
    public boolean areSettingsModified()
    {
        return this.settings.equals(this.defaultSettings) == false;
    }

    @Override
    public void resetSettingsToDefaults()
    {
        this.settings = this.defaultSettings;
    }

    @Override
    public void setValueFromString(String str)
    {
        this.clearKeys();
        this.keyCodes = Keys.readKeysFromStorageString(str);
    }

    @Override
    public boolean matches(int keyCode)
    {
        return this.keyCodes.size() == 1 && this.keyCodes.get(0) == keyCode;
    }

    public static boolean hotkeyMatchesKeyBind(Hotkey hotkey, KeyBinding keybind)
    {
        return hotkey.getKeyBind().matches(keybind.getKeyCode());
    }

    @Override
    public boolean overlaps(KeyBind other)
    {
        if (other == this || other.getKeys().size() > this.getKeys().size())
        {
            return false;
        }

        if (this.contextOverlaps(other))
        {
            KeyBindSettings settingsOther = other.getSettings();
            List<Integer> keys1 = this.getKeys();
            List<Integer> keys2 = other.getKeys();
            boolean o1 = this.settings.isOrderSensitive();
            boolean o2 = settingsOther.isOrderSensitive();
            int l1 = keys1.size();
            int l2 = keys2.size();

            if (l1 == 0 || l2 == 0)
            {
                return false;
            }

            boolean firstMatches = keys1.get(0).equals(keys2.get(0));

            if (firstMatches == false &&
                ((this.settings.getAllowExtraKeys() == false && l1 < l2) ||
                 (settingsOther.getAllowExtraKeys() == false && l2 < l1)))
            {
                return false;
            }

            // Both are order sensitive, try to "slide the shorter sequence over the longer sequence" to find a match
            if (o1 && o2)
            {
                return l1 < l2 ? Collections.indexOfSubList(keys2, keys1) != -1 : Collections.indexOfSubList(keys1, keys2) != -1;
            }
            // At least one of the keybinds is not order sensitive
            else
            {
                return l1 <= l2 ? keys2.containsAll(keys1) : keys1.containsAll(keys2);
            }
        }

        return false;
    }

    public boolean contextOverlaps(KeyBind other)
    {
        KeyBindSettings settingsOther = other.getSettings();
        Context c1 = this.settings.getContext();
        Context c2 = settingsOther.getContext();

        if (c1 == Context.ANY || c2 == Context.ANY || c1 == c2)
        {
            KeyAction a1 = this.settings.getActivateOn();
            KeyAction a2 = settingsOther.getActivateOn();

            return a1 == KeyAction.BOTH || a2 == KeyAction.BOTH || a1 == a2;
        }

        return false;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element, String hotkeyName)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();

                if (JsonUtils.hasString(obj, "keys"))
                {
                    this.setValueFromString(obj.get("keys").getAsString());
                }

                if (JsonUtils.hasObject(obj, "settings"))
                {
                    this.setSettings(KeyBindSettings.fromJson(obj.getAsJsonObject("settings")));
                }
            }
            // Backwards compatibility with some old hotkeys
            else if (element.isJsonPrimitive())
            {
                this.setValueFromString(element.getAsString());
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to set the hotkey '{}' from the JSON element '{}'", hotkeyName, element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set the hotkey '{}' from the JSON element '{}'", hotkeyName, element, e);
        }

        this.cacheSavedValue();
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        JsonObject obj = new JsonObject();

        String str = Keys.writeKeysToString(this.keyCodes, ",", Keys::charAsStorageString);
        obj.add("keys", new JsonPrimitive(str));

        if (this.areSettingsModified())
        {
            obj.add("settings", this.getSettings().toJson());
        }

        return obj;
    }

    public static KeyBindImpl fromStorageString(String storageString, KeyBindSettings settings)
    {
        KeyBindImpl keyBind = new KeyBindImpl(storageString, settings);
        keyBind.setValueFromString(storageString);
        return keyBind;
    }

    /**
     * NOT PUBLIC API - DO NOT CALL FROM MOD CODE!!!
     */
    public static void onKeyInputPre(int keyCode, int scanCode, int modifiers, char charIn, boolean keyState)
    {
        Integer valObj = keyCode;

        if (keyState)
        {
            if (PRESSED_KEYS.contains(valObj) == false)
            {
                List<Integer> ignored = MaLiLibConfigs.Hotkeys.IGNORED_KEYS.getKeyBind().getKeys();

                if (ignored.size() == 0 || ignored.contains(valObj) == false)
                {
                    PRESSED_KEYS.add(valObj);
                }
            }
        }
        else
        {
            PRESSED_KEYS.remove(valObj);
        }

        if (MaLiLibConfigs.Generic.PRESSED_KEYS_TOAST.getBooleanValue())
        {
            String heldKeys;

            if (PRESSED_KEYS.isEmpty())
            {
                heldKeys = StringUtils.translate("malilib.label.none.brackets");
            }
            else
            {
                heldKeys = Keys.writeKeysToString(PRESSED_KEYS, " + ", Keys::charAsStorageString);
            }

            StyledText text = StyledText.translate("malilib.label.pressed_keys_toast", heldKeys);
            MessageUtils.addToastMessage(text, 2000, "pressed_keys", false);
        }

        if (MaLiLibConfigs.Debug.KEYBIND_DEBUG.getBooleanValue())
        {
            printKeyBindDebugMessage(keyCode, scanCode, modifiers, charIn, keyState);
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL FROM MOD CODE!!!
     */
    public static void reCheckPressedKeys()
    {
        Iterator<Integer> iter = PRESSED_KEYS.iterator();

        while (iter.hasNext())
        {
            int keyCode = iter.next().intValue();

            if (Keys.isKeyDown(keyCode) == false)
            {
                iter.remove();
            }
        }

        // Clear the triggered count after all keys have been released
        if (PRESSED_KEYS.size() == 0)
        {
            triggeredCount = 0;
        }
    }

    public static int getCurrentlyPressedKeysCount()
    {
        return PRESSED_KEYS.size();
    }

    public static int getTriggeredCount()
    {
        return triggeredCount;
    }

    public static void printKeyBindDebugMessage(int keyCode, int scanCode, int modifiers, char charIn, boolean keyState)
    {
        String action = keyState ? "PRESS  " : "RELEASE";
        String keyName = Keys.getStorageStringForKeyCode(keyCode, Keys::charAsStorageString);
        String held = getActiveKeysString();
        String msg = String.format("%s '%s' (k: %d, s: %d, m: %d, c: '%c' = %d), held keys: %s",
                                   action, keyName, keyCode, scanCode, modifiers, charIn, (int) charIn, held);

        MaLiLib.LOGGER.info(msg);

        if (MaLiLibConfigs.Debug.KEYBIND_DEBUG_ACTIONBAR.getBooleanValue())
        {
            MessageUtils.printCustomActionbarMessage(msg);
        }

        if (MaLiLibConfigs.Debug.KEYBIND_DEBUG_TOAST.getBooleanValue())
        {
            StyledText text = StyledText.of(msg);
            MessageUtils.addToastMessage(text, 5000, "keybind_debug", false);
        }
    }

    public static String getActiveKeysString()
    {
        if (PRESSED_KEYS.isEmpty() == false)
        {
            StringBuilder sb = new StringBuilder(128);
            int i = 0;

            for (int key : PRESSED_KEYS)
            {
                if (i > 0)
                {
                    sb.append(" + ");
                }

                String name = Keys.getStorageStringForKeyCode(key, Keys::charAsCharacter);

                if (name != null)
                {
                    sb.append(String.format("%s (%d)", name, key));
                }

                i++;
            }

            return sb.toString();
        }

        return "<none>";
    }
}
