package fi.dy.masa.malilib.command;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class CommandBase
{
    public static void localOutput(ServerCommandSource sender, String message)
    {
        sendColoredText(sender, Formatting.AQUA, message);
    }

    public static void localOutputT(ServerCommandSource sender, String translationKey, Object... args)
    {
        sendColoredText(sender, Formatting.AQUA, new TranslatableText(translationKey, args));
    }

    public static void localError(ServerCommandSource sender, String message)
    {
        sendColoredText(sender, Formatting.DARK_RED, message);
    }

    public static void localErrorT(ServerCommandSource sender, String translationKey, Object... args)
    {
        sendColoredText(sender, Formatting.DARK_RED, new TranslatableText(translationKey, args));
    }

    public static void sendColoredText(ServerCommandSource sender, Formatting color, String message)
    {
        LiteralText chat = new LiteralText(message);
        chat.formatted(color);
        sender.getEntity().sendMessage(chat);
    }

    public static void sendColoredText(ServerCommandSource sender, Formatting color, Text component)
    {
        component.formatted(color);
        sender.getEntity().sendMessage(component);
    }
}