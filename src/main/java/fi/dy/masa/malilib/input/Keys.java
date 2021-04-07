package fi.dy.masa.malilib.input;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.MaLiLib;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class Keys
{
    private static final Pattern PATTERN_CHAR_CODE = Pattern.compile("^CHAR_(?<code>[0-9]+)$");

    private static final Object2IntOpenHashMap<String> NAMES_TO_IDS = new Object2IntOpenHashMap<>();
    private static final Int2ObjectOpenHashMap<String> IDS_TO_NAMES = new Int2ObjectOpenHashMap<>();

    static
    {
        initKeys();
    }

    /**
     * @return the run-time key code for the given key name,
     *         or Keyboard.KEY_NONE if no valid id could be determined.
     */
    public static int getKeyCodeForStorageString(String keyName)
    {
        int keyCode = NAMES_TO_IDS.getInt(keyName);

        if (keyCode == Keyboard.KEY_NONE)
        {
            keyCode = Keyboard.getKeyIndex(keyName);
        }

        if (keyCode == Keyboard.KEY_NONE)
        {
            Matcher matcher = PATTERN_CHAR_CODE.matcher(keyName);

            if (matcher.matches())
            {
                try
                {
                    keyCode = Integer.parseInt(matcher.group("code")) + 256;
                }
                catch (Exception ignore) {}
            }
        }

        if (keyCode == Keyboard.KEY_NONE)
        {
            keyCode = Mouse.getButtonIndex(keyName);

            if (keyCode >= 0 && keyCode < Mouse.getButtonCount())
            {
                keyCode -= 100;
            }
            else
            {
                keyCode = Keyboard.KEY_NONE;
            }
        }

        return keyCode;
    }

    /**
     * @param keyCode the key code for which the name is being requested
     * @param charEncoder the character encoder to use in case they key code value is 256 or greater. In such a case the value the encoder gets is keyCode - 256.
     * @return Returns the string to store in the config files for the given key code.
     *         If no valid mapping could be determined, then null is returned.
     */
    @Nullable
    public static String getStorageStringForKeyCode(int keyCode, Function<Integer, String> charEncoder)
    {
        String name = IDS_TO_NAMES.get(keyCode);

        if (name != null)
        {
            return name;
        }

        if (keyCode > 0 && keyCode < 256)
        {
            return Keyboard.getKeyName(keyCode);
        }
        else if (keyCode >= 256)
        {
            return charEncoder.apply(keyCode - 256);
        }
        else if (keyCode < 0)
        {
            keyCode += 100;

            if (keyCode >= 0 && keyCode < Mouse.getButtonCount())
            {
                return Mouse.getButtonName(keyCode);
            }
        }

        return null;
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

    public static ImmutableList<Integer> readKeysFromStorageString(String str)
    {
        ArrayList<Integer> keyCodes = new ArrayList<>();
        String[] keys = str.split(",");

        for (String keyName : keys)
        {
            keyName = keyName.trim();

            if (keyName.isEmpty() == false)
            {
                int keyCode = getKeyCodeForStorageString(keyName);

                if (keyCode != Keyboard.KEY_NONE && keyCodes.contains(keyCode) == false)
                {
                    keyCodes.add(keyCode);
                }
            }
        }

        return ImmutableList.copyOf(keyCodes);
    }

    public static String writeKeysToString(List<Integer> keyCodes, String separator, Function<Integer, String> charEncoder)
    {
        StringBuilder sb = new StringBuilder(32);

        for (int i = 0; i < keyCodes.size(); ++i)
        {
            if (i > 0)
            {
                sb.append(separator);
            }

            int keyCode = keyCodes.get(i).intValue();
            String name = getStorageStringForKeyCode(keyCode, charEncoder);

            if (name != null)
            {
                sb.append(name);
            }
        }

        return sb.toString();
    }

    public static String charAsStorageString(int charIn)
    {
        return String.format("CHAR_%d", charIn);
    }

    public static String charAsCharacter(int charIn)
    {
        return String.valueOf((char) (charIn & 0xFF));
    }

    public static void initKeys()
    {
        NAMES_TO_IDS.defaultReturnValue(Keyboard.KEY_NONE);

        addNameOverride(Keyboard.KEY_LMENU, "L_ALT");
        addNameOverride(Keyboard.KEY_RMENU, "R_ALT");
        addNameOverride(Keyboard.KEY_LSHIFT, "L_SHIFT");
        addNameOverride(Keyboard.KEY_RSHIFT, "R_SHIFT");
        addNameOverride(Keyboard.KEY_LCONTROL, "L_CTRL");
        addNameOverride(Keyboard.KEY_RCONTROL, "R_CTRL");
        addNameOverride(-100, "LMB");
        addNameOverride(-99, "RMB");
        addNameOverride(-98, "MMB");
        addNameOverride(-199, "SCROLL_UP");
        addNameOverride(-201, "SCROLL_DOWN");

        addLoadableNames(Keyboard.KEY_LMENU, "LMENU", "L_MENU", "LALT", "L_ALT", "LEFT_ALT");
        addLoadableNames(Keyboard.KEY_RMENU, "RMENU", "R_MENU", "RALT", "R_ALT", "RIGHT_ALT");
        addLoadableNames(Keyboard.KEY_LSHIFT, "LSHIFT", "L_SHIFT", "LEFT_SHIFT");
        addLoadableNames(Keyboard.KEY_RSHIFT, "RSHIFT", "R_SHIFT", "RIGHT_SHIFT");
        addLoadableNames(Keyboard.KEY_LCONTROL, "LCTRL", "L_CTRL", "LEFT_CTRL", "LCONTROL", "L_CONTROL", "LEFT_CONTROL");
        addLoadableNames(Keyboard.KEY_RCONTROL, "RCTRL", "R_CTRL", "RIGHT_CTRL", "RCONTROL", "R_CONTROL", "RIGHT_CONTROL");
        addLoadableNames(-100, "LMB", "BUTTON0", "MOUSE0");
        addLoadableNames(-99, "RMB", "BUTTON1", "MOUSE1");
        addLoadableNames(-98, "MMB", "BUTTON2", "MOUSE2");
        addLoadableNames(-199, "SCROLL_UP");
        addLoadableNames(-201, "SCROLL_DOWN");
    }

    /**
     * Adds names for the given keyCode, which can be loaded from the config file.
     * This is basically the name -> id mapping used when loading the config files.
     * @param keyCode the key code that the provided names will be loaded as
     * @param names one or more names that should be loaded as the given keyCode value
     */
    public static void addLoadableNames(int keyCode, String... names)
    {
        for (String name : names)
        {
            if (NAMES_TO_IDS.containsKey(name))
            {
                MaLiLib.LOGGER.warn("Duplicate key fallback name '{}'", name);
                continue;
            }

            NAMES_TO_IDS.put(name, keyCode);
        }
    }

    /**
     * Adds an id -> name override, which determines what the config file will contain for a given keyCode.
     * @param name the name to use for this keyCode in the config files
     */
    public static void addNameOverride(int keyCode, String name)
    {
        if (IDS_TO_NAMES.containsKey(keyCode) == false)
        {
            IDS_TO_NAMES.put(keyCode, name);
        }
        else
        {
            MaLiLib.LOGGER.warn("Duplicate key override name '{}'", name);
        }
    }
}
