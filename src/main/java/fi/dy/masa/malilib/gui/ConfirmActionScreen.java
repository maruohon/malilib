package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.listener.ConfirmationListener;
import fi.dy.masa.malilib.listener.TaskCompletionListener;
import fi.dy.masa.malilib.message.MessageConsumer;
import fi.dy.masa.malilib.message.MessageType;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfirmActionScreen extends BaseScreen implements TaskCompletionListener
{
    protected final List<String> messageLines = new ArrayList<>();
    protected final ConfirmationListener listener;
    protected int textColor = 0xFFC0C0C0;

    public ConfirmActionScreen(int width, String titleKey, ConfirmationListener listener, @Nullable GuiScreen parent, String messageKey, Object... args)
    {
        this.setParent(parent);
        this.title = StringUtils.translate(titleKey);
        this.listener = listener;
        this.useTitleHierarchy = false;
        this.zLevel = 1f;

        StringUtils.splitTextToLines(this.messageLines, StringUtils.translate(messageKey, args), width - 30);

        this.setScreenWidthAndHeight(width, this.getMessageHeight() + 50);
        this.centerOnScreen();
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int x = this.x + 10;

        this.addWidget(new LabelWidget(x, this.y + 20, this.textColor, this.messageLines));

        int buttonWidth = 10 + StringUtils.getMaxStringRenderWidth(StringUtils::translate, "malilib.gui.button.colored.confirm", "malilib.gui.button.colored.cancel");
        int y = this.y + this.screenHeight - 26;

        // FIXME Should the parent screen be opened before triggering the action, that way the TaskCompletionListener redirect would not be needed?
        this.addButton(new GenericButton(x, y, buttonWidth, 20, "malilib.gui.button.colored.confirm"), (btn, mbtn) -> {
            this.listener.onActionConfirmed();
            BaseScreen.openGui(this.getParent());
        });
        x += buttonWidth + 10;

        this.addButton(new GenericButton(x, y, buttonWidth, 20, "malilib.gui.button.colored.cancel"), (btn, mbtn) -> {
            this.listener.onActionCancelled();
            BaseScreen.openGui(this.getParent());
        });
    }

    public void setTextColor(int textColor)
    {
        this.textColor = textColor;
    }

    public int getMessageHeight()
    {
        return this.messageLines.size() * (this.fontHeight + 1) - 1 + 5;
    }

    @Override
    public void addMessage(MessageType type, int lifeTime, String messageKey, Object... args)
    {
        if (this.getParent() instanceof MessageConsumer)
        {
            ((MessageConsumer) this.getParent()).addMessage(type, lifeTime, messageKey, args);
        }
        else
        {
            super.addMessage(type, lifeTime, messageKey, args);
        }
    }

    @Override
    public void onTaskCompleted()
    {
        if (this.getParent() instanceof TaskCompletionListener)
        {
            ((TaskCompletionListener) this.getParent()).onTaskCompleted();
        }
    }

    @Override
    public void onTaskAborted()
    {
        if (this.getParent() instanceof TaskCompletionListener)
        {
            ((TaskCompletionListener) this.getParent()).onTaskAborted();
        }
    }
}
