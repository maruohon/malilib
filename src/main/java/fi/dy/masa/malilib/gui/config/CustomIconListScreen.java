package fi.dy.masa.malilib.gui.config;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.MaLiLibConfigScreen;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.action.ActionContext;
import fi.dy.masa.malilib.gui.BaseListScreen;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.icon.IconRegistry;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.IconEntryWidget;
import fi.dy.masa.malilib.input.ActionResult;

public class CustomIconListScreen extends BaseListScreen<DataListWidget<Icon>>
{
    protected final GenericButton addIconButton;

    public CustomIconListScreen()
    {
        super(10, 74, 20, 86,
              MaLiLibReference.MOD_ID, MaLiLibConfigScreen.ALL_TABS, MaLiLibConfigScreen.GENERIC);

        this.setTitle("malilib.gui.title.custom_icons_list_screen");

        this.addIconButton = new GenericButton("malilib.gui.button.custom_icon_list_screen.add_icon");
        this.addIconButton.setActionListener(this::openAddIconScreen);
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();
    }

    @Override
    public void onGuiClosed()
    {
        IconRegistry.INSTANCE.saveToFileIfDirty();
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

        int x = this.x + 10;
        int y = this.y + 50;

        this.addIconButton.setPosition(x, y);
    }

    @Nullable
    @Override
    protected DataListWidget<Icon> createListWidget(int listX, int listY, int listWidth, int listHeight)
    {
        DataListWidget<Icon> listWidget = new DataListWidget<>(listX, listY, listWidth, listHeight,
                                                               IconRegistry.INSTANCE::getUserIcons);
        listWidget.setNormalBorderWidth(1);
        listWidget.setListEntryWidgetFixedHeight(22);
        listWidget.setFetchFromSupplierOnRefresh(true);
        listWidget.setEntryWidgetFactory(IconEntryWidget::new);
        return listWidget;
    }

    public void addIcon(Icon icon)
    {
        IconRegistry.INSTANCE.registerUserIcon(icon);
        this.getListWidget().refreshEntries();
    }

    protected void openAddIconScreen()
    {
        CustomIconEditScreen screen = new CustomIconEditScreen(this::addIcon);
        screen.setParent(this);
        BaseScreen.openPopupScreen(screen);
    }

    public static ActionResult openCustomIconListScreen(ActionContext ctx)
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
