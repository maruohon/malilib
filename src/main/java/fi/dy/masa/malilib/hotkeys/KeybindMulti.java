package fi.dy.masa.malilib.hotkeys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.LiteModMaLiLib;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.util.IMinecraftAccessor;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.Minecraft;

public class KeybindMulti implements IKeybind
{
    public static final ConfigBoolean KEYBIND_DEBUG = new ConfigBoolean("keybindDebugging", false, "When enabled, key presses and held keys are printed to the action bar and console");

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

    @Override
    public boolean updateIsPressed()
    {
        if (this.isValid() == false ||
            (this.settings.getContext() != KeybindSettings.Context.ANY &&
            ((this.settings.getContext() == KeybindSettings.Context.INGAME) != (Minecraft.getMinecraft().currentScreen == null))))
        {
            return false;
        }

        boolean allowExtraKeys = this.settings.getAllowExtraKeys();
        boolean allowOutOfOrder = this.settings.isOrderSensitive() == false;
        boolean pressedLast = this.pressed;

        if (pressedKeys.size() >= this.keyCodes.size() && (allowExtraKeys || pressedKeys.size() == this.keyCodes.size()))
        {
            final int numKeys = this.keyCodes.size();
            int keyCodeIndex = 0;
            this.pressed = true;

            for (int i = 0; i < pressedKeys.size(); ++i)
            {
                Integer keyCodeObj = pressedKeys.get(i);

                if (this.keyCodes.get(keyCodeIndex).equals(keyCodeObj))
                {
                    // Fully matched keybind
                    if (++keyCodeIndex >= numKeys)
                    {
                        break;
                    }
                }
                else if (allowOutOfOrder == false || (this.keyCodes.contains(keyCodeObj) == false && allowExtraKeys == false))
                {
                    this.pressed = false;
                    break;
                }
            }
        }
        else
        {
            this.pressed = false;
        }

        if (this.pressed != pressedLast && this.pressed == (this.settings.getActivateOn() == KeyAction.PRESS))
        {
            if (triggeredCount == 0 || this.settings.isExclusive() == false)
            {
                boolean cancel = this.triggerKeyAction(pressedLast) && this.settings.shouldCancel();

                if (cancel)
                {
                    ++triggeredCount;
                }

                return cancel;
            }
        }

        return false;
    }

    private boolean triggerKeyAction(boolean pressedLast)
    {
        boolean cancel = false;

        if (this.pressed == false)
        {
            this.heldTime = 0;

            if (pressedLast && this.callback != null && this.settings.getActivateOn() == KeyAction.RELEASE)
            {
                cancel = this.callback.onKeyAction(KeyAction.RELEASE, this);
            }
        }
        else if (pressedLast == false && this.heldTime == 0)
        {
            if (this.keyCodes.contains(Keyboard.KEY_F3))
            {
                // Prevent the debug GUI from opening after the F3 key is released
                ((IMinecraftAccessor) Minecraft.getMinecraft()).setActionKeyF3(true);
            }

            if (this.callback != null && this.settings.getActivateOn() == KeyAction.PRESS)
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
        return ImmutableList.copyOf(this.keyCodes);
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

            if (keyCode > 0)
            {
                sb.append(Keyboard.getKeyName(keyCode));
            }
            else
            {
                keyCode += 100;

                if (keyCode >= 0 && keyCode < Mouse.getButtonCount())
                {
                    sb.append(Mouse.getButtonName(keyCode));
                }
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

        for (String key : keys)
        {
            key = key.trim();

            if (key.isEmpty() == false)
            {
                int keyCode = Keyboard.getKeyIndex(key);

                if (keyCode != Keyboard.KEY_NONE)
                {
                    this.addKey(keyCode);
                    continue;
                }

                keyCode = Mouse.getButtonIndex(key);

                if (keyCode >= 0 && keyCode < Mouse.getButtonCount())
                {
                    this.addKey(keyCode - 100);
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
        if (keyCode > 0)
        {
            return keyCode < Keyboard.getKeyCount() && Keyboard.isKeyDown(keyCode);
        }

        keyCode += 100;

        return keyCode >= 0 && keyCode < Mouse.getButtonCount() && Mouse.isButtonDown(keyCode);
    }

    public static void onKeyInputPre(int keyCode, boolean state)
    {
        reCheckPressedKeys();
        Integer valObj = Integer.valueOf(keyCode);

        if (state)
        {
            if (pressedKeys.contains(valObj) == false)
            {
                pressedKeys.add(valObj);
            }
        }
        else
        {
            pressedKeys.remove(valObj);
        }

        if (KEYBIND_DEBUG.getBooleanValue())
        {
            printKeybindDebugMessage(keyCode, state);
        }
    }

    public static void onKeyInputPost()
    {
        // Clear the triggered count after all keys have been released
        if (pressedKeys.size() == 0)
        {
            triggeredCount = 0;
        }
    }

    private static void reCheckPressedKeys()
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
    }

    private static void printKeybindDebugMessage(int eventKey, boolean eventKeyState)
    {
        String keyName = eventKey > 0 ? Keyboard.getKeyName(eventKey) : Mouse.getButtonName(eventKey + 100);
        String type = eventKeyState ? "pressed" : "released";
        String held = KeybindMulti.getActiveKeysString();
        String msg = String.format("%s %s, held keys: %s", type, keyName, held);
        StringUtils.printActionbarMessage(msg);
        LiteModMaLiLib.logger.info(msg);
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

                String name;

                if (key > 0)
                {
                    name = Keyboard.getKeyName(key);
                }
                else
                {
                    key += 100;

                    if (key >= 0)
                    {
                        name = Mouse.getButtonName(key);
                    }
                    else
                    {
                        name = "<unknown>";
                    }
                }

                sb.append(String.format("%s (%d)", name, key));
                i++;
            }

            return sb.toString();
        }

        return "<none>";
    }
}
