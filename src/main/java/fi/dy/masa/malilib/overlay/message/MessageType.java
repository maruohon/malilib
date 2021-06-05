package fi.dy.masa.malilib.overlay.message;

import java.util.function.BiConsumer;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.value.BaseOptionListConfigValue;
import fi.dy.masa.malilib.render.text.StyledText;

public class MessageType extends BaseOptionListConfigValue
{
    public static final MessageType MESSAGE_OVERLAY = new MessageType("message",        DefaultMessageDispatchers::sendOverlayMessageString, DefaultMessageDispatchers::sendOverlayMessageText);
    public static final MessageType TOAST           = new MessageType("toast",          DefaultMessageDispatchers::sendToastMessageString, DefaultMessageDispatchers::sendToastMessageText);
    public static final MessageType CUSTOM_HOTBAR   = new MessageType("custom_hotbar",  DefaultMessageDispatchers::sendCustomHotbarMessageString, DefaultMessageDispatchers::sendCustomHotbarMessageText);
    public static final MessageType VANILLA_HOTBAR  = new MessageType("vanilla_hotbar", DefaultMessageDispatchers::sendVanillaHotbarMessageString, DefaultMessageDispatchers::sendVanillaChatMessageText);
    public static final MessageType CHAT            = new MessageType("chat",           DefaultMessageDispatchers::sendVanillaChatMessageString, DefaultMessageDispatchers::sendVanillaChatMessageText);
    public static final MessageType NONE            = new MessageType("none",           DefaultMessageDispatchers::dummyStringMessageDispatcher, DefaultMessageDispatchers::dummyStyledTextMessageDispatcher);

    public static ImmutableList<MessageType> VALUES = ImmutableList.of(MESSAGE_OVERLAY, TOAST, CUSTOM_HOTBAR, VANILLA_HOTBAR, CHAT, NONE);

    protected final BiConsumer<String, MessageDispatcher> stringMessageDispatcher;
    protected final BiConsumer<StyledText, MessageDispatcher> styledTextMessageDispatcher;

    private MessageType(String name,
                        BiConsumer<String, MessageDispatcher> stringMessageDispatcher,
                        BiConsumer<StyledText, MessageDispatcher> styledTextMessageDispatcher)
    {
        this(name, "malilib.label.message_type." + name, stringMessageDispatcher, styledTextMessageDispatcher);
    }

    public MessageType(String name,
                       String translationKey,
                       BiConsumer<String, MessageDispatcher> stringMessageDispatcher,
                       BiConsumer<StyledText, MessageDispatcher> styledTextMessageDispatcher)
    {
        super(name, translationKey);

        this.stringMessageDispatcher = stringMessageDispatcher;
        this.styledTextMessageDispatcher = styledTextMessageDispatcher;
    }

    public void send(String message, MessageDispatcher messageDispatcher)
    {
        this.stringMessageDispatcher.accept(message, messageDispatcher);
    }

    public void send(StyledText text, MessageDispatcher messageDispatcher)
    {
        this.styledTextMessageDispatcher.accept(text, messageDispatcher);
    }
}
