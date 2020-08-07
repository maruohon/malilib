package fi.dy.masa.malilib.render.message;

import java.util.ArrayList;
import java.util.List;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.util.StringUtils;

public class Message
{
    protected final List<String> messageLines = new ArrayList<>();
    protected final MessageType type;
    protected final long creationTime;
    protected final int displayTime;
    protected final int maxLineLength;

    public Message(MessageType type, int displayTimeMs, int maxLineLength, String message, Object... args)
    {
        this.type = type;
        this.creationTime = System.currentTimeMillis();
        this.displayTime = displayTimeMs;
        this.maxLineLength = maxLineLength;

        StringUtils.splitTextToLines(this.messageLines, StringUtils.translate(message, args), this.maxLineLength);
    }

    public boolean hasExpired(long currentTime)
    {
        return currentTime > (this.creationTime + (long) this.displayTime);
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
            StringUtils.drawString(x, y, textColor, format + text + BaseScreen.TXT_RST);
            y += lineSpacing;
        }
    }

    public String getFormatCode()
    {
        return this.type.getFormatting();
    }
}
