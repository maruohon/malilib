package malilib.input;

import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import malilib.MaLiLib;
import malilib.util.game.wrap.GameUtils;
import org.lwjgl.glfw.GLFW;

public class Keys
{
    private static final Pattern PATTERN_CHAR_CODE = Pattern.compile("^CHAR_(?<code>[0-9]+)$");

    private static final Object2IntOpenHashMap<String> NAMES_TO_IDS = new Object2IntOpenHashMap<>();
    private static final Int2ObjectOpenHashMap<String> IDS_TO_NAMES = new Int2ObjectOpenHashMap<>();

    public static final int KEY_NONE            = GLFW.GLFW_KEY_UNKNOWN;
    public static final int KEY_0               = GLFW.GLFW_KEY_0;
    public static final int KEY_1               = GLFW.GLFW_KEY_1;
    public static final int KEY_2               = GLFW.GLFW_KEY_2;
    public static final int KEY_3               = GLFW.GLFW_KEY_3;
    public static final int KEY_4               = GLFW.GLFW_KEY_4;
    public static final int KEY_5               = GLFW.GLFW_KEY_5;
    public static final int KEY_6               = GLFW.GLFW_KEY_6;
    public static final int KEY_7               = GLFW.GLFW_KEY_7;
    public static final int KEY_8               = GLFW.GLFW_KEY_8;
    public static final int KEY_9               = GLFW.GLFW_KEY_9;
    public static final int KEY_A               = GLFW.GLFW_KEY_A;
    public static final int KEY_B               = GLFW.GLFW_KEY_B;
    public static final int KEY_C               = GLFW.GLFW_KEY_C;
    public static final int KEY_D               = GLFW.GLFW_KEY_D;
    public static final int KEY_E               = GLFW.GLFW_KEY_E;
    public static final int KEY_F               = GLFW.GLFW_KEY_F;
    public static final int KEY_G               = GLFW.GLFW_KEY_G;
    public static final int KEY_H               = GLFW.GLFW_KEY_H;
    public static final int KEY_I               = GLFW.GLFW_KEY_I;
    public static final int KEY_J               = GLFW.GLFW_KEY_J;
    public static final int KEY_K               = GLFW.GLFW_KEY_K;
    public static final int KEY_L               = GLFW.GLFW_KEY_L;
    public static final int KEY_M               = GLFW.GLFW_KEY_M;
    public static final int KEY_N               = GLFW.GLFW_KEY_N;
    public static final int KEY_O               = GLFW.GLFW_KEY_O;
    public static final int KEY_P               = GLFW.GLFW_KEY_P;
    public static final int KEY_Q               = GLFW.GLFW_KEY_Q;
    public static final int KEY_R               = GLFW.GLFW_KEY_R;
    public static final int KEY_S               = GLFW.GLFW_KEY_S;
    public static final int KEY_T               = GLFW.GLFW_KEY_T;
    public static final int KEY_U               = GLFW.GLFW_KEY_U;
    public static final int KEY_V               = GLFW.GLFW_KEY_V;
    public static final int KEY_W               = GLFW.GLFW_KEY_W;
    public static final int KEY_X               = GLFW.GLFW_KEY_X;
    public static final int KEY_Y               = GLFW.GLFW_KEY_Y;
    public static final int KEY_Z               = GLFW.GLFW_KEY_Z;
    public static final int KEY_SPACE           = GLFW.GLFW_KEY_SPACE;
    public static final int KEY_APOSTROPHE      = GLFW.GLFW_KEY_APOSTROPHE;
    public static final int KEY_COMMA           = GLFW.GLFW_KEY_COMMA;
    public static final int KEY_MINUS           = GLFW.GLFW_KEY_MINUS;
    public static final int KEY_PERIOD          = GLFW.GLFW_KEY_PERIOD;
    public static final int KEY_SLASH           = GLFW.GLFW_KEY_SLASH;
    public static final int KEY_SEMICOLON       = GLFW.GLFW_KEY_SEMICOLON;
    public static final int KEY_EQUAL           = GLFW.GLFW_KEY_EQUAL;
    public static final int KEY_BACKSLASH       = GLFW.GLFW_KEY_BACKSLASH;
    public static final int KEY_ESCAPE          = GLFW.GLFW_KEY_ESCAPE;
    public static final int KEY_ENTER           = GLFW.GLFW_KEY_ENTER;
    public static final int KEY_TAB             = GLFW.GLFW_KEY_TAB;
    public static final int KEY_BACKSPACE       = GLFW.GLFW_KEY_BACKSPACE;
    public static final int KEY_INSERT          = GLFW.GLFW_KEY_INSERT;
    public static final int KEY_DELETE          = GLFW.GLFW_KEY_DELETE;
    public static final int KEY_RIGHT           = GLFW.GLFW_KEY_RIGHT;
    public static final int KEY_LEFT            = GLFW.GLFW_KEY_LEFT;
    public static final int KEY_DOWN            = GLFW.GLFW_KEY_DOWN;
    public static final int KEY_UP              = GLFW.GLFW_KEY_UP;
    public static final int KEY_PAGE_UP         = GLFW.GLFW_KEY_PAGE_UP;
    public static final int KEY_PAGE_DOWN       = GLFW.GLFW_KEY_PAGE_DOWN;
    public static final int KEY_HOME            = GLFW.GLFW_KEY_HOME;
    public static final int KEY_END             = GLFW.GLFW_KEY_END;
    public static final int KEY_LEFT_BRACKET    = GLFW.GLFW_KEY_LEFT_BRACKET;
    public static final int KEY_RIGHT_BRACKET   = GLFW.GLFW_KEY_RIGHT_BRACKET;
    public static final int KEY_F1              = GLFW.GLFW_KEY_F1;
    public static final int KEY_F2              = GLFW.GLFW_KEY_F2;
    public static final int KEY_F3              = GLFW.GLFW_KEY_F3;
    public static final int KEY_F4              = GLFW.GLFW_KEY_F4;
    public static final int KEY_F5              = GLFW.GLFW_KEY_F5;
    public static final int KEY_F6              = GLFW.GLFW_KEY_F6;
    public static final int KEY_F7              = GLFW.GLFW_KEY_F7;
    public static final int KEY_F8              = GLFW.GLFW_KEY_F8;
    public static final int KEY_F9              = GLFW.GLFW_KEY_F9;
    public static final int KEY_F10             = GLFW.GLFW_KEY_F10;
    public static final int KEY_F11             = GLFW.GLFW_KEY_F11;
    public static final int KEY_F12             = GLFW.GLFW_KEY_F12;
    public static final int KEY_F13             = GLFW.GLFW_KEY_F13;
    public static final int KEY_F14             = GLFW.GLFW_KEY_F14;
    public static final int KEY_F15             = GLFW.GLFW_KEY_F15;
    public static final int KEY_F16             = GLFW.GLFW_KEY_F16;
    public static final int KEY_F17             = GLFW.GLFW_KEY_F17;
    public static final int KEY_F18             = GLFW.GLFW_KEY_F18;
    public static final int KEY_F19             = GLFW.GLFW_KEY_F19;
    /*
    public static final int KEY_F20             = Keyboard.KEY_F20;
    public static final int KEY_F21             = Keyboard.KEY_F21;
    public static final int KEY_F22             = Keyboard.KEY_F22;
    public static final int KEY_F23             = Keyboard.KEY_F23;
    public static final int KEY_F24             = Keyboard.KEY_F24;
    public static final int KEY_F25             = Keyboard.KEY_F25;
    public static final int KEY_KP_0            = Keyboard.KEY_KP_0;
    public static final int KEY_KP_1            = Keyboard.KEY_KP_1;
    public static final int KEY_KP_2            = Keyboard.KEY_KP_2;
    public static final int KEY_KP_3            = Keyboard.KEY_KP_3;
    public static final int KEY_KP_4            = Keyboard.KEY_KP_4;
    public static final int KEY_KP_5            = Keyboard.KEY_KP_5;
    public static final int KEY_KP_6            = Keyboard.KEY_KP_6;
    public static final int KEY_KP_7            = Keyboard.KEY_KP_7;
    public static final int KEY_KP_8            = Keyboard.KEY_KP_8;
    public static final int KEY_KP_9            = Keyboard.KEY_KP_9;
    */
    public static final int KEY_KP_DECIMAL      = GLFW.GLFW_KEY_KP_DECIMAL;
    public static final int KEY_KP_DIVIDE       = GLFW.GLFW_KEY_KP_DIVIDE;
    public static final int KEY_KP_MULTIPLY     = GLFW.GLFW_KEY_KP_MULTIPLY;
    public static final int KEY_KP_SUBTRACT     = GLFW.GLFW_KEY_KP_SUBTRACT;
    public static final int KEY_KP_ADD          = GLFW.GLFW_KEY_KP_ADD;
    public static final int KEY_KP_ENTER        = GLFW.GLFW_KEY_KP_ENTER;
    public static final int KEY_KP_EQUAL        = GLFW.GLFW_KEY_KP_EQUAL;
    public static final int KEY_LEFT_SHIFT      = GLFW.GLFW_KEY_LEFT_SHIFT;
    public static final int KEY_LEFT_CONTROL    = GLFW.GLFW_KEY_LEFT_CONTROL;
    public static final int KEY_LEFT_ALT        = GLFW.GLFW_KEY_LEFT_ALT;
    public static final int KEY_LEFT_SUPER      = GLFW.GLFW_KEY_LEFT_SUPER;
    public static final int KEY_RIGHT_SHIFT     = GLFW.GLFW_KEY_RIGHT_SHIFT;
    public static final int KEY_RIGHT_CONTROL   = GLFW.GLFW_KEY_RIGHT_CONTROL;
    public static final int KEY_RIGHT_ALT       = GLFW.GLFW_KEY_RIGHT_ALT;
    public static final int KEY_RIGHT_SUPER     = GLFW.GLFW_KEY_RIGHT_SUPER;
    public static final int KEY_GRAVE_ACCENT    = GLFW.GLFW_KEY_GRAVE_ACCENT;
    public static final int KEY_CAPS_LOCK       = GLFW.GLFW_KEY_CAPS_LOCK;
    public static final int KEY_SCROLL_LOCK     = GLFW.GLFW_KEY_SCROLL_LOCK;
    public static final int KEY_NUM_LOCK        = GLFW.GLFW_KEY_NUM_LOCK;
    public static final int KEY_PAUSE           = GLFW.GLFW_KEY_PAUSE;
    /*
    public static final int KEY_WORLD_1         = Keyboard.KEY_WORLD_1;
    public static final int KEY_WORLD_2         = Keyboard.KEY_WORLD_2;
    public static final int KEY_PRINT_SCREEN    = Keyboard.KEY_PRINT_SCREEN;
    public static final int KEY_MENU            = Keyboard.KEY_MENU;
    */

