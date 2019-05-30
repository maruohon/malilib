package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.List;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;

public class Message
{
    private final MessageType type;
    private final long created;
    private final int displayTime;
    private final int maxLineLength;
    private final List<String> messageLines = new ArrayList<>();
    private final FontRenderer textRenderer;

    public Message(MessageType type, int displayTimeMs, int maxLineLength, String message, Object... args)
    {
        this.type = type;
        this.created = System.currentTimeMillis();
        this.displayTime = displayTimeMs;
        this.maxLineLength = maxLineLength;
        this.textRenderer = Minecraft.getInstance().fontRenderer;

        this.setMessage(I18n.format(message, args));
    }

    public boolean hasExpired(long currentTime)
    {
        return currentTime > (this.created + this.displayTime);
    }

    public int getMessageHeight()
    {
        return this.messageLines.size() * (this.textRenderer.FONT_HEIGHT + 1) - 1 + 5;
    }

    public void setMessage(String message)
    {
        this.messageLines.clear();
        StringUtils.splitTextToLines(this.messageLines, message, this.maxLineLength, this.textRenderer);
    }

    /**
     * Renders the lines for this message
     * @return the y coordinate of the next message
     */
    public int renderAt(int x, int y, int textColor)
    {
        String format = this.getFormatCode();

        for (String text : this.messageLines)
        {
            this.textRenderer.drawString(format + text + GuiBase.TXT_RST, x, y, textColor);
            y += this.textRenderer.FONT_HEIGHT + 1;
        }

        return y + 3;
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
            return I18n.format(this.translationKey);
        }
    }
}
