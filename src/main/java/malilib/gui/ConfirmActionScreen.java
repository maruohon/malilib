package malilib.gui;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import malilib.gui.widget.LabelWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.listener.EventListener;
import malilib.render.text.StyledText;
import malilib.render.text.StyledTextLine;
import malilib.render.text.StyledTextUtils;

public class ConfirmActionScreen extends BaseScreen
{
    protected final EventListener confirmListener;
    @Nullable protected final EventListener cancelListener;
    protected final LabelWidget labelWidget;
    protected final GenericButton confirmButton;
    protected final GenericButton cancelButton;

    public ConfirmActionScreen(int width,
                               String titleKey,
                               EventListener confirmListener,
                               String messageKey, Object... args)
    {
        this(width, titleKey, confirmListener, null, messageKey, args);
    }

    public ConfirmActionScreen(int width,
                               String titleKey,
                               EventListener confirmListener,
                               @Nullable EventListener cancelListener,
                               String messageKey, Object... args)
    {
        this.confirmListener = confirmListener;
        this.cancelListener = cancelListener;
        this.useTitleHierarchy = false;
        this.renderBorder = true;
        this.backgroundColor = 0xFf000000;

        ImmutableList<StyledTextLine> messageLines = StyledTextUtils.wrapStyledTextToMaxWidth(StyledText.translate(messageKey, args).lines, width - 30);
        this.labelWidget = new LabelWidget(0xFFC0C0C0, messageLines);

        this.confirmButton = GenericButton.create("malilibdev.button.misc.confirm.colored", this::onConfirm);
        this.cancelButton = GenericButton.create("malilibdev.button.misc.cancel.colored", this::onCancel);

        this.setTitle(titleKey);
        this.setScreenWidthAndHeight(width, 50 + StyledTextUtils.getRenderHeight(messageLines));
        this.centerOnScreen();
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.labelWidget);
        this.addWidget(this.confirmButton);
        this.addWidget(this.cancelButton);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + 10;
        int y = this.y + 20;

        this.labelWidget.setPosition(x, y);

        y = this.y + this.screenHeight - 26;
        this.confirmButton.setPosition(x, y);
        this.cancelButton.setPosition(this.confirmButton.getRight() + 10, y);
    }

    public void setTextColor(int color)
    {
        this.labelWidget.getTextSettings().setTextColor(color);
    }

    protected void onConfirm()
    {
        openScreen(this.getParent());

        if (this.confirmListener != null)
        {
            this.confirmListener.onEvent();
        }
    }

    protected void onCancel()
    {
        openScreen(this.getParent());

        if (this.cancelListener != null)
        {
            this.cancelListener.onEvent();
        }
    }
}
