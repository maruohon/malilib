package fi.dy.masa.malilib.overlay.message;

import net.minecraft.network.ChatMessageSender;
import net.minecraft.network.MessageType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.overlay.widget.MessageRendererWidget;
import fi.dy.masa.malilib.overlay.widget.ToastRendererWidget;
import fi.dy.masa.malilib.render.text.StyledText;
import fi.dy.masa.malilib.util.GameUtils;

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
        MutableText msg = Text.translatable(translatedMessage);
        ChatMessageSender sender = new ChatMessageSender(Util.NIL_UUID, msg);
        GameUtils.getClient().inGameHud.onChatMessage(Registry.field_39206.get(MessageType.GAME_INFO), msg, new ChatMessageSender(Util.NIL_UUID, msg));
    }

    public static void sendVanillaHotbarMessageText(StyledText text, MessageDispatcher messageDispatcher)
    {
        if (text.lines.size() > 0)
        {
            MutableText msg = Text.translatable(text.lines.get(0).displayText);
            ChatMessageSender sender = new ChatMessageSender(Util.NIL_UUID, msg);
            GameUtils.getClient().inGameHud.onChatMessage(Registry.field_39206.get(MessageType.GAME_INFO), msg, sender);
        }
    }

    public static void sendVanillaChatMessageString(String translatedMessage, MessageDispatcher messageDispatcher)
    {
        MutableText msg = Text.translatable(translatedMessage);
        ChatMessageSender sender = new ChatMessageSender(Util.NIL_UUID, msg);
        GameUtils.getClient().inGameHud.onChatMessage(Registry.field_39206.get(MessageType.CHAT), msg, sender);
    }

    public static void sendVanillaChatMessageText(StyledText text, MessageDispatcher messageDispatcher)
    {
        if (text.lines.size() > 0)
        {
            MutableText msg = Text.translatable(text.lines.get(0).displayText);
            ChatMessageSender sender = new ChatMessageSender(Util.NIL_UUID, msg);
            GameUtils.getClient().inGameHud.onChatMessage(Registry.field_39206.get(MessageType.CHAT), msg, sender);
        }
    }

    public static void sendStringToDefaultToggleMessageOutput(String translatedMessage, MessageDispatcher messageDispatcher)
    {
        MaLiLibConfigs.Generic.DEFAULT_TOGGLE_MESSAGE_OUTPUT.getValue().send(translatedMessage, messageDispatcher);
    }

    public static void sendTextToDefaultToggleMessageOutput(StyledText text, MessageDispatcher messageDispatcher)
    {
        MaLiLibConfigs.Generic.DEFAULT_TOGGLE_MESSAGE_OUTPUT.getValue().send(text, messageDispatcher);
    }

    public static void dummyStringMessageDispatcher(String translatedMessage, MessageDispatcher messageDispatcher)
    {
    }

    public static void dummyStyledTextMessageDispatcher(StyledText text, MessageDispatcher messageDispatcher)
    {
    }
}
