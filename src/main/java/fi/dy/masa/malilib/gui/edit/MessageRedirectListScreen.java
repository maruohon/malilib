package fi.dy.masa.malilib.gui.edit;

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
import fi.dy.masa.malilib.overlay.message.MessageRedirectManager.MessageRedirect;
import fi.dy.masa.malilib.registry.Registry;

public class MessageRedirectListScreen extends BaseListScreen<DataListWidget<MessageRedirect>>
{
    protected final GenericButton addRedirectButton;

    public MessageRedirectListScreen()
    {
        super(10, 52, 20, 64);

        this.setTitle("malilib.title.screen.message_redirect_list_screen");

        String key = "malilib.button.message_redirect.add_redirect";
        this.addRedirectButton = GenericButton.create(key, this::openAddRedirectScreen);
        this.screenCloseListener = Registry.MESSAGE_REDIRECT_MANAGER::saveToFileIfDirty;
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

        this.addRedirectButton.setPosition(this.x + 10, this.y + 28);
    }

    @Nullable
    @Override
    protected DataListWidget<MessageRedirect> createListWidget()
    {
        DataListWidget<MessageRedirect> listWidget
                = new DataListWidget<>(Registry.MESSAGE_REDIRECT_MANAGER::getAllRedirects, true);

        listWidget.setListEntryWidgetFixedHeight(16);
        listWidget.setEntryWidgetFactory(MessageRedirectEntryWidget::new);

        return listWidget;
    }

    public boolean addRedirect(String translationKey)
    {
        MessageRedirect redirect = new MessageRedirect(translationKey, MessageOutput.MESSAGE_OVERLAY);
        Registry.MESSAGE_REDIRECT_MANAGER.addRedirect(translationKey, redirect);
        this.getListWidget().refreshEntries();
        return true;
    }

    protected void openAddRedirectScreen()
    {
        TextInputScreen screen = new TextInputScreen("malilib.title.screen.add_message_redirect",
                                                     "", this::addRedirect, this);
        screen.setScreenWidth(300);
        screen.setInfoText("malilib.label.message_redirect.add_redirect_for_key");
        BaseScreen.openPopupScreen(screen);
    }

    public static ActionResult openMessageRedirectListScreenAction(ActionContext ctx)
    {
        MessageRedirectListScreen screen = new MessageRedirectListScreen();
        BaseScreen.openScreen(screen);
        return ActionResult.SUCCESS;
    }
}
