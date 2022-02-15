package fi.dy.masa.malilib.gui.edit;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.MaLiLibConfigScreen;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.action.ActionContext;
import fi.dy.masa.malilib.gui.BaseListScreen;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.IconEntryWidget;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.registry.Registry;

public class CustomIconListScreen extends BaseListScreen<DataListWidget<Icon>>
{
    protected final GenericButton addIconButton;

    public CustomIconListScreen()
    {
        super(10, 74, 20, 80, MaLiLibReference.MOD_ID, MaLiLibConfigScreen.ALL_TABS, MaLiLibConfigScreen.GENERIC);

        this.setTitle("malilib.gui.title.custom_icons_list_screen");
        this.addIconButton = GenericButton.create(16, "malilib.gui.button.custom_icon_list_screen.add_icon", this::openAddIconScreen);
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();
    }

    @Override
    public void onGuiClosed()
    {
        Registry.ICON.saveToFileIfDirty();
        super.onGuiClosed();
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.addIconButton);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        this.addIconButton.setPosition(this.x + 10, this.y + 57);
    }

    @Nullable
    @Override
    protected DataListWidget<Icon> createListWidget(int listX, int listY, int listWidth, int listHeight)
    {
        DataListWidget<Icon> listWidget = new DataListWidget<>(listX, listY, listWidth, listHeight,
                                                               Registry.ICON::getUserIcons);
        listWidget.getBorderRenderer().getNormalSettings().setBorderWidth(1);
        listWidget.setListEntryWidgetFixedHeight(22);
        listWidget.setFetchFromSupplierOnRefresh(true);
        listWidget.setEntryWidgetFactory(IconEntryWidget::new);
        return listWidget;
    }

    public void addIcon(Icon icon)
    {
        Registry.ICON.registerUserIcon(icon);
        this.getListWidget().refreshEntries();
    }

    protected void openAddIconScreen()
    {
        CustomIconEditScreen screen = new CustomIconEditScreen(this::addIcon);
        screen.setParent(this);
        BaseScreen.openPopupScreen(screen);
    }

    public static ActionResult openCustomIconListScreenAction(ActionContext ctx)
    {
        CustomIconListScreen screen = new CustomIconListScreen();
        screen.setCurrentTab(MaLiLibConfigScreen.ICONS);
        BaseScreen.openScreen(screen);
        return ActionResult.SUCCESS;
    }

    public static BaseScreen openCustomIconListScreen(@Nullable GuiScreen currentScreen)
    {
        return new CustomIconListScreen();
    }
}