    public static final int MOUSE_BUTTON_1      = GLFW.GLFW_MOUSE_BUTTON_1 - 100;
    public static final int MOUSE_BUTTON_2      = GLFW.GLFW_MOUSE_BUTTON_2 - 100;
    public static final int MOUSE_BUTTON_3      = GLFW.GLFW_MOUSE_BUTTON_3 - 100;
    public static final int MOUSE_BUTTON_4      = GLFW.GLFW_MOUSE_BUTTON_4 - 100;
    public static final int MOUSE_BUTTON_5      = GLFW.GLFW_MOUSE_BUTTON_5 - 100;
    public static final int MOUSE_BUTTON_6      = GLFW.GLFW_MOUSE_BUTTON_6 - 100;
    public static final int MOUSE_BUTTON_7      = GLFW.GLFW_MOUSE_BUTTON_7 - 100;
    public static final int MOUSE_BUTTON_8      = GLFW.GLFW_MOUSE_BUTTON_8 - 100;

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

        if (keyCode == Keys.KEY_NONE)
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

        if (keyCode >= 256)
        {
            return charEncoder.apply(keyCode - 256);
        }

        return null;
    }

    public static boolean isKeyDown(int keyCode)
    {
        long window = GameUtils.getClient().getWindow().getHandle();

        if (keyCode >= 0)
        {
            return GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS;
        }

        keyCode += 100;

        return keyCode >= 0 && GLFW.glfwGetMouseButton(window, keyCode) == GLFW.GLFW_PRESS;
    }

    public static IntArrayList readKeysFromStorageString(String str)
    {
        IntArrayList keyCodes = new IntArrayList();
        String[] keys = str.split(",");

        for (String keyName : keys)
        {
            keyName = keyName.trim();

            if (keyName.isEmpty() == false)
            {
                int keyCode = getKeyCodeForStorageString(keyName);

                if (keyCode != Keys.KEY_NONE && keyCodes.contains(keyCode) == false)
                {
                    keyCodes.add(keyCode);
                }
            }
        }

        return keyCodes;
    }

    public static String writeKeysToString(IntArrayList keyCodes, String separator, Function<Integer, String> charEncoder)
    {
        StringBuilder sb = new StringBuilder(32);
        final int size = keyCodes.size();

        for (int i = 0; i < size; ++i)
        {
            if (i > 0)
            {
                sb.append(separator);
            }

            int keyCode = keyCodes.getInt(i);
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
        initDefaultKeyMappings();

        addNameOverride(Keys.KEY_LEFT_ALT, "L_ALT");
        addNameOverride(Keys.KEY_RIGHT_ALT, "R_ALT");
        addNameOverride(Keys.KEY_LEFT_SHIFT, "L_SHIFT");
        addNameOverride(Keys.KEY_RIGHT_SHIFT, "R_SHIFT");
        addNameOverride(Keys.KEY_LEFT_CONTROL, "L_CTRL");
        addNameOverride(Keys.KEY_RIGHT_CONTROL, "R_CTRL");
        addNameOverride(Keys.KEY_PAGE_UP, "PAGE_UP");
        addNameOverride(Keys.KEY_PAGE_DOWN, "PAGE_DOWN");
        addNameOverride(-100, "LEFT_MOUSE");
        addNameOverride(-99, "RIGHT_MOUSE");
        addNameOverride(-98, "MIDDLE_MOUSE");
        addNameOverride(-199, "SCROLL_UP");
        addNameOverride(-201, "SCROLL_DOWN");

        addLoadableNames(Keys.KEY_LEFT_ALT, "LMENU", "L_MENU", "LALT", "LEFT_ALT");
        addLoadableNames(Keys.KEY_RIGHT_ALT, "RMENU", "R_MENU", "RALT", "RIGHT_ALT");
        addLoadableNames(Keys.KEY_LEFT_SHIFT, "LSHIFT", "LEFT_SHIFT");
        addLoadableNames(Keys.KEY_RIGHT_SHIFT, "RSHIFT", "RIGHT_SHIFT");
        addLoadableNames(Keys.KEY_LEFT_CONTROL, "LCTRL", "LEFT_CTRL", "LCONTROL", "L_CONTROL", "LEFT_CONTROL");
        addLoadableNames(Keys.KEY_RIGHT_CONTROL, "RCTRL", "RIGHT_CTRL", "RCONTROL", "R_CONTROL", "RIGHT_CONTROL");
        addLoadableNames(-100, "BUTTON0", "MOUSE0", "LMB", "MOUSE_LEFT");
        addLoadableNames(-99, "BUTTON1", "MOUSE1", "RMB", "MOUSE_RIGHT");
        addLoadableNames(-98, "BUTTON2", "MOUSE2", "MMB", "MOUSE_MIDDLE");
    }

    private static void initDefaultKeyMappings()
    {
        NAMES_TO_IDS.defaultReturnValue(KEY_NONE);

        for (Field field : Keys.class.getDeclaredFields())
        {
            try
            {
                String name = field.getName();
                int keyCode = KEY_NONE;

                if (name.startsWith("KEY_"))
                {
                    name = name.substring(4);
                    keyCode = field.getInt(null);
                }
                else if (name.startsWith("MOUSE_"))
                {
                    name = name.substring(6);
                    keyCode = field.getInt(null);
                }

                if (keyCode != KEY_NONE)
                {
                    IDS_TO_NAMES.put(keyCode, name);
                    NAMES_TO_IDS.put(name, keyCode);
                }
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.error("Failed to initialize the key name lookup!", e);
            }
        }
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
                MaLiLib.LOGGER.warn("Duplicate key fallback name '{}' => {}", name, keyCode);
                continue;
            }

            NAMES_TO_IDS.put(name, keyCode);
        }
    }

    /**
     * Adds an id -> name override, which determines what the config file will contain for a given keyCode.
     * Also adds the reverse mapping, name -> id for being able to load the key by the given name.
     * @param name the name to use for this keyCode in the config files
     */
    public static void addNameOverride(int keyCode, String name)
    {
        if (IDS_TO_NAMES.containsKey(keyCode))
        {
            MaLiLib.LOGGER.warn("Duplicate key override name {} => '{}'", keyCode, name);
        }

        IDS_TO_NAMES.put(keyCode, name);
        NAMES_TO_IDS.put(name, keyCode);
    }
}
