package fi.dy.masa.malilib.hotkeys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.MaLiLibConfigs;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.util.IF3KeyStateSetter;
import fi.dy.masa.malilib.util.KeyCodes;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;

public class KeybindMulti implements IKeybind
{
    public static final ConfigBoolean KEYBIND_DEBUG = new ConfigBoolean("keybindDebugging", false, "When enabled, key presses and held keys are\nprinted to the game console (and the action bar, if enabled)");
    public static final ConfigBoolean KEYBIND_DEBUG_ACTIONBAR = new ConfigBoolean("keybindDebuggingIngame", true, "If enabled, then the messages from 'keybindDebugging'\nare also printed to the in-game action bar");

    private static List<Integer> pressedKeys = new ArrayList<>();
    private static int triggeredCount;

    private final String defaultStorageString;
    private final KeybindSettings defaultSettings;
    private List<Integer> keyCodes = new ArrayList<>(4);
    private KeybindSettings settings;
    private boolean pressed;
    private boolean pressedLast;
    private int heldTime;
    @Nullable
    private IHotkeyCallback callback;

    private KeybindMulti(String defaultStorageString, KeybindSettings settings)
    {
        this.defaultStorageString = defaultStorageString;
        this.defaultSettings = settings;
        this.settings = settings;
    }

    @Override
    public KeybindSettings getSettings()
    {
        return this.settings;
    }

    @Override
    public void setSettings(KeybindSettings settings)
    {
        this.settings = settings;
    }

    @Override
    public void setCallback(@Nullable IHotkeyCallback callback)
    {
        this.callback = callback;
    }

