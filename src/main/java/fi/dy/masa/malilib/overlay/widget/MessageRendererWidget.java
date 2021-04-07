package fi.dy.masa.malilib.overlay.widget;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.overlay.message.Message;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.text.TextRenderer;
import fi.dy.masa.malilib.util.JsonUtils;

public class MessageRendererWidget extends InfoRendererWidget
{
    protected final List<Message> messages = new ArrayList<>();
    protected boolean renderBackground = true;
    protected int backgroundColor = 0xC0000000;
    protected int borderColor = 0xFFC0C0C0;
    protected int messageGap = 3;
    protected int maxMessages = -1;

    public MessageRendererWidget()
    {
        super();
        this.isOverlay = true;
        this.setLineHeight(10);
        this.padding.setAll(4, 6, 4, 6);
    }

    public void clearMessages()
    {
        this.messages.clear();
        this.updateSizeAndPosition();
    }

    public void addMessage(int defaultColor, int displayTimeMs, int fadeTimeMs, String translationKey, Object... args)
    {
        if (this.maxMessages > 0 && this.messages.size() >= this.maxMessages)
        {
            this.messages.remove(0);
        }

        int width = this.getMaxMessageWidth();
        this.messages.add(new Message(defaultColor, displayTimeMs, fadeTimeMs, width, translationKey, args));
        this.updateSizeAndPosition();
    }

    protected int getMaxMessageWidth()
    {
        return this.automaticWidth ? GuiUtils.getScaledWindowWidth() - 80 : this.getWidth() - this.getPadding().getHorizontalTotal();
    }

    public boolean getRenderBackground()
    {
        return this.renderBackground;
    }

    public void setRenderBackground(boolean renderBackground)
    {
        this.renderBackground = renderBackground;
    }

    public int getBackgroundColor()
    {
        return this.backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    public int getBorderColor()
    {
        return this.borderColor;
    }

    public void setBorderColor(int borderColor)
    {
        this.borderColor = borderColor;
    }

    public int getMessageGap()
    {
        return this.messageGap;
    }

    public void setMessageGap(int messageGap)
    {
        this.messageGap = messageGap;
    }

    public void setAutomaticWidth(boolean automaticWidth)
    {
        this.automaticWidth = automaticWidth;
    }

    /**
     * Sets the maximum number of concurrent messages to display.
     * Use -1 for no limit.
     */
    public void setMaxMessages(int maxMessages)
    {
        this.maxMessages = maxMessages;
    }

    @Override
    public void onAdded()
    {
        this.updateSizeAndPosition();
    }

    protected void updateSizeAndPosition()
    {
        this.updateWidth();
        this.updateHeight();
        this.updateWidgetPosition();
    }

    @Override
    public void updateWidth()
    {
        if (this.automaticWidth)
        {
            int width = 0;

            for (Message msg : this.messages)
            {
                width = Math.max(width, msg.getWidth());
            }

            width += this.getPadding().getHorizontalTotal();

            // Don't shrink while there are active messages,
            // to prevent an annoying horizontal move of the messages
            if (width > this.getWidth() || this.messages.isEmpty())
            {
                this.setWidth(width);
            }
        }
    }

    @Override
    public void updateHeight()
    {
        this.setHeight(this.getMessagesHeight() + this.getPadding().getVerticalTotal());
    }

    protected int getMessagesHeight()
    {
        final int messageCount = this.messages.size();

        if (messageCount > 0)
        {
            int height = (messageCount - 1) * this.messageGap;

            for (Message message : this.messages)
            {
                height += message.getLineCount() * this.lineHeight;
            }

            return height - (this.lineHeight - TextRenderer.INSTANCE.getFontHeight());
        }

        return 0;
    }

    protected void updateWidgetPosition()
    {
        //System.out.printf("MessageRendererWidget(%s)#updateWidgetPosition()\n");
        int viewportWidth = this.viewportWidthSupplier.getAsInt();
        int viewportHeight = this.viewportHeightSupplier.getAsInt();
        int width = (int) Math.ceil(this.getWidth() * this.getScale()) + this.margin.getHorizontalTotal();
        int height = (int) Math.ceil(this.getHeight() * this.getScale()) + this.margin.getVerticalTotal();
        int x = this.location.getStartX(width, viewportWidth, 0) + this.margin.getLeft();
        int y = this.location.getStartY(height, viewportHeight, 0) + this.margin.getTop();

        this.setPosition(x, y);
    }

    @Override
    protected void renderBackground(int x, int y, float z)
    {
        if (this.renderBackground && this.messages.isEmpty() == false)
        {
            ShapeRenderUtils.renderOutlinedRectangle(x, y, z, this.getWidth(), this.getHeight(), this.backgroundColor, this.borderColor);
        }
    }

    @Override
    protected void renderContents(int x, int y, float z)
    {
        this.drawMessages(x, y, z);
    }

    public void drawMessages(int x, int y, float z)
    {
        if (this.messages.isEmpty() == false)
        {
            x += this.getPadding().getLeft();
            y += this.getPadding().getTop();

            long currentTime = System.nanoTime();
            int countBefore = this.messages.size();

            for (int i = 0; i < this.messages.size(); ++i)
            {
                Message message = this.messages.get(i);

                if (message.hasExpired(currentTime))
                {
                    this.messages.remove(i);
                    --i;
                }
                else
                {
                    message.renderAt(x, y, z + 0.1f, this.lineHeight, currentTime);
                }

                // Always offset the position to prevent a flicker from the later
                // messages jumping over the fading message when it disappears,
                // before the entire widget gets resized and the messages possibly moving
                // (if the widget is bottom-aligned).
                y += message.getLineCount() * this.lineHeight + this.messageGap;
            }

            if (this.messages.size() != countBefore)
            {
                this.updateSizeAndPosition();
            }
        }
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();
        obj.addProperty("bg_enabled", this.renderBackground);
        obj.addProperty("bg_color", this.backgroundColor);
        obj.addProperty("border_color", this.borderColor);
        obj.addProperty("msg_gap", this.messageGap);
        return obj;
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        super.fromJson(obj);

        this.renderBackground = JsonUtils.getBooleanOrDefault(obj, "bg_enabled", this.renderBackground);
        this.backgroundColor = JsonUtils.getIntegerOrDefault(obj, "bg_color", this.backgroundColor);
        this.borderColor = JsonUtils.getIntegerOrDefault(obj, "border_color", this.borderColor);
        this.messageGap = JsonUtils.getIntegerOrDefault(obj, "msg_gap", this.messageGap);
    }
}
