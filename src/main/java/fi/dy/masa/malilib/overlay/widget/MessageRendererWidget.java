package fi.dy.masa.malilib.overlay.widget;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.config.overlay.MessageRendererWidgetEditScreen;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseInfoRendererWidgetEntryWidget;
import fi.dy.masa.malilib.overlay.message.Message;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.render.text.StyledText;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class MessageRendererWidget extends InfoRendererWidget
{
    protected final List<Message> messages = new ArrayList<>();
    protected int messageGap = 3;
    protected int maxMessages = -1;

    public MessageRendererWidget()
    {
        super();

        this.shouldSerialize = true;
        this.getBackgroundSettings().setEnabled(true);
        this.getBackgroundSettings().setColor(0xC0000000);
        this.getBorderSettings().setEnabled(true);

        this.padding.setAll(4, 6, 4, 6);
        this.setName(StringUtils.translate("malilib.label.default_message_renderer"));
        this.setLineHeight(10);
        this.setMaxWidth(320);
    }

    @Override
    public String getWidgetTypeId()
    {
        return MaLiLibReference.MOD_ID + ":message_renderer";
    }

    @Override
    public boolean isFixedPosition()
    {
        return true;
    }

    @Override
    public void initListEntryWidget(BaseInfoRendererWidgetEntryWidget widget)
    {
        widget.setCanConfigure(true);
        widget.setCanRemove(true);
    }

    @Override
    public void openEditScreen()
    {
        MessageRendererWidgetEditScreen screen = new MessageRendererWidgetEditScreen(this);
        screen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(screen);
    }

    public void clearMessages()
    {
        this.messages.clear();
        this.updateSizeAndPosition();
    }

    public void addMessage(String translatedMessage, MessageDispatcher messageDispatcher)
    {
        this.addMessage(StyledText.of(translatedMessage), messageDispatcher);
    }

    public void addMessage(StyledText text, MessageDispatcher messageDispatcher)
    {
        int defaultTextColor = messageDispatcher.getDefaultTextColor();
        int displayTimeMs = messageDispatcher.getDisplayTimeMs();
        int fadeOutTimeMs = messageDispatcher.getFadeOutTimeMs();
        this.addMessage(text, defaultTextColor, displayTimeMs, fadeOutTimeMs);
    }

    public void addMessage(StyledText text, int defaultTextColor, int displayTimeMs, int fadeOutTimeMs)
    {
        if (this.maxMessages > 0 && this.messages.size() >= this.maxMessages)
        {
            this.messages.remove(0);
        }

        int width = this.getMaxMessageWidth();
        this.messages.add(new Message(text, defaultTextColor, displayTimeMs, fadeOutTimeMs, width));
        this.updateSizeAndPosition();
    }

    protected int getMaxMessageWidth()
    {
        int baseWidth = this.automaticWidth ? this.maxWidth : this.getWidth();
        return baseWidth - this.getPadding().getHorizontalTotal();
    }

    public int getMessageGap()
    {
        return this.messageGap;
    }

    public void setMessageGap(int messageGap)
    {
        this.messageGap = messageGap;
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
            int lineHeight = this.getLineHeight();

            for (Message message : this.messages)
            {
                height += message.getLineCount() * lineHeight;
            }

            return height - (lineHeight - this.getFontHeight());
        }

        return 0;
    }

    @Override
    protected void renderTextBackground(int x, int y, float z, ScreenContext ctx)
    {
        if (this.messages.isEmpty() == false)
        {
            super.renderTextBackground(x, y, z, ctx);
        }
    }

    @Override
    protected void renderWidgetBackground(int x, int y, float z, ScreenContext ctx)
    {
        if (this.messages.isEmpty() == false)
        {
            super.renderWidgetBackground(x, y, z, ctx);
        }
    }

    @Override
    protected void renderWidgetBorder(int x, int y, float z, ScreenContext ctx)
    {
        if (this.messages.isEmpty() == false)
        {
            super.renderWidgetBorder(x, y, z, ctx);
        }
    }

    @Override
    protected void renderContents(int x, int y, float z, ScreenContext ctx)
    {
        this.drawMessages(x, y, z, ctx);
    }

    public void drawMessages(int x, int y, float z, ScreenContext ctx)
    {
        if (this.messages.isEmpty() == false)
        {
            x += this.getPadding().getLeft();
            y += this.getPadding().getTop();

            long currentTime = System.nanoTime();
            int countBefore = this.messages.size();
            int lineHeight = this.getLineHeight();

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
                    message.renderAt(x, y, z + 0.1f, lineHeight, currentTime, ctx);
                }

                // Always offset the position to prevent a flicker from the later
                // messages jumping over the fading message when it disappears,
                // before the entire widget gets resized and the messages possibly moving
                // (if the widget is bottom-aligned).
                y += message.getLineCount() * lineHeight + this.messageGap;
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
        obj.addProperty("msg_gap", this.messageGap);
        obj.addProperty("max_messages", this.maxMessages);
        obj.addProperty("width", this.getWidth());

        if (this.hasMaxWidth())
        {
            obj.addProperty("max_width", this.maxWidth);
        }

        if (this.automaticWidth)
        {
            obj.addProperty("width_auto", true);
        }

        if (this.renderAboveScreen)
        {
            obj.addProperty("above_screen", true);
        }

        return obj;
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        super.fromJson(obj);

        this.messageGap = JsonUtils.getIntegerOrDefault(obj, "msg_gap", this.messageGap);
        this.maxMessages = JsonUtils.getIntegerOrDefault(obj, "max_messages", this.maxMessages);
        this.renderAboveScreen = JsonUtils.getBooleanOrDefault(obj, "above_screen", this.renderAboveScreen);
        this.automaticWidth = JsonUtils.getBooleanOrDefault(obj, "width_auto", this.automaticWidth);
        this.setWidth(JsonUtils.getIntegerOrDefault(obj, "width", this.getWidth()));

        if (JsonUtils.hasInteger(obj, "max_width"))
        {
            this.setMaxWidth(JsonUtils.getInteger(obj, "max_width"));
        }
    }
}
