package fi.dy.masa.malilib.overlay.message;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.config.value.ScreenLocation;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.render.text.StyledText;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.GameUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class MessageDispatcher
{
    protected MessageOutput type = MessageOutput.MESSAGE_OVERLAY;
    @Nullable protected ScreenLocation location;
    @Nullable protected String rendererMarker;
    @Nullable protected String messageMarker;
    @Nullable protected Consumer<String> consoleMessageConsumer;
    protected boolean append;
    protected boolean console;
    protected int defaultTextColor;
    protected int displayTimeMs = 5000;
    protected int fadeOutTimeMs;
    protected int fadeInTimeMs = 200;

    public MessageDispatcher(int defaultTextColor)
    {
        this.defaultTextColor = defaultTextColor;
        this.fadeOutTimeMs = MaLiLibConfigs.Generic.MESSAGE_FADE_OUT_TIME.getIntegerValue();
        this.consoleMessageConsumer = MaLiLib.LOGGER::info;
    }

    public MessageDispatcher type(MessageOutput type)
    {
        this.type = type;
        return this;
    }

    public MessageDispatcher append(boolean append)
    {
        this.append = append;
        return this;
    }

    public MessageDispatcher console()
    {
        this.console = true;
        return this;
    }

    public MessageDispatcher consoleMessageConsumer(@Nullable Consumer<String> messageConsumer)
    {
        this.consoleMessageConsumer = messageConsumer;
        return this;
    }

    public MessageDispatcher color(int defaultTextColor)
    {
        this.defaultTextColor = defaultTextColor;
        return this;
    }

    public MessageDispatcher time(int displayTimeMs)
    {
        this.displayTimeMs = displayTimeMs;
        return this;
    }

    public MessageDispatcher fadeOut(int fadeOutTimeMs)
    {
        this.fadeOutTimeMs = fadeOutTimeMs;
        return this;
    }

    public MessageDispatcher fadeIn(int fadeInTimeMs)
    {
        this.fadeInTimeMs = fadeInTimeMs;
        return this;
    }

    public MessageDispatcher location(@Nullable ScreenLocation location)
    {
        this.location = location;
        return this;
    }

    public MessageDispatcher rendererMarker(@Nullable String marker)
    {
        this.rendererMarker = marker;
        return this;
    }

    public MessageDispatcher messageMarker(@Nullable String marker)
    {
        this.messageMarker = marker;
        return this;
    }

    public MessageOutput getType()
    {
        return this.type;
    }

    @Nullable
    public ScreenLocation getLocation()
    {
        return this.location;
    }

    @Nullable
    public String getRendererMarker()
    {
        return this.rendererMarker;
    }

    @Nullable
    public String getMessageMarker()
    {
        return this.messageMarker;
    }

    public boolean getAppend()
    {
        return this.append;
    }

    public int getDefaultTextColor()
    {
        return this.defaultTextColor;
    }

    public int getDisplayTimeMs()
    {
        return this.displayTimeMs;
    }

    public int getFadeOutTimeMs()
    {
        return this.fadeOutTimeMs;
    }

    public int getFadeInTimeMs()
    {
        return this.fadeInTimeMs;
    }

    public void translate(String translationKey, Object... args)
    {
        if (MaLiLibConfigs.Debug.MESSAGE_KEY_TO_CHAT.getBooleanValue())
        {
            TextComponentString message = new TextComponentString(translationKey);
            TextComponentTranslation hoverMessage = new TextComponentTranslation("malilib.label.message_debug.add_key_to_chat");
            message.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, translationKey));
            message.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessage));
            GameUtils.getClient().ingameGUI.getChatGUI().printChatMessage(message);
        }

        MessageOutput output = Registry.MESSAGE_REDIRECT_MANAGER.getRedirectedMessageOutput(translationKey, this.type);
        String translatedMessage = StringUtils.translate(translationKey, args);
        this.printToConsoleIfEnabled(translatedMessage);
        output.send(translatedMessage, this);
    }

    public void send(String translatedMessage)
    {
        this.printToConsoleIfEnabled(translatedMessage);
        this.type.send(translatedMessage, this);
    }

    public void send(StyledText text)
    {
        if (this.console)
        {
            this.printToConsole(text);
        }

        this.type.send(text, this);
    }

    protected void printToConsoleIfEnabled(String translatedMessage)
    {
        if (this.console)
        {
            this.printToConsole(translatedMessage);
        }
    }

    public void printToConsole(String translatedMessage)
    {
        if (this.consoleMessageConsumer != null)
        {
            this.consoleMessageConsumer.accept(translatedMessage);
        }
    }

    public void printToConsole(StyledText text)
    {
        if (this.consoleMessageConsumer != null)
        {
            for (StyledTextLine line : text.lines)
            {
                this.consoleMessageConsumer.accept(line.displayText);
            }
        }
    }

    public static MessageDispatcher generic()
    {
        return generic(Message.INFO);
    }

    public static MessageDispatcher generic(int defaultTextColor)
    {
        return new MessageDispatcher(defaultTextColor);
    }

    public static MessageDispatcher success()
    {
        return new MessageDispatcher(Message.SUCCESS);
    }

    public static MessageDispatcher warning()
    {
        return new MessageDispatcher(Message.WARNING).consoleMessageConsumer(MaLiLib.LOGGER::warn);
    }

    public static MessageDispatcher error()
    {
        return new MessageDispatcher(Message.ERROR).consoleMessageConsumer(MaLiLib.LOGGER::error);
    }

    public static void generic(String translationKey, Object... args)
    {
        generic().translate(translationKey, args);
    }

    public static void success(String translationKey, Object... args)
    {
        success().translate(translationKey, args);
    }

    public static void warning(String translationKey, Object... args)
    {
        warning().translate(translationKey, args);
    }

    public static void error(String translationKey, Object... args)
    {
        error().translate(translationKey, args);
    }
}
