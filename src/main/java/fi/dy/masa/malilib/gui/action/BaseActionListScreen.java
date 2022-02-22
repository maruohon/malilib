package fi.dy.masa.malilib.gui.action;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.action.ActionGroup;
import fi.dy.masa.malilib.action.AliasAction;
import fi.dy.masa.malilib.action.MacroAction;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.action.ParameterizableNamedAction;
import fi.dy.masa.malilib.action.ParameterizedNamedAction;
import fi.dy.masa.malilib.gui.BaseMultiListScreen;
import fi.dy.masa.malilib.gui.tab.ScreenTab;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.gui.widget.list.entry.action.ActionListBaseActionEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.action.ParameterizableActionEntryWidget;
import fi.dy.masa.malilib.registry.Registry;

public abstract class BaseActionListScreen extends BaseMultiListScreen
{
    protected final DropDownListWidget<ActionGroup> allActionTypesDropdown;
    protected final DataListWidget<NamedAction> leftSideListWidget;
    protected DataListWidget<NamedAction> rightSideListWidget;
    protected int centerGap = 10;

    public BaseActionListScreen(String screenId, List<? extends ScreenTab> screenTabs, @Nullable ScreenTab defaultTab)
    {
        super(screenId, screenTabs, defaultTab);

        this.allActionTypesDropdown = new DropDownListWidget<>(-1, 14, 140, 10, ActionGroup.VALUES, ActionGroup::getDisplayName);
        this.allActionTypesDropdown.setSelectedEntry(ActionGroup.ALL);
        this.allActionTypesDropdown.setSelectionListener((t) -> this.initScreen());
        this.allActionTypesDropdown.translateAndAddHoverString("malilib.hover.action.action_types_explanation");

        this.leftSideListWidget = this.createLeftSideActionListWidget();
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        //this.leftSideListWidget.refreshEntries();
        //this.rightSideListWidget.refreshEntries();
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addActionListScreenWidgets();
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = 10;
        int w = (this.screenWidth - 20 - this.centerGap) / 2;
        this.updateActionListScreenWidgetPositions(x, this.y + 44, w);
    }

    @Override
    public void onGuiClosed()
    {
        this.saveChangesOnScreenClose();

        super.onGuiClosed();
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

    protected ImmutableList<NamedAction> getLeftSideActions()
    {
        return this.allActionTypesDropdown.getSelectedEntry().getActions();
    }

    protected DataListWidget<NamedAction> createBaseActionListWidget(Supplier<List<NamedAction>> supplier)
    {
        DataListWidget<NamedAction> listWidget = new DataListWidget<>(0, 0, 120, 120, supplier);

        listWidget.setListEntryWidgetFixedHeight(14);
        listWidget.getBorderRenderer().getNormalSettings().setBorderWidth(1);
        listWidget.setFetchFromSupplierOnRefresh(true);
        listWidget.setEntryWidgetFactory(this::createEntryWidget);
        listWidget.setEntryFilterStringFactory(NamedAction::getSearchString);

        return listWidget;
    }

    protected DataListWidget<NamedAction> createLeftSideActionListWidget()
    {
        DataListWidget<NamedAction> listWidget = this.createBaseActionListWidget(this::getLeftSideActions);
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
