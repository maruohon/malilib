package fi.dy.masa.malilib.input;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
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

    public static void initKeys()
    {
        NAMES_TO_IDS.defaultReturnValue(Keyboard.KEY_NONE);

        addNameOverride(Keyboard.KEY_LMENU, "L_ALT");
        addNameOverride(Keyboard.KEY_RMENU, "R_ALT");
        addNameOverride(Keyboard.KEY_LSHIFT, "L_SHIFT");
        addNameOverride(Keyboard.KEY_RSHIFT, "R_SHIFT");
        addNameOverride(Keyboard.KEY_LCONTROL, "L_CTRL");
        addNameOverride(Keyboard.KEY_RCONTROL, "R_CTRL");
        addNameOverride(-199, "SCROLL_UP");
        addNameOverride(-201, "SCROLL_DOWN");

        addFallBackNames(Keyboard.KEY_LMENU, "LMENU", "L_MENU", "LALT", "L_ALT", "LEFT_ALT");
        addFallBackNames(Keyboard.KEY_RMENU, "RMENU", "R_MENU", "RALT", "R_ALT", "RIGHT_ALT");
        addFallBackNames(Keyboard.KEY_LSHIFT, "LSHIFT", "L_SHIFT", "LEFT_SHIFT");
        addFallBackNames(Keyboard.KEY_RSHIFT, "RSHIFT", "R_SHIFT", "RIGHT_SHIFT");
        addFallBackNames(Keyboard.KEY_LCONTROL, "LCTRL", "L_CTRL", "LEFT_CTRL", "LCONTROL", "L_CONTROL", "LEFT_CONTROL");
        addFallBackNames(Keyboard.KEY_RCONTROL, "RCTRL", "R_CTRL", "RIGHT_CTRL", "RCONTROL", "R_CONTROL", "RIGHT_CONTROL");
        addFallBackNames(-199, "SCROLL_UP");
        addFallBackNames(-201, "SCROLL_DOWN");
    }

    public static void addFallBackNames(int keyCode, String... names)
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
