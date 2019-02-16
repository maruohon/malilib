package fi.dy.masa.malilib.util;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.interfaces.IStringConsumer;
import fi.dy.masa.malilib.render.MessageRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentTranslation;

public class InfoUtils
{
    private static final MessageRenderer IN_GAME_MESSAGES = new MessageRenderer(0xA0000000, 0).setBackgroundStyle(true, false).setCentered(true, false).setExpandUp(true);

    public static final IStringConsumer INFO_MESSAGE_CONSUMER = new InfoMessageConsumer();

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open. Otherwise prints the message to the action bar.
     * @param type
     * @param translationKey
     * @param args
     */
    public static void showMessage(MessageType type, String translationKey, Object... args)
    {
        showMessage(type, 5000, translationKey, args);
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open. Otherwise prints the message to the action bar.
     * @param type
     * @param lifeTime
     * @param translationKey
     * @param args
     */
    public static void showMessage(MessageType type, int lifeTime, String translationKey, Object... args)
    {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;

        if (gui instanceof IMessageConsumer)
        {
            ((IMessageConsumer) gui).addMessage(type, lifeTime, translationKey, args);
        }
        else
        {
            String msg = type.getFormatting() + I18n.format(translationKey, args) + GuiBase.TXT_RST;
            printActionbarMessage(msg);
        }
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open. Otherwise adds the message to the in-game message handler.
     * @param type
     * @param translationKey
     * @param args
     */
    public static void showGuiOrInGameMessage(MessageType type, String translationKey, Object... args)
    {
        showGuiOrInGameMessage(type, 5000, translationKey, args);
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open. Otherwise adds the message to the in-game message handler.
     * @param type
     * @param lifeTime
     * @param translationKey
     * @param args
     */
    public static void showGuiOrInGameMessage(MessageType type, int lifeTime, String translationKey, Object... args)
    {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;

        if (gui instanceof IMessageConsumer)
        {
            ((IMessageConsumer) gui).addMessage(type, lifeTime, translationKey, args);
        }
        else
        {
            printInGameMessage(type, lifeTime, translationKey, args);
        }
    }

    public static void printActionbarMessage(String key, Object... args)
    {
        Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.GAME_INFO, new TextComponentTranslation(key, args));
    }

    /**
     * Adds the message to the in-game message handler
     * @param type
     * @param translationKey
     * @param args
     */
    public static void printInGameMessage(MessageType type, String translationKey, Object... args)
    {
        printInGameMessage(type, translationKey, 5000, args);
    }

    /**
     * Adds the message to the in-game message handler
     * @param type
     * @param lifeTime
     * @param translationKey
     * @param args
     */
    public static void printInGameMessage(MessageType type, int lifeTime, String translationKey, Object... args)
    {
        IN_GAME_MESSAGES.addMessage(type, lifeTime, translationKey, args);
    }

    public static void printBooleanConfigToggleMessage(String prettyName, boolean newValue)
    {
        String pre = newValue ? GuiBase.TXT_GREEN : GuiBase.TXT_RED;
        String status = I18n.format("malilib.message.value." + (newValue ? "on" : "off"));
        String message = I18n.format("malilib.message.toggled", prettyName, pre + status + GuiBase.TXT_RST);

        printActionbarMessage(message);
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public static void renderInGameMessages()
    {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int x = sr.getScaledWidth() / 2;
        int y = sr.getScaledHeight() - 76;

        IN_GAME_MESSAGES.drawMessages(x, y);
    }

    public static class InfoMessageConsumer implements IStringConsumer
    {
        @Override
        public void setString(String string)
        {
            TextComponentTranslation message = new TextComponentTranslation(string);
            Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.GAME_INFO, message);
        }
    }
}
