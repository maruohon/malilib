package fi.dy.masa.malilib.message;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentTranslation;
import fi.dy.masa.malilib.LiteModMaLiLib;
import fi.dy.masa.malilib.config.value.HudAlignment;
import fi.dy.masa.malilib.config.value.InfoType;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.render.MessageRenderer;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.consumer.IStringConsumer;

public class MessageUtils
{
    private static final MessageRenderer IN_GAME_MESSAGES_CENTER = createMainMessageRenderer();
    private static final MessageRenderer IN_GAME_MESSAGES_TOP_LEFT     = new MessageRenderer();
    private static final MessageRenderer IN_GAME_MESSAGES_TOP_RIGHT    = new MessageRenderer();
    private static final MessageRenderer IN_GAME_MESSAGES_BOTTOM_LEFT  = (new MessageRenderer()).setExpandUp(true);
    private static final MessageRenderer IN_GAME_MESSAGES_BOTTOM_RIGHT = (new MessageRenderer()).setExpandUp(true);

    public static final IStringConsumer INFO_MESSAGE_CONSUMER = new InfoMessageConsumer();
    public static final IMessageConsumer INGAME_MESSAGE_CONSUMER = new InGameMessageConsumer();

    private static MessageRenderer createMainMessageRenderer()
    {
        MessageRenderer renderer = new MessageRenderer();
        renderer.setCentered(true, false).setExpandUp(true);
        return renderer;
    }

    public static void showMessage(InfoType outputType, MessageType messageType, String translationKey, Object... args)
    {
        showMessage(outputType, messageType, 5000, translationKey, args);
    }

