package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;

public class Message
{
    private final MessageType type;
    private final long created;
    private final int displayTime;
    private final int maxLineWidth;
    private final List<String> message = new ArrayList<>();
    private final FontRenderer fontRenderer;

    public Message(MessageType type, int displayTimeMs, int maxLineWidth, String message, Object... args)
    {
        this.type = type;
        this.created = System.currentTimeMillis();
        this.displayTime = displayTimeMs;
        this.maxLineWidth = maxLineWidth;
        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;

        this.setMessage(I18n.format(message, args));
    }

    public boolean hasExpired()
    {
        return System.currentTimeMillis() > (this.created + this.displayTime);
    }

    public int getMessageHeight()
    {
        return this.message.size() * (this.fontRenderer.FONT_HEIGHT + 1) - 1 + 5;
    }

    public void setMessage(String message)
    {
        this.message.clear();

        String[] arr = message.split(" ");
        StringBuilder sb = new StringBuilder(this.maxLineWidth + 32);
        final int spaceWidth = this.fontRenderer.getStringWidth(" ");
        int lineWidth = 0;

        for (String str : arr)
        {
            int width = this.fontRenderer.getStringWidth(str);

            if ((lineWidth + width + spaceWidth) > this.maxLineWidth)
            {
                if (lineWidth > 0)
                {
                    this.message.add(sb.toString());
                    sb = new StringBuilder(this.maxLineWidth + 32);
                    lineWidth = 0;
                }

                // Long continuous string
                if (width > this.maxLineWidth)
                {
                    final int chars = str.length();

                    for (int i = 0; i < chars; ++i)
                    {
                        String c = str.substring(i, i + 1);
                        lineWidth += this.fontRenderer.getStringWidth(c);

                        if (lineWidth > this.maxLineWidth)
                        {
                            this.message.add(sb.toString());
                            sb = new StringBuilder(this.maxLineWidth + 32);
                            lineWidth = 0;
                        }

                        sb.append(c);
                    }

                    this.message.add(sb.toString());
                    sb = new StringBuilder(this.maxLineWidth + 32);
                    lineWidth = 0;
                }
            }

            if (lineWidth > 0)
            {
                sb.append(" ");
            }

            if (width <= this.maxLineWidth)
            {
                sb.append(str);
                lineWidth += width;
            }
        }

        this.message.add(sb.toString());
    }

    /**
     * Renders the lines for this message
     * @return the y coordinate of the next message
     */
    public int renderAt(int x, int y, int textColor)
    {
        String format = this.getFormatCode();

        for (String text : this.message)
        {
            this.fontRenderer.drawString(format + text + GuiBase.TXT_RST, x, y, textColor);
            y += this.fontRenderer.FONT_HEIGHT + 1;
        }

        return y + 3;
    }

    public String getFormatCode()
    {
        switch (this.type)
        {
            case INFO:      return GuiBase.TXT_GRAY;
            case SUCCESS:   return GuiBase.TXT_GREEN;
            case WARNING:   return GuiBase.TXT_GOLD;
            case ERROR:     return GuiBase.TXT_RED;
            default:        return GuiBase.TXT_GRAY;
        }
    }

    public enum MessageType
    {
        INFO,
        SUCCESS,
        WARNING,
        ERROR;
    }
}
