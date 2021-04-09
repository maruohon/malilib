package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.listener.ConfirmationListener;
import fi.dy.masa.malilib.listener.TaskCompletionListener;
import fi.dy.masa.malilib.render.text.StyledText;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.StyledTextUtils;

public class ConfirmActionScreen extends BaseScreen implements TaskCompletionListener
{
    protected final ImmutableList<StyledTextLine> messageLines;
    protected final ConfirmationListener listener;
    protected int textColor = 0xFFC0C0C0;

    public ConfirmActionScreen(int width, String titleKey, ConfirmationListener listener, @Nullable GuiScreen parent, String messageKey, Object... args)
    {
        this.setParent(parent);
        this.title = StringUtils.translate(titleKey);
        this.listener = listener;
        this.useTitleHierarchy = false;
        this.zLevel = 1f;

        this.messageLines = StyledTextUtils.wrapStyledTextToMaxWidth(StyledText.translatedOf(messageKey, args).lines, width - 30);

        this.setScreenWidthAndHeight(width, this.getMessageHeight() + 50);
        this.centerOnScreen();
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        int x = this.x + 10;

        this.addWidget(new LabelWidget(x, this.y + 20, -1, -1, this.textColor, this.messageLines));

        int buttonWidth = 10 + StringUtils.getMaxStringRenderWidth(StringUtils::translate, "malilib.gui.button.colored.confirm", "malilib.gui.button.colored.cancel");
        int y = this.y + this.screenHeight - 26;

        // FIXME Should the parent screen be opened before triggering the action, that way the TaskCompletionListener redirect would not be needed?
        this.addButton(new GenericButton(x, y, buttonWidth, 20, "malilib.gui.button.colored.confirm"), (btn, mbtn) -> {
            this.listener.onActionConfirmed();
            BaseScreen.openScreen(this.getParent());
        });
        x += buttonWidth + 10;

        this.addButton(new GenericButton(x, y, buttonWidth, 20, "malilib.gui.button.colored.cancel"), (btn, mbtn) -> {
            this.listener.onActionCancelled();
            BaseScreen.openScreen(this.getParent());
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
