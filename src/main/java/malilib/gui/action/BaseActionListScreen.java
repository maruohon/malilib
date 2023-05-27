package malilib.gui.action;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;

import malilib.action.ActionGroup;
import malilib.action.ActionType;
import malilib.action.AliasAction;
import malilib.action.MacroAction;
import malilib.action.NamedAction;
import malilib.action.ParameterizableNamedAction;
import malilib.action.ParameterizedNamedAction;
import malilib.gui.BaseImportExportEntriesListScreen;
import malilib.gui.BaseMultiListScreen;
import malilib.gui.BaseScreen;
import malilib.gui.ExportEntriesListScreen;
import malilib.gui.ImportEntriesListScreen;
import malilib.gui.tab.ScreenTab;
import malilib.gui.widget.DropDownListWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.DataListWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.gui.widget.list.entry.action.ActionListBaseActionEntryWidget;
import malilib.gui.widget.list.entry.action.ParameterizableActionEntryWidget;
import malilib.registry.Registry;
import malilib.util.data.AppendOverwrite;

public abstract class BaseActionListScreen extends BaseMultiListScreen
{
    protected final DropDownListWidget<ActionGroup> allActionTypesDropdown;
    protected final DataListWidget<NamedAction> leftSideListWidget;
    protected final GenericButton exportButton;
    protected final GenericButton importButton;
    protected DataListWidget<NamedAction> rightSideListWidget;
    @Nullable protected String importRadioWidgetHoverText;
    protected int centerGap = 10;

    public BaseActionListScreen(String screenId, List<? extends ScreenTab> screenTabs, @Nullable ScreenTab defaultTab)
    {
        super(screenId, screenTabs, defaultTab);

        this.exportButton  = GenericButton.create(16, "malilib.button.misc.export", this::openExportScreen);
        this.importButton  = GenericButton.create(16, "malilib.button.misc.import", this::openImportScreen);

        this.allActionTypesDropdown = new DropDownListWidget<>(14, 10, ActionGroup.VALUES, ActionGroup::getDisplayName);
        this.allActionTypesDropdown.setSelectedEntry(ActionGroup.ALL);
        this.allActionTypesDropdown.setSelectionListener((t) -> this.initScreen());
        this.allActionTypesDropdown.translateAndAddHoverString("malilib.hover.action.action_types_explanation");

        this.leftSideListWidget = this.createLeftSideActionListWidget();
        this.addPreScreenCloseListener(this::saveChangesOnScreenClose);
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addActionListScreenWidgets();
        this.addWidget(this.importButton);
        this.addWidget(this.exportButton);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = 10;
        int w = (this.screenWidth - 20 - this.centerGap) / 2;
        this.updateActionListScreenWidgetPositions(x, this.y + 44, w);

        int y = this.rightSideListWidget.getY() - 18;
        this.exportButton.setRight(this.rightSideListWidget.getRight());
        this.exportButton.setY(y);
        this.importButton.setRight(this.exportButton.getX() - 2);
        this.importButton.setY(y);
    }

    protected void addActionListScreenWidgets()
    {
        this.addWidget(this.allActionTypesDropdown);
        this.addListWidget(this.leftSideListWidget);
    }

    protected void updateActionListScreenWidgetPositions(int x, int y, int w)
    {
        this.allActionTypesDropdown.setPosition(x, y);
        y += 16;

        int h = this.screenHeight - y - 6;
        this.leftSideListWidget.setPositionAndSize(x, y, w, h);
    }

    protected abstract void saveChangesOnScreenClose();

    protected void openExportScreen()
    {
        this.initAndOpenExportOrImportScreen(new ExportEntriesListScreen<>(this.rightSideListWidget.getFilteredDataList(),
                                                                           NamedAction::toJson));
    }

    protected void openImportScreen()
    {
        ImportEntriesListScreen<NamedAction> screen = new ImportEntriesListScreen<>(ActionType::loadActionFromJson,
                                                                           this::importEntries);
        if (this.importRadioWidgetHoverText != null)
        {
            screen.setRadioWidgetHoverText(this.importRadioWidgetHoverText);
        }

        this.initAndOpenExportOrImportScreen(screen);
    }

