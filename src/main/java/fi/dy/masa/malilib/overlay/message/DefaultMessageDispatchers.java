package fi.dy.masa.malilib.overlay.message;

import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.overlay.widget.MessageRendererWidget;
import fi.dy.masa.malilib.overlay.widget.ToastRendererWidget;
import fi.dy.masa.malilib.render.text.StyledText;
import fi.dy.masa.malilib.util.game.wrap.GameUtils;

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
        sendVanillaMessage(MessageType.GAME_INFO, translatedMessage);
    }

    public static void sendVanillaHotbarMessageText(StyledText text, MessageDispatcher messageDispatcher)
    {
        if (text.lines.size() > 0)
        {
            sendVanillaMessage(MessageType.GAME_INFO, text.lines.get(0).displayText);
        }
    }

    public static void sendVanillaChatMessageString(String translatedMessage, MessageDispatcher messageDispatcher)
    {
        sendVanillaMessage(MessageType.CHAT, translatedMessage);
    }

    public static void sendVanillaChatMessageText(StyledText text, MessageDispatcher messageDispatcher)
    {
        if (text.lines.size() > 0)
        {
            sendVanillaMessage(MessageType.CHAT, text.lines.get(0).displayText);
        }
    }

    public static void sendVanillaMessage(RegistryKey<MessageType> messageTypeKey, String translatedMessage)
    {
        World world = GameUtils.getClientWorld();

        if (world != null)
        {
            MessageType type = world.getRegistryManager().get(Registry.MESSAGE_TYPE_KEY).get(messageTypeKey);
            MutableText msg = Text.literal(translatedMessage);
            MessageSender sender = new MessageSender(Util.NIL_UUID, msg);
            GameUtils.getClient().inGameHud.onChatMessage(type, msg, sender);
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
