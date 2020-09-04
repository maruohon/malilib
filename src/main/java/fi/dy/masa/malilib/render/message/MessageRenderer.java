package fi.dy.masa.malilib.render.message;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import fi.dy.masa.malilib.gui.widget.BackgroundWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class MessageRenderer extends BackgroundWidget
{
    protected final List<Message> messages = new ArrayList<>();
    protected MessageType nextMessageType = MessageType.INFO;
    protected boolean centeredH;
    protected boolean centeredV;
    protected boolean expandUp;
    protected int lineSpacing = -1;
    protected int textColor = 0xFFFFFFFF;

    public MessageRenderer()
    {
        super(0, 0, 320, -1);

        this.setBackgroundEnabled(true);
        this.setBackgroundColor(0xA0000000);
        this.setBorderColor(0xFFC0C0C0);
    }

    /**
     * Sets whether the rendered box should get centered to the given x and y coordinates, or expand
     * to a given direction from that point.
     * If centeredV is false, then the value set in {@link #setExpandUp(boolean)} determines whether the box expands up or down.
     * @param centeredH
     * @param centeredV
     */
    public MessageRenderer setCentered(boolean centeredH, boolean centeredV)
    {
        this.centeredH = centeredH;
        this.centeredV = centeredV;
        return this;
    }

    public MessageRenderer setExpandUp(boolean expandUp)
    {
        this.expandUp = expandUp;
        return this;
    }

    public MessageRenderer setLineSpacing(int spacing)
    {
        this.lineSpacing = spacing;
        return this;
    }

    public MessageRenderer setTextColor(int color)
    {
        this.textColor = color;
        return this;
    }

    public MessageRenderer setNextMessageType(MessageType type)
    {
        this.nextMessageType = type;
        return this;
    }

    public MessageType getNextMessageType()
    {
        return this.nextMessageType;
    }

    public void clearMessages()
    {
        this.messages.clear();
        this.updateHeight();
    }

    public void addMessage(int displayTimeMs, String messageKey, Object... args)
    {
        this.addMessage(this.nextMessageType, displayTimeMs, messageKey, args);
    }

    public void addMessage(MessageType type, int displayTimeMs, String messageKey, Object... args)
    {
        this.messages.add(new Message(type, displayTimeMs, this.getWidth() - 8, messageKey, args));
        this.updateHeight();
    }

    @Override
    public void updateHeight()
    {
        if (this.automaticHeight)
        {
            this.setHeight(this.getMessagesHeight() + 12);
        }
    }

    public int getMessagesHeight()
    {
        final int messageCount = this.messages.size();

        if (messageCount > 0)
        {
            int height = 0;
            int lineSpacing = this.getLineSpacing();

            for (Message message : this.messages)
            {
                height += message.getLineCount() * lineSpacing + 2;
            }

            return height - (lineSpacing - StringUtils.getFontHeight()) - 2;
        }

        return 0;
    }

    protected int getLineSpacing()
    {
        if (this.lineSpacing < 0)
        {
            return StringUtils.getFontHeight() + 3;
        }

        return this.lineSpacing;
    }

    public void drawMessages(int x, int y)
    {
        if (this.messages.isEmpty() == false)
        {
            int width = this.getWidth();
            int height = this.getHeight();

            if (this.centeredH)
            {
                x -= width / 2;
            }

            if (this.centeredV)
            {
                y -= height / 2;
            }
            else if (this.expandUp)
            {
                y -= height;
            }

            this.renderWidgetBackground(x, y, width, height, false);

            x += 6;
            y += 6;
            long currentTime = System.currentTimeMillis();
            int lineSpacing = this.getLineSpacing();
            int countBefore = this.messages.size();

            GlStateManager.pushMatrix();
            GlStateManager.translate(0f, 0f, this.getZLevel() + 0.1f);

            for (int i = 0; i < this.messages.size(); ++i)
            {
                Message message = this.messages.get(i);
                message.renderAt(x, y, this.textColor, lineSpacing);
                y += message.getLineCount() * lineSpacing + 2;

                if (message.hasExpired(currentTime))
                {
                    this.messages.remove(i);
                    --i;
                }
            }

            GlStateManager.popMatrix();

            if (this.messages.size() != countBefore)
            {
                this.updateHeight();
            }
        }
    }
}
