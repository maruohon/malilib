package fi.dy.masa.malilib.gui.config;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.action.ActionContext;
import fi.dy.masa.malilib.gui.BaseListScreen;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.TextInputScreen;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.MessageRedirectEntryWidget;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.overlay.message.MessageOutput;
import fi.dy.masa.malilib.overlay.message.MessageRedirectManager;
import fi.dy.masa.malilib.overlay.message.MessageRedirectManager.MessageRedirect;

public class MessageRedirectListScreen extends BaseListScreen<DataListWidget<MessageRedirect>>
{
    protected final GenericButton addRedirectButton;

    public MessageRedirectListScreen()
    {
        super(10, 52, 20, 64);

        this.setTitle("malilib.gui.title.message_redirect_list_screen");

        this.addRedirectButton = new GenericButton("malilib.gui.button.message_redirect_list_screen.add_redirect");
        this.addRedirectButton.setActionListener(this::openAddRedirectScreen);
    }

    @Override
    public void onGuiClosed()
    {
        MessageRedirectManager.INSTANCE.saveToFileIfDirty();
        super.onGuiClosed();
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.addRedirectButton);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + 10;
        int y = this.y + 28;

        this.addRedirectButton.setPosition(x, y);
    }

    @Nullable
    @Override
    protected DataListWidget<MessageRedirect> createListWidget(int listX, int listY, int listWidth, int listHeight)
    {
        DataListWidget<MessageRedirect> listWidget = new DataListWidget<>(listX, listY, listWidth, listHeight,
                                                                          MessageRedirectManager.INSTANCE::getAllRedirects);
        listWidget.getBorderRenderer().getNormalSettings().setBorderWidth(1);
        listWidget.setListEntryWidgetFixedHeight(16);
        listWidget.setFetchFromSupplierOnRefresh(true);
        listWidget.setEntryWidgetFactory(MessageRedirectEntryWidget::new);
        return listWidget;
    }

    public boolean addRedirect(String translationKey)
    {
        MessageRedirect redirect = new MessageRedirect(translationKey, MessageOutput.MESSAGE_OVERLAY);
        MessageRedirectManager.INSTANCE.addRedirect(translationKey, redirect);
        this.getListWidget().refreshEntries();
        return true;
    }

    protected void openAddRedirectScreen()
    {
        TextInputScreen screen = new TextInputScreen("malilib.gui.title.add_message_redirect",
                                                     "", this, this::addRedirect);
        screen.setScreenWidth(300);
        screen.setInfoText("malilib.gui.label.add_message_redirect_for_key");
        BaseScreen.openPopupScreen(screen);
    }

    public static ActionResult openMessageRedirectListScreenAction(ActionContext ctx)
    {
        MessageRedirectListScreen screen = new MessageRedirectListScreen();
        BaseScreen.openScreen(screen);
        return ActionResult.SUCCESS;
    }
}
