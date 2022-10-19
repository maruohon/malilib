package malilib.overlay.message;

import malilib.MaLiLibConfigs;
import malilib.overlay.widget.MessageRendererWidget;
import malilib.overlay.widget.ToastRendererWidget;
import malilib.render.text.StyledText;
import malilib.util.game.wrap.GameUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

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
        addVanillaOverlayMessage(translatedMessage);
    }

    public static void sendVanillaHotbarMessageText(StyledText text, MessageDispatcher messageDispatcher)
    {
        if (text.lines.size() > 0)
        {
            addVanillaOverlayMessage(text.lines.get(0).displayText);
        }
    }

    public static void sendVanillaChatMessageString(String translatedMessage, MessageDispatcher messageDispatcher)
    {
        addLocalVanillaChatMessage(translatedMessage);
    }

    public static void sendVanillaChatMessageText(StyledText text, MessageDispatcher messageDispatcher)
    {
        if (text.lines.size() > 0)
        {
            addLocalVanillaChatMessage(text.lines.get(0).displayText);
        }
    }

    public static void addLocalVanillaChatMessage(String translatedMessage)
    {
        MutableText msg = Text.literal(translatedMessage);
        GameUtils.getClient().inGameHud.getChatHud().addMessage(msg);
    }

    public static void addVanillaOverlayMessage(String translatedMessage)
    {
        MutableText msg = Text.literal(translatedMessage);
        GameUtils.getClient().inGameHud.setOverlayMessage(msg, false);
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
