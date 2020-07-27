package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.message.IMessageConsumer;
import fi.dy.masa.malilib.message.MessageType;
import fi.dy.masa.malilib.gui.widget.WidgetLabel;
import fi.dy.masa.malilib.listener.ICompletionListener;
import fi.dy.masa.malilib.listener.IConfirmationListener;
import fi.dy.masa.malilib.util.StringUtils;

public class GuiConfirmAction extends GuiDialogBase implements ICompletionListener
{
    protected final List<String> messageLines = new ArrayList<>();
    protected final IConfirmationListener listener;
    protected int textColor = 0xFFC0C0C0;

    public GuiConfirmAction(int width, String titleKey, IConfirmationListener listener, @Nullable GuiScreen parent, String messageKey, Object... args)
    {
        this.setParent(parent);
        this.title = StringUtils.translate(titleKey);
        this.listener = listener;
        this.useTitleHierarchy = false;
        this.zLevel = 1f;

        StringUtils.splitTextToLines(this.messageLines, StringUtils.translate(messageKey, args), width - 30);

        this.setWidthAndHeight(width, this.getMessageHeight() + 50);
        this.centerOnScreen();
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int x = this.dialogLeft + 10;

        this.addWidget(new WidgetLabel(x, this.dialogTop + 20, this.textColor, this.messageLines));

        int buttonWidth = this.getButtonWidth();
        int y = this.dialogTop + this.dialogHeight - 26;

        this.createButton(x, y, buttonWidth, ButtonType.OK, (btn, mbtn) -> {
            this.listener.onActionConfirmed();
            GuiBase.openGui(this.getParent());
        });
        x += buttonWidth + 10;

        this.createButton(x, y, buttonWidth, ButtonType.CANCEL, (btn, mbtn) -> {
            this.listener.onActionCancelled();
            GuiBase.openGui(this.getParent());
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

    protected int getButtonWidth()
    {
        int width = 0;

        for (ButtonType type : ButtonType.values())
        {
            width = Math.max(width, this.getStringWidth(type.getDisplayName()) + 10);
        }

        return width;
    }

    protected void createButton(int x, int y, int buttonWidth, ButtonType type, IButtonActionListener listener)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, buttonWidth, 20, type.getDisplayName());
        this.addButton(button, listener);
    }

    @Override
    public void addMessage(MessageType type, int lifeTime, String messageKey, Object... args)
    {
        if (this.getParent() instanceof IMessageConsumer)
        {
            ((IMessageConsumer) this.getParent()).addMessage(type, lifeTime, messageKey, args);
        }
        else
        {
            super.addMessage(type, lifeTime, messageKey, args);
        }
    }

    @Override
    public void onTaskCompleted()
    {
        if (this.getParent() instanceof ICompletionListener)
        {
            ((ICompletionListener) this.getParent()).onTaskCompleted();
        }
    }

    @Override
    public void onTaskAborted()
    {
        if (this.getParent() instanceof ICompletionListener)
        {
            ((ICompletionListener) this.getParent()).onTaskAborted();
        }
    }

    protected enum ButtonType
    {
        OK      ("malilib.gui.button.ok"),
        CANCEL  ("malilib.gui.button.cancel");

        private final String labelKey;

        private ButtonType(String labelKey)
        {
            this.labelKey = labelKey;
        }

        public String getDisplayName()
        {
            return (this == ButtonType.OK ? GuiBase.TXT_GREEN : GuiBase.TXT_RED) + StringUtils.translate(this.labelKey) + GuiBase.TXT_RST;
        }
    }
}
