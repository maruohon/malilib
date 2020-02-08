package fi.dy.masa.malilib.gui.util;

import java.util.ArrayList;
import java.util.List;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.StringUtils;

public class Message
{
    private final List<String> messageLines = new ArrayList<>();
    private final MessageType type;
    private final long created;
    private final int displayTime;
    private final int maxLineLength;

    public Message(MessageType type, int displayTimeMs, int maxLineLength, String message, Object... args)
    {
        this.type = type;
        this.created = System.currentTimeMillis();
        this.displayTime = displayTimeMs;
        this.maxLineLength = maxLineLength;

        StringUtils.splitTextToLines(this.messageLines, StringUtils.translate(message, args), this.maxLineLength);
    }

    public boolean hasExpired(long currentTime)
    {
        return currentTime > (this.created + (long) this.displayTime);
    }

    public int getLineCount()
    {
        return this.messageLines.size();
    }

    /**
     * Renders the lines for this message
     * @return the y coordinate of the next message
     */
    public void renderAt(int x, int y, int textColor, int lineSpacing)
    {
        String format = this.getFormatCode();

        for (String text : this.messageLines)
        {
            StringUtils.drawString(x, y, textColor, format + text + GuiBase.TXT_RST);
            y += lineSpacing;
        }
    }

    public String getFormatCode()
    {
        return this.type.getFormatting();
    }

    public enum MessageType
    {
        INFO        ("malilib.message.formatting_code.info"),
        SUCCESS     ("malilib.message.formatting_code.success"),
        WARNING     ("malilib.message.formatting_code.warning"),
        ERROR       ("malilib.message.formatting_code.error");

        private final String translationKey;

        private MessageType(String translationKey)
        {
            this.translationKey = translationKey;
        }

        public String getFormatting()
        {
            return StringUtils.translate(this.translationKey);
        }
    }
}
