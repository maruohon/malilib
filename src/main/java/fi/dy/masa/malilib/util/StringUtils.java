package fi.dy.masa.malilib.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class StringUtils
{
    /**
     * Parses the given string as a hexadecimal value, if it begins with '#' or '0x'.
     * Otherwise tries to parse it as a regular base 10 integer.
     * @param colorStr
     * @param defaultColor
     * @return
     */
    public static int getColor(String colorStr, int defaultColor)
    {
        Pattern pattern = Pattern.compile("(?:0x|#)([a-fA-F0-9]{1,8})");
        Matcher matcher = pattern.matcher(colorStr);

        if (matcher.matches())
        {
            try { return (int) Long.parseLong(matcher.group(1), 16); }
            catch (NumberFormatException e) { return defaultColor; }
        }

        try { return Integer.parseInt(colorStr, 10); }
        catch (NumberFormatException e) { return defaultColor; }
    }

    /**
     * Splits the given camel-case string into parts separated by a space
     * @param str
     * @return
     */
    // https://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human-readable-names-in-java
    public static String splitCamelCase(String str)
    {
        return str.replaceAll(
           String.format("%s|%s|%s",
              "(?<=[A-Z])(?=[A-Z][a-z])",
              "(?<=[^A-Z])(?=[A-Z])",
              "(?<=[A-Za-z])(?=[^A-Za-z])"
           ),
           " "
        );
     }

    public static void printBooleanConfigToggleMessage(String prettyName, boolean newValue)
    {
        String pre = newValue ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString();
        String status = I18n.format("malilib.message.value." + (newValue ? "on" : "off"));
        String message = I18n.format("malilib.message.toggled", prettyName, pre + status + TextFormatting.RESET);
        printActionbarMessage(message);
    }

    public static void printActionbarMessage(String key, Object... args)
    {
        Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.GAME_INFO, new TextComponentTranslation(key, args));
    }
}