    @Nullable
    protected NamedAction createActionFromJson(JsonElement el)
    {
        if (el != null && el.isJsonObject())
        {
            return NamedAction.baseActionFromJson(el.getAsJsonObject());
        }

        return null;
    }

    protected void initAndOpenExportOrImportScreen(BaseImportExportEntriesListScreen<NamedAction> screen)
    {
        screen.setWidgetFactory(this::createExportImportEntryWidget);
        screen.getListWidget().setListEntryWidgetFixedHeight(14);
        BaseScreen.openScreenWithParent(screen);
    }

    protected abstract void importEntries(List<NamedAction> list, AppendOverwrite mode);

    protected ActionListBaseActionEntryWidget createExportImportEntryWidget(NamedAction data,
                                                                            DataListEntryWidgetData constructData)
    {
        ActionListBaseActionEntryWidget widget = new ActionListBaseActionEntryWidget(data, constructData);
        widget.setNoRemoveButtons();
        return widget;
    }

    protected ImmutableList<NamedAction> getLeftSideActions()
    {
        return this.allActionTypesDropdown.getSelectedEntry().getActions();
    }

    protected DataListWidget<NamedAction> createBaseActionListWidget(Supplier<List<NamedAction>> supplier,
                                                                     boolean fetchFromSupplierOnRefresh)
    {
        DataListWidget<NamedAction> listWidget = new DataListWidget<>(supplier, fetchFromSupplierOnRefresh);

        listWidget.setListEntryWidgetFixedHeight(14);
        listWidget.setEntryFilterStringFunction(NamedAction::getSearchString);
        listWidget.setDataListEntryWidgetFactory(this::createEntryWidget);

        return listWidget;
    }

    protected DataListWidget<NamedAction> createLeftSideActionListWidget()
    {
        DataListWidget<NamedAction> listWidget = this.createBaseActionListWidget(this::getLeftSideActions, true);
        listWidget.addDefaultSearchBar();
        listWidget.getEntrySelectionHandler()
                .setAllowSelection(true)
                .setAllowMultiSelection(true)
                .setModifierKeyMultiSelection(true);
        return listWidget;
    }

    protected void refreshBothLists()
    {
        this.leftSideListWidget.refreshEntries();
        this.rightSideListWidget.refreshEntries();
    }

    protected abstract DataListWidget<NamedAction> createRightSideActionListWidget();

    protected ActionListBaseActionEntryWidget createEntryWidget(NamedAction data,
                                                                DataListEntryWidgetData constructData)
    {
        ActionListBaseActionEntryWidget widget;

        if (data instanceof ParameterizableNamedAction)
        {
            ParameterizableActionEntryWidget parWidget = new ParameterizableActionEntryWidget(data, constructData);
            parWidget.setParameterizationButtonHoverText("malilib.hover.button.parameterize_action");
            widget = parWidget;
        }
        else
        {
            widget = new ActionListBaseActionEntryWidget(data, constructData);

            if (data instanceof AliasAction)
            {
                widget.setActionRemoveFunction((i, a) -> this.removeAction(a, Registry.ACTION_REGISTRY::removeAlias));
            }
            else if (data instanceof MacroAction)
            {
                widget.setActionEditFunction((i, a) -> ActionListBaseActionEntryWidget.openMacroEditScreen(a, this));
                widget.setActionRemoveFunction((i, a) -> this.removeAction(a, Registry.ACTION_REGISTRY::removeMacro));
            }
            else if (data instanceof ParameterizedNamedAction)
            {
                widget.setActionRemoveFunction((i, a) -> this.removeAction(a, Registry.ACTION_REGISTRY::removeParameterizedAction));
            }
        }

        widget.setAddCreateAliasButton(true);

        return widget;
    }

    protected void removeAction(NamedAction action, Consumer<NamedAction> removeFunction)
    {
        removeFunction.accept(action);
        this.refreshBothLists();
    }
}
