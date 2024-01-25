package malilib.overlay.message;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;

import malilib.MaLiLib;
import malilib.MaLiLibConfigs;
import malilib.config.value.ScreenLocation;
import malilib.gui.util.GuiUtils;
import malilib.registry.Registry;
import malilib.render.text.StyledText;
import malilib.render.text.StyledTextLine;
import malilib.util.StringUtils;
import malilib.util.game.wrap.GameUtils;

public class MessageDispatcher
{
    protected MessageOutput type = MessageOutput.MESSAGE_OVERLAY;
    @Nullable protected ScreenLocation location;
    @Nullable protected String rendererMarker;
    @Nullable protected String messageMarker;
    @Nullable protected Consumer<String> consoleMessageConsumer;
    @Nullable protected Throwable exception;
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

    public MessageDispatcher console(Throwable exception)
    {
        this.exception = exception;
        return this.console();
    }

    public MessageDispatcher customHotbar()
    {
        this.type = MessageOutput.CUSTOM_HOTBAR;
        return this;
    }

    public MessageDispatcher screenOrActionbar()
    {
        if (GuiUtils.isScreenOpen())
        {
            this.type = MessageOutput.MESSAGE_OVERLAY;
        }
        else
        {
            this.type = MessageOutput.CUSTOM_HOTBAR;
        }

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
            String message = StringUtils.translate(translationKey, args);
            GameUtils.printMessageToChat(message);
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
            this.printExceptionToConsole();
        }
    }

    public void printToConsole(StyledText text)
    {
        this.printToConsole(text.lines);
    }

    public void printToConsole(List<StyledTextLine> lines)
    {
        if (this.consoleMessageConsumer != null)
        {
            for (StyledTextLine line : lines)
            {
                this.consoleMessageConsumer.accept(line.displayText);
            }

            this.printExceptionToConsole();
        }
    }

    protected void printExceptionToConsole()
    {
        if (MaLiLibConfigs.Debug.PRINT_STACK_TRACE.getBooleanValue())
        {
            if (this.exception != null)
            {
                MaLiLib.LOGGER.error("Exception:", this.exception);
            }

            /*
            if (this.exception != null && this.consoleMessageConsumer != null)
            {
                //this.exception.printStackTrace();

                try (PrintWriter w = new PrintWriter(new ConsumerWriter(this.consoleMessageConsumer)))
                {
                    this.exception.printStackTrace(w);
                }
                catch (Exception ignore) {}
            }
            */
        }
    }

    public static MessageDispatcher generic()
    {
        return new MessageDispatcher(Message.INFO);
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

    public static MessageDispatcher generic(int displayTimeMs)
    {
        return generic().time(displayTimeMs);
    }

    public static MessageDispatcher success(int displayTimeMs)
    {
        return success().time(displayTimeMs);
    }

    public static MessageDispatcher warning(int displayTimeMs)
    {
        return warning().time(displayTimeMs);
    }

    public static MessageDispatcher error(int displayTimeMs)
    {
        return error().time(displayTimeMs);
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

    public static class ConsumerWriter extends Writer
    {
        protected final Consumer<String> consumer;

        public ConsumerWriter(Consumer<String> consumer)
        {
            this.consumer = consumer;
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException
        {
            this.consumer.accept(new String(cbuf, off, len));
        }

        @Override
        public void flush()
        {
        }

        @Override
        public void close()
        {
        }
    }
}
