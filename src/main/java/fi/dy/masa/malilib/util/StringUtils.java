package fi.dy.masa.malilib.util;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.World;

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
        str = str.replaceAll(
           String.format("%s|%s|%s",
              "(?<=[A-Z])(?=[A-Z][a-z])",
              "(?<=[^A-Z])(?=[A-Z])",
              "(?<=[A-Za-z])(?=[^A-Za-z])"
           ),
           " "
        );

        if (str.length() > 1 && str.charAt(0) > 'Z')
        {
            str = str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1);
        }

        return str;
    }

    /**
     * @deprecated since 0.10.0. Use the same method in InfoUtils instead.
     * @param key
     * @param args
     */
    @Deprecated
    public static void printActionbarMessage(String key, Object... args)
    {
        Minecraft.getInstance().ingameGUI.addChatMessage(ChatType.GAME_INFO, new TextComponentTranslation(key, args));
    }

    public static void sendOpenFileChatMessage(EntityPlayer player, String messageKey, File file)
    {
        ITextComponent name = new TextComponentString(file.getName());
        name.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath()));
        name.getStyle().setUnderlined(Boolean.valueOf(true));
        player.sendMessage(new TextComponentTranslation(messageKey, name));
    }

    /**
     * Splits the given string into lines up to maxLineLength long
     * @param linesOut
     * @param textIn
     * @param maxLineLength
     * @param font
     */
    public static void splitTextToLines(List<String> linesOut, String textIn, int maxLineLength, FontRenderer font)
    {
        String[] lines = textIn.split("\\\\n");

        for (String line : lines)
        {
            String[] parts = line.split(" ");
            StringBuilder sb = new StringBuilder(256);
            final int spaceWidth = font.getStringWidth(" ");
            int lineWidth = 0;

            for (String str : parts)
            {
                int width = font.getStringWidth(str);

                if ((lineWidth + width + spaceWidth) > maxLineLength)
                {
                    if (lineWidth > 0)
                    {
                        linesOut.add(sb.toString());
                        sb = new StringBuilder(256);
                        lineWidth = 0;
                    }

                    // Long continuous string
                    if (width > maxLineLength)
                    {
                        final int chars = str.length();

                        for (int i = 0; i < chars; ++i)
                        {
                            String c = str.substring(i, i + 1);
                            lineWidth += font.getStringWidth(c);

                            if (lineWidth > maxLineLength)
                            {
                                linesOut.add(sb.toString());
                                sb = new StringBuilder(256);
                                lineWidth = 0;
                            }

                            sb.append(c);
                        }

                        linesOut.add(sb.toString());
                        sb = new StringBuilder(256);
                        lineWidth = 0;
                    }
                }

                if (lineWidth > 0)
                {
                    sb.append(" ");
                }

                if (width <= maxLineLength)
                {
                    sb.append(str);
                    lineWidth += width + spaceWidth;
                }
            }

            linesOut.add(sb.toString());
        }
    }

    public static String getClampedDisplayStringStrlen(List<String> list, final int maxWidth, String prefix, String suffix)
    {
        StringBuilder sb = new StringBuilder(128);
        sb.append(prefix);
        int width = prefix.length() + suffix.length();
        final int size = list.size();

        if (size > 0)
        {
            for (int i = 0; i < size && width < maxWidth; i++)
            {
                if (i > 0)
                {
                    sb.append(", ");
                    width += 2;
                }

                String str = list.get(i);
                final int len = str.length();
                int end = Math.min(len, maxWidth - width);

                if (end < len)
                {
                    end = Math.max(0, Math.min(len, maxWidth - width - 3));

                    if (end >= 1)
                    {
                        sb.append(str.substring(0, end));
                    }

                    sb.append("...");
                    width += end + 3;
                }
                else
                {
                    sb.append(str);
                    width += len;
                }
            }
        }
        else
        {
            sb.append("<empty>");
        }

        sb.append(suffix);

        return sb.toString();
    }

    public static String getClampedDisplayStringRenderlen(List<String> list, final int maxWidth, String prefix, String suffix)
    {
        StringBuilder sb = new StringBuilder(128);
        sb.append(prefix);

        FontRenderer font = Minecraft.getInstance().fontRenderer;
        String entrySep = ", ";
        String dots = " ...";
        final int listSize = list.size();
        final int widthSep = font.getStringWidth(entrySep);
        final int widthDots = font.getStringWidth(dots);
        int width = font.getStringWidth(prefix) + font.getStringWidth(suffix);

        if (listSize > 0)
        {
            for (int listIndex = 0; listIndex < listSize && width < maxWidth; ++listIndex)
            {
                if (listIndex > 0)
                {
                    sb.append(entrySep);
                    width += widthSep;
                }

                String str = list.get(listIndex);
                final int len = font.getStringWidth(str);

                if ((width + len) <= maxWidth)
                {
                    sb.append(str);
                    width += len;
                }
                else
                {
                    for (int i = 0; i < str.length(); ++i)
                    {
                        String c = str.substring(i, i + 1);
                        final int charWidth = font.getStringWidth(c);

                        if ((width + charWidth + widthDots) <= maxWidth)
                        {
                            sb.append(c);
                            width += charWidth;
                        }
                        else
                        {
                            break;
                        }
                    }

                    sb.append(dots);
                    width += widthDots;
                    break;
                }
            }
        }
        else
        {
            sb.append("<empty>");
        }

        sb.append(suffix);

        return sb.toString();
    }

    @Nullable
    public static String getWorldOrServerName()
    {
        Minecraft mc = Minecraft.getInstance();

        if (mc.isSingleplayer())
        {
            IntegratedServer server = mc.getIntegratedServer();

            if (server != null)
            {
                return server.getFolderName();
            }
        }
        else
        {
            ServerData server = mc.getCurrentServerData();

            if (server != null)
            {
                return server.serverIP.replace(':', '_');
            }
        }

        return null;
    }

    /**
     * Returns a file name based on the current server or world name.
     * If <b>globalData</b> is false, the the name will also include the current dimension ID.
     * @param globalData
     * @param prefix
     * @param suffix
     * @param defaultName the default file name, if getting a per-server/world name fails
     * @return
     */
    public static String getStorageFileName(boolean globalData, String prefix, String suffix, String defaultName)
    {
        String name = getWorldOrServerName();

        if (name != null)
        {
            if (globalData)
            {
                return prefix + name + suffix;
            }

            World world = Minecraft.getInstance().world;

            if (world != null)
            {
                return prefix + name + "_dim" + WorldUtils.getDimensionId(world) + suffix;
            }
        }

        return prefix + defaultName + suffix;
    }
}
