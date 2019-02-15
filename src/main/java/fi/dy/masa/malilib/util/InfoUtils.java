package fi.dy.masa.malilib.util;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.interfaces.IStringConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentTranslation;

public class InfoUtils
{
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
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;

        if (gui instanceof IMessageConsumer)
        {
            ((IMessageConsumer) gui).addMessage(type, translationKey, args);
        }
        else
        {
            String msg = type.getFormatting() + I18n.format(translationKey, args) + GuiBase.TXT_RST;
            printActionbarMessage(msg);
        }
    }

    public static void printActionbarMessage(String key, Object... args)
    {
        Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.GAME_INFO, new TextComponentTranslation(key, args));
    }

    public static void printBooleanConfigToggleMessage(String prettyName, boolean newValue)
    {
        String pre = newValue ? GuiBase.TXT_GREEN : GuiBase.TXT_RED;
        String status = I18n.format("malilib.message.value." + (newValue ? "on" : "off"));
        String message = I18n.format("malilib.message.toggled", prettyName, pre + status + GuiBase.TXT_RST);

        printActionbarMessage(message);
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
