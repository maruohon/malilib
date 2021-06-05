package fi.dy.masa.malilib.overlay.message;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import fi.dy.masa.malilib.overlay.widget.MessageRendererWidget;
import fi.dy.masa.malilib.overlay.widget.ToastRendererWidget;
import fi.dy.masa.malilib.render.text.StyledText;

public class DefaultMessageDispatchers
{
    public static void sendOverlayMessageString(String translatedMessage, MessageDispatcher messageDispatcher)
    {
        MessageRendererWidget widget = MessageUtils.getMessageRendererWidget(messageDispatcher.getLocation(),
                                                                             messageDispatcher.getRendererMarker());
        widget.addMessage(translatedMessage, messageDispatcher);
    }

    public static void sendOverlayMessageText(StyledText text, MessageDispatcher messageDispatcher)
    {
        MessageRendererWidget widget = MessageUtils.getMessageRendererWidget(messageDispatcher.getLocation(),
                                                                             messageDispatcher.getRendererMarker());
        widget.addMessage(text, messageDispatcher);
    }

    public static void sendCustomHotbarMessageString(String translatedMessage, MessageDispatcher messageDispatcher)
    {
        MessageRendererWidget widget = MessageUtils.getCustomActionBarMessageRenderer();
        widget.addMessage(translatedMessage, messageDispatcher);
    }

    public static void sendCustomHotbarMessageText(StyledText text, MessageDispatcher messageDispatcher)
    {
        MessageRendererWidget widget = MessageUtils.getCustomActionBarMessageRenderer();
        widget.addMessage(text, messageDispatcher);
    }

    public static void sendToastMessageString(String translatedMessage, MessageDispatcher messageDispatcher)
    {
        ToastRendererWidget widget = MessageUtils.getToastRendererWidget(messageDispatcher.getLocation(),
                                                                         messageDispatcher.getRendererMarker());
        widget.addToast(translatedMessage, messageDispatcher);
    }

    public static void sendToastMessageText(StyledText text, MessageDispatcher messageDispatcher)
    {
        ToastRendererWidget widget = MessageUtils.getToastRendererWidget(messageDispatcher.getLocation(),
                                                                         messageDispatcher.getRendererMarker());
        widget.addToast(text, messageDispatcher);
    }

    public static void sendVanillaHotbarMessageString(String translatedMessage, MessageDispatcher messageDispatcher)
    {
        TextComponentString msg = new TextComponentString(translatedMessage);
        Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.GAME_INFO, msg);
    }

    public static void sendVanillaHotbarMessageText(StyledText text, MessageDispatcher messageDispatcher)
    {
        if (text.lines.size() > 0)
        {
            TextComponentString msg = new TextComponentString(text.lines.get(0).displayText);
            Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.GAME_INFO, msg);
        }
    }

    public static void sendVanillaChatMessageString(String translatedMessage, MessageDispatcher messageDispatcher)
    {
        TextComponentString msg = new TextComponentString(translatedMessage);
        Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.GAME_INFO, msg);
    }

    public static void sendVanillaChatMessageText(StyledText text, MessageDispatcher messageDispatcher)
    {
        if (text.lines.size() > 0)
        {
            TextComponentString msg = new TextComponentString(text.lines.get(0).displayText);
            Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.GAME_INFO, msg);
        }
    }

    public static void dummyStringMessageDispatcher(String translatedMessage, MessageDispatcher messageDispatcher)
    {
    }

    public static void dummyStyledTextMessageDispatcher(StyledText text, MessageDispatcher messageDispatcher)
    {
    }
}
