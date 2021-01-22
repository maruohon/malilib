package fi.dy.masa.malilib.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import fi.dy.masa.malilib.util.data.LeftRight;

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
     * Removes the string <b>extension</b> from the end of <b>str</b>,
     * if <b>str</b> ends in <b>extension</b>
     * @param str
     * @param extension
     * @return
     */
    public static String stripExtensionIfMatches(String str, String extension)
    {
        if (str.endsWith(extension) && str.length() > extension.length())
        {
            return str.substring(0, str.length() - extension.length());
        }

        return str;
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

    public static void sendOpenFileChatMessage(net.minecraft.command.ICommandSender sender, String messageKey, File file)
    {
        net.minecraft.util.text.TextComponentString name = new net.minecraft.util.text.TextComponentString(file.getName());
        name.getStyle().setClickEvent(new net.minecraft.util.text.event.ClickEvent(net.minecraft.util.text.event.ClickEvent.Action.OPEN_FILE, file.getAbsolutePath()));
        name.getStyle().setUnderlined(Boolean.TRUE);
        sender.sendMessage(new net.minecraft.util.text.TextComponentTranslation(messageKey, name));
    }

    public static int getMaxStringRenderWidth(String... strings)
    {
        return getMaxStringRenderWidth(Arrays.asList(strings));
    }

    public static int getMaxStringRenderWidth(List<String> lines)
    {
        int width = 0;

        for (String line : lines)
        {
            width = Math.max(width, getStringWidth(line));
        }

        return width;
    }

    /**
     * Splits the given string into lines up to maxLineLength long
     * @param linesOut
     * @param textIn
     * @param maxLineLength
     */
    public static void splitTextToLines(List<String> linesOut, String textIn, int maxLineLength)
    {
        String[] lines = textIn.split("\\\\n");
        @Nullable String activeColor = null;

        for (String line : lines)
        {
            String[] parts = line.split(" ");
            StringBuilder sb = new StringBuilder(256);
            final int spaceWidth = getStringWidth(" ");
            int lineWidth = 0;

            for (String str : parts)
            {
                int width = getStringWidth(str);

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

                            if (c.equals("§") && i < (chars - 1))
                            {
                                activeColor = str.substring(i, i + 2);
                                sb.append(activeColor);
                                ++i;
                                continue;
                            }

                            lineWidth += getStringWidth(c);

                            if (lineWidth > maxLineLength)
                            {
                                linesOut.add(sb.toString());
                                sb = new StringBuilder(256);
                                lineWidth = 0;

                                if (activeColor != null)
                                {
                                    sb.append(activeColor);
                                }
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
                        sb.append(str, 0, end);
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

    public static String getDisplayStringForList(List<String> list, final int maxWidth,
                                                 String quote, String prefix, String suffix)
    {
        StringBuilder sb = new StringBuilder(128);
        sb.append(prefix);

        String entrySep = ", ";
        String dots = " ...";
        final int listSize = list.size();
        final int widthQuotes = getStringWidth(quote) * 2;
        final int widthSep = getStringWidth(entrySep);
        final int widthDots = getStringWidth(dots);
        final int widthNextMin = widthSep + widthDots;
        int width = getStringWidth(prefix) + getStringWidth(suffix);

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
                final int len = getStringWidth(str) + widthQuotes;
                int widthNext = listIndex < listSize - 1 ? widthNextMin : 0;

                if ((width + len + widthNext) <= maxWidth)
                {
                    sb.append(quote).append(str).append(quote);
                    width += len;
                }
                else
                {
                    if ((width + getStringWidth(str.substring(0, 1)) + widthDots) <= maxWidth)
                    {
                        sb.append(quote);
                        width += widthQuotes;

                        for (int i = 0; i < str.length(); ++i)
                        {
                            String c = str.substring(i, i + 1);
                            final int charWidth = getStringWidth(c);

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

                        sb.append(quote);
                    }

                    sb.append(dots);
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

    /**
     * Shrinks the given string until it can fit into the provided maximum width,
     * and adds the provided clamping indicator to indicate that the string is longer than what is shown.
     * @param text
     * @param maxWidth
     * @param side the side from which to shrink the string
     * @param indicator the appended shrinkage indicator, for example "..."
     * @return
     */
    public static String clampTextToRenderLength(String text, final int maxWidth, LeftRight side, String indicator)
    {
        // The entire string fits, just return it as-is
        if (getStringWidth(text) <= maxWidth)
        {
            return text;
        }

        StringBuilder sb = new StringBuilder(128);

        final int indicatorWidth = getStringWidth(indicator);
        final int stringLen = text.length();
        int usedWidth = indicatorWidth;
        int index = 0;
        int lastIndex = stringLen - 1;
        int indexIncrement = 1;

        // Shrink from the left, so append/build from the right
        if (side == LeftRight.LEFT)
        {
            index = stringLen - 1;
            lastIndex = 0;
            indexIncrement = -1;
        }

        while (usedWidth < maxWidth)
        {
            String chr = text.substring(index, index + 1);
            int charWidth = getStringWidth(chr);

            if (usedWidth + charWidth > maxWidth)
            {
                break;
            }

            sb.append(chr);
            usedWidth += charWidth;

            if (index == lastIndex)
            {
                break;
            }

            index += indexIncrement;
        }

        if (side == LeftRight.LEFT)
        {
            return indicator + sb.reverse().toString();
        }

        sb.append(indicator);

        return sb.toString();
    }

    @Nullable
    public static String getWorldOrServerName()
    {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();

        if (mc.isSingleplayer())
        {
            net.minecraft.server.integrated.IntegratedServer server = mc.getIntegratedServer();

            if (server != null)
            {
                return server.getFolderName();
            }
        }
        else
        {
            net.minecraft.client.multiplayer.ServerData server = mc.getCurrentServerData();

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
                name = prefix + name + suffix;
            }
            else
            {
                World world = Minecraft.getMinecraft().world;

                if (world != null)
                {
                    name = prefix + name + "_dim" + WorldUtils.getDimensionId(world) + suffix;
                }
            }
        }
        else
        {
            name = prefix + defaultName + suffix;
        }

        return FileUtils.generateSimpleSafeFileName(name);
    }

    public static String getStackString(net.minecraft.item.ItemStack stack)
    {
        if (stack.isEmpty() == false)
        {
            net.minecraft.util.ResourceLocation rl = net.minecraft.item.Item.REGISTRY.getNameForObject(stack.getItem());

            return String.format("[%s @ %d - display: %s - NBT: %s] (%s)",
                    rl != null ? rl.toString() : "null", stack.getMetadata(), stack.getDisplayName(),
                    stack.getTagCompound() != null ? stack.getTagCompound().toString() : "<no NBT>",
                    stack.toString());
        }

        return "<empty>";
    }

    // Some MCP vs. Yarn vs. MC versions compatibility/wrapper stuff below this

    /**
     * Just a wrapper around I18n, to reduce the number of changed lines between MCP/Yarn versions of mods
     * @param translationKey
     * @param args
     * @return
     */
    public static String translate(String translationKey, Object... args)
    {
        try
        {
            return net.minecraft.client.resources.I18n.format(translationKey, args);
        }
        catch (Exception e)
        {
            return translationKey;
        }
    }

    /**
     * Just a wrapper to get the font height from the Font/TextRenderer
     * @return
     */
    public static int getFontHeight()
    {
        return net.minecraft.client.Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
    }

    /**
     * Returns the render width of the given string
     * @param text
     * @return
     */
    public static int getStringWidth(String text)
    {
        return net.minecraft.client.Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
    }

    public static void drawString(int x, int y, int color, String text)
    {
        net.minecraft.client.Minecraft.getMinecraft().fontRenderer.drawString(text, x, y, color);
    }
}
