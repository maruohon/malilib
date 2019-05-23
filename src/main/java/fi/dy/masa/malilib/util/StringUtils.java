package fi.dy.masa.malilib.util;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import fi.dy.masa.malilib.gui.GuiBase;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class StringUtils
{
    public static String getModVersionString(String modId)
    {
        for (ModContainer container : FabricLoader.getInstance().getAllMods())
        {
            if (container.getMetadata().getId().equals(modId))
            {
                return container.getMetadata().getVersion().getFriendlyString();
            }
        }

        return "?";
    }

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
        String pre = newValue ? GuiBase.TXT_GREEN : GuiBase.TXT_RED;
        String status = I18n.translate("malilib.message.value." + (newValue ? "on" : "off"));
        String message = I18n.translate("malilib.message.toggled", prettyName, pre + status + GuiBase.TXT_RST);
        printActionbarMessage(message);
    }

    public static void printActionbarMessage(String key, Object... args)
    {
        MinecraftClient.getInstance().inGameHud.addChatMessage(ChatMessageType.GAME_INFO, new TranslatableComponent(key, args));
    }

    public static void sendOpenFileChatMessage(PlayerEntity player, String messageKey, File file)
    {
        TextComponent name = new TextComponent(file.getName());
        name.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath()));
        name.getStyle().setUnderline(Boolean.valueOf(true));
        player.sendMessage(new TranslatableComponent(messageKey, name));
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

        TextRenderer font = MinecraftClient.getInstance().textRenderer;
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
}