    public static void showMessage(InfoType outputType, MessageType messageType, int lifeTime, String translationKey, Object... args)
    {
        if (outputType != InfoType.NONE)
        {
            if (outputType == InfoType.MESSAGE_OVERLAY)
            {
                showGuiOrInGameMessage(messageType, lifeTime, translationKey, args);
            }
            else if (outputType == InfoType.HOTBAR)
            {
                printActionbarMessage(translationKey, args);
            }
            else if (outputType == InfoType.CHAT)
            {
                Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.CHAT, new TextComponentTranslation(translationKey, args));
            }
        }
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open.
     * @param type
     * @param translationKey
     * @param args
     */
    public static void showGuiMessage(MessageType type, String translationKey, Object... args)
    {
        showGuiMessage(type, 5000, translationKey, args);
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open.
     * @param type
     * @param lifeTime
     * @param translationKey
     * @param args
     */
    public static void showGuiMessage(MessageType type, int lifeTime, String translationKey, Object... args)
    {
        if (GuiUtils.getCurrentScreen() instanceof IMessageConsumer)
        {
            ((IMessageConsumer) GuiUtils.getCurrentScreen()).addMessage(type, lifeTime, translationKey, args);
        }
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open. Otherwise prints the message to the action bar.
     * @param type
     * @param translationKey
     * @param args
     */
    public static void showGuiOrActionBarMessage(MessageType type, String translationKey, Object... args)
    {
        showGuiOrActionBarMessage(type, 5000, translationKey, args);
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open. Otherwise prints the message to the action bar.
     * @param type
     * @param lifeTime
     * @param translationKey
     * @param args
     */
    public static void showGuiOrActionBarMessage(MessageType type, int lifeTime, String translationKey, Object... args)
    {
        if (GuiUtils.getCurrentScreen() instanceof IMessageConsumer)
        {
            ((IMessageConsumer) GuiUtils.getCurrentScreen()).addMessage(type, lifeTime, translationKey, args);
        }
        else
        {
            String msg = type.getFormatting() + StringUtils.translate(translationKey, args) + BaseScreen.TXT_RST;
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
        /*
        // For debugging
        showInGameMessage(HudAlignment.CENTER, type, lifeTime, translationKey, args);
        showInGameMessage(HudAlignment.TOP_LEFT, type, lifeTime, translationKey, args);
        showInGameMessage(HudAlignment.TOP_RIGHT, type, lifeTime, translationKey, args);
        showInGameMessage(HudAlignment.BOTTOM_LEFT, type, lifeTime, translationKey, args);
        showInGameMessage(HudAlignment.BOTTOM_RIGHT, type, lifeTime, translationKey, args);
        */

        if (GuiUtils.getCurrentScreen() instanceof IMessageConsumer)
        {
            ((IMessageConsumer) GuiUtils.getCurrentScreen()).addMessage(type, lifeTime, translationKey, args);
        }
        else
        {
            showInGameMessage(type, lifeTime, translationKey, args);
        }
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open.
     * Also shows the message in the in-game message box.
     * @param type
     * @param translationKey
     * @param args
     */
    public static void showGuiAndInGameMessage(MessageType type, String translationKey, Object... args)
    {
        showGuiAndInGameMessage(type, 5000, translationKey, args);
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open.
     * Also shows the message in the in-game message box.
     * @param type
     * @param lifeTime
     * @param translationKey
     * @param args
     */
    public static void showGuiAndInGameMessage(MessageType type, int lifeTime, String translationKey, Object... args)
    {
        showGuiMessage(type, lifeTime, translationKey, args);
        showInGameMessage(type, lifeTime, translationKey, args);
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
    public static void showInGameMessage(MessageType type, String translationKey, Object... args)
    {
        showInGameMessage(type, 5000, translationKey, args);
    }

    /**
     * Adds the message to the in-game message handler
     * @param type
     * @param lifeTime
     * @param translationKey
     * @param args
     */
    public static void showInGameMessage(MessageType type, int lifeTime, String translationKey, Object... args)
    {
        synchronized (IN_GAME_MESSAGES_CENTER)
        {
            IN_GAME_MESSAGES_CENTER.addMessage(type, lifeTime, translationKey, args);
        }
    }

    /**
     * Adds a message to one of the specific message renderers that are aligned to different
     * parts of the screen.
     * @param renderPosition
     * @param type
     * @param lifeTime
     * @param translationKey
     * @param args
     */
    public static void showInGameMessage(HudAlignment renderPosition, MessageType type, int lifeTime, String translationKey, Object... args)
    {
        synchronized (IN_GAME_MESSAGES_CENTER)
        {
            switch (renderPosition)
            {
                case CENTER:
                    IN_GAME_MESSAGES_CENTER.addMessage(type, lifeTime, translationKey, args);
                    break;

                case TOP_LEFT:
                    IN_GAME_MESSAGES_TOP_LEFT.addMessage(type, lifeTime, translationKey, args);
                    break;

                case TOP_RIGHT:
                    IN_GAME_MESSAGES_TOP_RIGHT.addMessage(type, lifeTime, translationKey, args);
                    break;

                case BOTTOM_LEFT:
                    IN_GAME_MESSAGES_BOTTOM_LEFT.addMessage(type, lifeTime, translationKey, args);
                    break;

                case BOTTOM_RIGHT:
                    IN_GAME_MESSAGES_BOTTOM_RIGHT.addMessage(type, lifeTime, translationKey, args);
                    break;
            }
        }
    }

    /**
     * Prints an error message both to the in-game or GUI messages, and to the game console
     * @param translationKey
     * @param args
     */
    public static void printErrorMessage(String translationKey, Object... args)
    {
        String msg = StringUtils.translate(translationKey, args);
        showGuiOrInGameMessage(MessageType.ERROR, msg);
        LiteModMaLiLib.logger.error(msg);
    }

    public static void printBooleanConfigToggleMessage(String prettyName, boolean newValue)
    {
        String pre = newValue ? BaseScreen.TXT_GREEN : BaseScreen.TXT_RED;
        String status = StringUtils.translate("malilib.message.value." + (newValue ? "on" : "off"));
        String message = StringUtils.translate("malilib.message.toggled", prettyName, pre + status + BaseScreen.TXT_RST);

        printActionbarMessage(message);
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public static void renderInGameMessages()
    {
        int width = GuiUtils.getScaledWindowWidth();
        int height = GuiUtils.getScaledWindowHeight();
        int x = width / 2;
        int y = height - 76;

        synchronized (IN_GAME_MESSAGES_CENTER)
        {
            IN_GAME_MESSAGES_CENTER.drawMessages(x, y);
            IN_GAME_MESSAGES_TOP_LEFT.drawMessages(4, 4);
            IN_GAME_MESSAGES_TOP_RIGHT.drawMessages(width - IN_GAME_MESSAGES_TOP_RIGHT.getWidth() - 4, 4);
            IN_GAME_MESSAGES_BOTTOM_LEFT.drawMessages(4, height - 4);
            IN_GAME_MESSAGES_BOTTOM_RIGHT.drawMessages(width - IN_GAME_MESSAGES_BOTTOM_RIGHT.getWidth() - 4, height - 4);
        }
    }

    public static class InfoMessageConsumer implements IStringConsumer
    {
        @Override
        public boolean consumeString(String string)
        {
            TextComponentTranslation message = new TextComponentTranslation(string);
            Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.GAME_INFO, message);
            return true;
        }
    }

    public static class InGameMessageConsumer implements IMessageConsumer
    {
        @Override
        public void addMessage(MessageType type, int lifeTime, String translationKey, Object... args)
        {
            MessageUtils.showInGameMessage(type, lifeTime, translationKey, args);
        }
    }
}
