package malilib.gui;

import java.util.List;

import malilib.gui.widget.LabelWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.render.text.StyledTextLine;
import malilib.render.text.StyledTextUtils;

public abstract class BaseConfirmActionScreen extends BaseScreen
{
    protected final LabelWidget labelWidget;
    protected final GenericButton confirmButton;
    protected final GenericButton cancelButton;

    public BaseConfirmActionScreen(int width,
                                   String titleKey,
                                   String confirmButtonTranslationKey,
                                   String cancelButtonTranslationKey,
                                   String messageKey, Object... messageArgs)
    {
        this.useTitleHierarchy = false;
        this.renderBorder = true;
        this.backgroundColor = 0xFF000000;

        List<StyledTextLine> lines = StyledTextLine.translate(messageKey, messageArgs);
        List<StyledTextLine> messageLines = StyledTextUtils.wrapStyledTextToMaxWidth(lines, width - 30);
        this.labelWidget = new LabelWidget(0xFFC0C0C0).setLines(messageLines);

        this.confirmButton = GenericButton.create(confirmButtonTranslationKey, this::onConfirm);
        this.cancelButton = GenericButton.create(cancelButtonTranslationKey, this::onCancel);

        this.setTitle(titleKey);
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

    protected int getRequiredScreenHeight()
    {
        return this.labelWidget.getHeight() + 50;
    }

    protected abstract void onConfirm();

    protected abstract void onCancel();
}
