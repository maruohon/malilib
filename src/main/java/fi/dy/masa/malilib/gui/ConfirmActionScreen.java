package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.listener.ConfirmationListener;
import fi.dy.masa.malilib.render.text.StyledText;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StyledTextUtils;

public class ConfirmActionScreen extends BaseScreen
{
    protected final ConfirmationListener listener;
    protected final LabelWidget labelWidget;
    protected final GenericButton confirmButton;
    protected final GenericButton cancelButton;
    protected int textColor = 0xFFC0C0C0;

    public ConfirmActionScreen(int width, String titleKey, ConfirmationListener listener, @Nullable GuiScreen parent, String messageKey, Object... args)
    {
        this.listener = listener;
        this.useTitleHierarchy = false;
        this.setTitle(titleKey);

        ImmutableList<StyledTextLine> messageLines = StyledTextUtils.wrapStyledTextToMaxWidth(StyledText.translate(messageKey, args).lines, width - 30);
        this.labelWidget = new LabelWidget(0xFFC0C0C0, messageLines);

        this.confirmButton = GenericButton.simple("malilib.gui.button.colored.confirm", this::onConfirm);
        this.cancelButton = GenericButton.simple("malilib.gui.button.colored.cancel", this::onCancel);

        this.setParent(parent);
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

    protected void onConfirm()
    {
        BaseScreen.openScreen(this.getParent());
        this.listener.onActionConfirmed();
    }

    protected void onCancel()
    {
        BaseScreen.openScreen(this.getParent());
        this.listener.onActionCancelled();
    }
}