    @Override
    public boolean isValid()
    {
        return this.keyCodes.isEmpty() == false;
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
    public boolean isKeybindHeld()
    {
        return this.pressed;
    }

    /**
     * NOT PUBLIC API - DO NOT CALL FROM MOD CODE!!!
     */
    @Override
    public boolean updateIsPressed()
    {
        if (this.isValid() == false ||
            (this.settings.getContext() != KeybindSettings.Context.ANY &&
            ((this.settings.getContext() == KeybindSettings.Context.INGAME) != (Minecraft.getInstance().currentScreen == null))))
        {
            this.pressed = false;
            return false;
        }

        boolean allowExtraKeys = this.settings.getAllowExtraKeys();
        boolean allowOutOfOrder = this.settings.isOrderSensitive() == false;
        boolean pressedLast = this.pressed;
        final int sizePressed = pressedKeys.size();
        final int sizeRequired = this.keyCodes.size();

        if (sizePressed >= sizeRequired && (allowExtraKeys || sizePressed == sizeRequired))
        {
            int keyCodeIndex = 0;
            this.pressed = pressedKeys.containsAll(this.keyCodes);

            for (int i = 0; i < sizePressed; ++i)
            {
                Integer keyCodeObj = pressedKeys.get(i);

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
            (triggeredCount == 0 || this.settings.isExclusive() == false) &&
            (activateOn == KeyAction.BOTH || this.pressed == (activateOn == KeyAction.PRESS)))
        {
            boolean cancel = this.triggerKeyAction(pressedLast) && this.settings.shouldCancel();
            //System.out.printf("triggered, cancel: %s, triggeredCount: %d\n", cancel, triggeredCount);

            if (cancel)
            {
                ++triggeredCount;
            }

            return cancel;
        }

        return false;
    }

    private boolean triggerKeyAction(boolean pressedLast)
    {
        boolean cancel = false;

        if (this.pressed == false)
        {
            this.heldTime = 0;
            KeyAction activateOn = this.settings.getActivateOn();

            if (pressedLast && this.callback != null && (activateOn == KeyAction.RELEASE || activateOn == KeyAction.BOTH))
            {
                cancel = this.callback.onKeyAction(KeyAction.RELEASE, this);
            }
        }
        else if (pressedLast == false && this.heldTime == 0)
        {
            if (this.keyCodes.contains(KeyCodes.KEY_F3))
            {
                // Prevent the debug GUI from opening after the F3 key is released
                ((IF3KeyStateSetter) Minecraft.getInstance().keyboardListener).setF3KeyState(true);
            }

            KeyAction activateOn = this.settings.getActivateOn();

            if (this.callback != null && (activateOn == KeyAction.PRESS || activateOn == KeyAction.BOTH))
            {
                cancel = this.callback.onKeyAction(KeyAction.PRESS, this);
            }
        }

        return cancel;
    }

    @Override
    public void clearKeys()
    {
        this.keyCodes.clear();
        this.pressed = false;
        this.heldTime = 0;
    }

    @Override
    public void addKey(int keyCode)
    {
        if (this.keyCodes.contains(keyCode) == false)
        {
            this.keyCodes.add(keyCode);
        }
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
    public void removeKey(int keyCode)
    {
        this.keyCodes.remove(keyCode);
    }

    @Override
    public Collection<Integer> getKeys()
    {
        return this.keyCodes;
    }

    @Override
    public String getKeysDisplayString()
    {
        return this.getStringValue().replaceAll(",", " + ");
    }

    /**
     * Returns true if the keybind has been changed from the default value
     */
    @Override
    public boolean isModified()
    {
        return this.getStringValue().equals(this.defaultStorageString) == false;
    }

    @Override
    public boolean isModified(String newValue)
    {
        return this.defaultStorageString.equals(newValue) == false;
    }

    @Override
    public void resetToDefault()
    {
        this.setValueFromString(this.defaultStorageString);
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
    public String getStringValue()
    {
        StringBuilder sb = new StringBuilder(32);

        for (int i = 0; i < this.keyCodes.size(); ++i)
        {
            if (i > 0)
            {
                sb.append(",");
            }

            int keyCode = this.keyCodes.get(i).intValue();
            String name = getStorageStringForKeyCode(keyCode);

            if (name != null)
            {
                sb.append(name);
            }
        }

        return sb.toString();
    }

    @Override
    public String getDefaultStringValue()
    {
        return this.defaultStorageString;
    }

    @Override
    public void setValueFromString(String str)
    {
        this.clearKeys();
        String[] keys = str.split(",");

        for (String keyName : keys)
        {
            keyName = keyName.trim();

            if (keyName.isEmpty() == false)
            {
                int keyCode = KeyCodes.getKeyCodeFromName(keyName);

                if (keyCode != KeyCodes.KEY_NONE)
                {
                    this.addKey(keyCode);
                }
            }
        }
    }

    public static KeybindMulti fromStorageString(String str, KeybindSettings settings)
    {
        KeybindMulti keybind = new KeybindMulti(str, settings);
        keybind.setValueFromString(str);
        return keybind;
    }

    public static boolean isKeyDown(int keyCode)
    {
        if (keyCode >= 0)
        {
            return InputMappings.isKeyDown(keyCode);
        }

        keyCode += 100;

        return keyCode >= 0 && InputMappings.isKeyDown(keyCode);
    }

    /**
     * NOT PUBLIC API - DO NOT CALL FROM MOD CODE!!!
     */
    public static void onKeyInputPre(int keyCode, int scanCode, boolean state)
    {
        Integer valObj = Integer.valueOf(keyCode);

        if (state)
        {
            if (pressedKeys.contains(valObj) == false)
            {
                Collection<Integer> ignored = MaLiLibConfigs.Generic.IGNORED_KEYS.getKeybind().getKeys();

                if (ignored.size() == 0 || ignored.contains(valObj) == false)
                {
                    pressedKeys.add(valObj);
                }
            }
        }
        else
        {
            pressedKeys.remove(valObj);
        }

        if (KEYBIND_DEBUG.getBooleanValue())
        {
            printKeybindDebugMessage(keyCode, scanCode, state);
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL FROM MOD CODE!!!
     */
    public static void reCheckPressedKeys()
    {
        Iterator<Integer> iter = pressedKeys.iterator();

        while (iter.hasNext())
        {
            int keyCode = iter.next().intValue();

            if (isKeyDown(keyCode) == false)
            {
                iter.remove();
            }
        }

        // Clear the triggered count after all keys have been released
        if (pressedKeys.size() == 0)
        {
            triggeredCount = 0;
        }
    }

    private static void printKeybindDebugMessage(int keyCode, int scanCode, boolean keyState)
    {
        String keyName = keyCode != KeyCodes.KEY_NONE ? KeyCodes.getNameForKey(keyCode) : "<unknown>";
        String type = keyState ? "PRESS" : "RELEASE";
        String held = getActiveKeysString();
        String msg = String.format("%s %s (%d), held keys: %s", type, keyName, keyCode, held);

        MaLiLib.logger.info(msg);

        if (KEYBIND_DEBUG_ACTIONBAR.getBooleanValue())
        {
            StringUtils.printActionbarMessage(msg);
        }
    }

    public static String getActiveKeysString()
    {
        if (pressedKeys.isEmpty() == false)
        {
            StringBuilder sb = new StringBuilder(128);
            int i = 0;

            for (int key : pressedKeys)
            {
                if (i > 0)
                {
                    sb.append(" + ");
                }

                String name = getStorageStringForKeyCode(key);

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

    @Nullable
    public static String getStorageStringForKeyCode(int keyCode)
    {
        return KeyCodes.getNameForKey(keyCode);
    }
}
