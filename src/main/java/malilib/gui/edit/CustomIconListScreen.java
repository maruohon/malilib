package malilib.gui.edit;

import java.util.List;
import com.google.common.collect.ImmutableList;

import malilib.MaLiLibConfigScreen;
import malilib.MaLiLibReference;
import malilib.action.ActionContext;
import malilib.gui.BaseImportExportEntriesListScreen;
import malilib.gui.BaseListScreen;
import malilib.gui.BaseScreen;
import malilib.gui.ExportEntriesListScreen;
import malilib.gui.ImportEntriesListScreen;
import malilib.gui.icon.NamedBaseIcon;
import malilib.gui.icon.NamedIcon;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.DataListWidget;
import malilib.gui.widget.list.entry.IconEntryWidget;
import malilib.input.ActionResult;
import malilib.overlay.message.MessageDispatcher;
import malilib.registry.Registry;
import malilib.util.data.AppendOverwrite;

public class CustomIconListScreen extends BaseListScreen<DataListWidget<NamedIcon>>
{
    protected final GenericButton addIconButton;
    protected final GenericButton exportButton;
    protected final GenericButton importButton;

    public CustomIconListScreen()
    {
        super(10, 74, 20, 80, MaLiLibReference.MOD_ID, MaLiLibConfigScreen.ALL_TABS, MaLiLibConfigScreen.GENERIC);

        this.addIconButton = GenericButton.create(16, "malilib.button.custom_icons.add_icon", this::openAddIconScreen);
        this.exportButton  = GenericButton.create(16, "malilib.button.misc.export", this::openExportScreen);
        this.importButton  = GenericButton.create(16, "malilib.button.misc.import", this::openImportScreen);

        this.addPreScreenCloseListener(Registry.ICON::saveToFileIfDirty);
        this.createSwitchModConfigScreenDropDown(MaLiLibReference.MOD_INFO);
        this.setTitle("malilib.title.screen.configs.custom_icons_list_screen", MaLiLibReference.MOD_VERSION);
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.addIconButton);
        this.addWidget(this.importButton);
        this.addWidget(this.exportButton);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int y = this.y + 57;
        this.addIconButton.setPosition(this.x + 10, y);
        this.exportButton.setRight(this.getListWidget().getRight());
        this.exportButton.setY(y);
        this.importButton.setRight(this.exportButton.getX() - 2);
        this.importButton.setY(y);
    }

    @Override
    protected DataListWidget<NamedIcon> createListWidget()
    {
        DataListWidget<NamedIcon> listWidget = new DataListWidget<>(Registry.ICON::getUserIcons, true);

        listWidget.setListEntryWidgetFixedHeight(22);
        listWidget.setDataListEntryWidgetFactory(IconEntryWidget::new);
        listWidget.addDefaultSearchBar();
        listWidget.setEntryFilterStringFunction(i -> ImmutableList.of(i.getName(), i.getTexture().toString()));

        return listWidget;
    }

    public void addIcon(NamedIcon icon)
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

    protected void openExportScreen()
    {
        this.initAndOpenExportOrImportScreen(new ExportEntriesListScreen<>(Registry.ICON.getUserIcons(),
                                                                           NamedIcon::toJson));
    }

    protected void openImportScreen()
    {
        this.initAndOpenExportOrImportScreen(new ImportEntriesListScreen<>(NamedBaseIcon::namedBaseIconFromJson,
                                                                           this::importEntries));
    }

    protected void initAndOpenExportOrImportScreen(BaseImportExportEntriesListScreen<NamedIcon> screen)
    {
        screen.setEntryNameFunction(NamedIcon::getName);
        screen.setEntryIconFunction(e -> e);
        screen.setHoverInfoFunction(IconEntryWidget::getHoverInfoForIcon);
        screen.setListEntryWidgetHeight(22);

        BaseScreen.openScreenWithParent(screen);
    }

    protected void importEntries(List<NamedIcon> list, AppendOverwrite mode)
    {
        if (mode == AppendOverwrite.OVERWRITE)
        {
            Registry.ICON.clearAllUserIcons();
        }

        int count = 0;

        for (NamedIcon icon : list)
        {
            if (Registry.ICON.registerUserIcon(icon))
            {
                ++count;
            }
        }

        if (count > 0)
        {
            MessageDispatcher.success("malilib.message.info.successfully_imported_n_entries", count);
        }
        else
        {
            MessageDispatcher.warning("malilib.message.warn.import_entries.didnt_import_any_entries");
        }
    }

    public static ActionResult openCustomIconListScreenAction(ActionContext ctx)
    {
        CustomIconListScreen screen = new CustomIconListScreen();
        screen.setCurrentTab(MaLiLibConfigScreen.ICONS);
        BaseScreen.openScreen(screen);
        return ActionResult.SUCCESS;
    }

    public static BaseScreen openCustomIconListScreen()
    {
        return new CustomIconListScreen();
    }
}
