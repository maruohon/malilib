package fi.dy.masa.malilib.gui.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.action.AliasAction;
import fi.dy.masa.malilib.action.MacroAction;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.action.ParameterizableNamedAction;
import fi.dy.masa.malilib.action.ParameterizedNamedAction;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseListEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.action.ActionListBaseActionEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.action.ParameterizableActionEntryWidget;
import fi.dy.masa.malilib.registry.Registry;

public class MacroActionEditScreen extends BaseActionListScreen
{
    protected final DataListWidget<NamedAction> rightSideListWidget;
    protected final ImmutableList<NamedAction> originalMacroActionsList;
    protected final List<NamedAction> macroActionsList;
    protected final LabelWidget macroActionsLabelWidget;
    protected final GenericButton addActionsButton;
    protected final MacroAction macro;

    public MacroActionEditScreen(MacroAction macro)
    {
        super("", Collections.emptyList(), null);

        this.macro = macro;
        this.originalMacroActionsList = macro.getActionList();
        this.centerGap = 20;

        this.setTitle("malilib.gui.title.edit_macro");

        this.macroActionsLabelWidget = new LabelWidget("malilib.gui.label.macro_edit_screen.macro_actions");

        this.addActionsButton = GenericButton.createIconOnly(DefaultIcons.LIST_ADD_PLUS_13, this::addSelectedActions);
        this.addActionsButton.translateAndAddHoverString("malilib.hover_info.macro_edit_screen.add_actions");

        this.leftSideListWidget.setEntryWidgetFactory(this::createMacroSourceActionsWidget);
        this.rightSideListWidget = this.createRightSideActionListWidget();

        // fetch the backing list reference from the list widget
        this.macroActionsList = this.rightSideListWidget.getCurrentContents();
        this.macroActionsList.addAll(this.originalMacroActionsList);
    }

    @Override
    protected void addActionListScreenWidgets()
    {
        super.addActionListScreenWidgets();

        this.addWidget(this.macroActionsLabelWidget);
        this.addWidget(this.addActionsButton);
        this.addListWidget(this.rightSideListWidget);
    }

    @Override
    protected void updateActionListScreenWidgetPositions(int x, int y, int w)
    {
        super.updateActionListScreenWidgetPositions(x, y, w);

        x = this.leftSideListWidget.getRight();
        y = this.leftSideListWidget.getY();
        this.addActionsButton.setPosition(x + 3, y + 4);

        x += this.centerGap;
        int h = this.screenHeight - y - 6;
        this.rightSideListWidget.setPositionAndSize(x, y, w, h);
        this.macroActionsLabelWidget.setPosition(x + 2, y - 12);
    }

    @Override
    protected void saveChangesOnScreenClose()
    {
        if (this.originalMacroActionsList.equals(this.macroActionsList) == false)
        {
            this.macro.setActionList(ImmutableList.copyOf(this.macroActionsList));
            Registry.ACTION_REGISTRY.saveToFile();
        }
    }

    protected void addSelectedActions()
    {
        Set<NamedAction> selectedActions = this.leftSideListWidget.getSelectedEntries();

        if (selectedActions.isEmpty() == false)
        {
            this.macroActionsList.addAll(selectedActions);
            this.rightSideListWidget.refreshEntries();
        }
    }

    protected boolean addAction(NamedAction action)
    {
        this.macroActionsList.add(action);
        this.rightSideListWidget.refreshEntries();
        return true;
    }

    protected void removeAction(NamedAction action)
    {
        this.macroActionsList.remove(action);
        this.rightSideListWidget.getEntrySelectionHandler().clearSelection();
        this.rightSideListWidget.refreshEntries();
    }

    protected void removeSelectedActions()
    {
        Set<Integer> selectedActions = this.rightSideListWidget.getEntrySelectionHandler().getSelectedEntryIndices();

        if (selectedActions.isEmpty() == false)
        {
            List<Integer> indices = new ArrayList<>(selectedActions);

            // reverse order, so that we can remove the entries without the indices being shifted over
            indices.sort(Comparator.reverseOrder());

            for (int index : indices)
            {
                if (index >= 0 && index < this.macroActionsList.size())
                {
                    this.macroActionsList.remove(index);
                }
            }

            this.rightSideListWidget.getEntrySelectionHandler().clearSelection();
            this.rightSideListWidget.refreshEntries();
        }
    }

    @Override
    protected DataListWidget<NamedAction> createRightSideActionListWidget()
    {
        DataListWidget<NamedAction> listWidget = this.createBaseActionListWidget(Collections::emptyList);

        listWidget.setFetchFromSupplierOnRefresh(false);
        listWidget.setEntryWidgetFactory(this::createMacroMemberWidget);

        return listWidget;
    }

    protected ActionListBaseActionEntryWidget createBaseMacroEditScreenActionWidget(
            int x, int y, int width, int height, int listIndex, int originalListIndex,
            NamedAction data, DataListWidget<NamedAction> listWidget)
    {
        ActionListBaseActionEntryWidget widget;

        if (data instanceof ParameterizableNamedAction)
        {
            ParameterizableActionEntryWidget parWidget = new ParameterizableActionEntryWidget(
                    x, y, width, height, listIndex, originalListIndex, data, listWidget);
            parWidget.setParameterizedActionConsumer(this::addAction);
            parWidget.setParameterizationButtonHoverText("malilib.button.hover.parameterize_action_for_macro");
            setWidgetStartingStyleFrom(parWidget, "malilib.style.action_list_screen.widget.parameterizable");
            widget = parWidget;
        }
        else
        {
            widget = new ActionListBaseActionEntryWidget(
                    x, y, width, height, listIndex, originalListIndex, data, listWidget);
        }

        if (data instanceof MacroAction)
        {
            setWidgetStartingStyleFrom(widget, "malilib.style.action_list_screen.widget.macro");
        }
        else if (data instanceof AliasAction)
        {
            setWidgetStartingStyleFrom(widget, "malilib.style.action_list_screen.widget.alias");
        }
        else if (data instanceof ParameterizedNamedAction)
        {
            setWidgetStartingStyleFrom(widget, "malilib.style.action_list_screen.widget.parameterized");
        }

        widget.setAddCreateAliasButton(false);

        return widget;
    }

    protected BaseListEntryWidget createMacroSourceActionsWidget(
            int x, int y, int width, int height, int listIndex, int originalListIndex,
            NamedAction data, DataListWidget<NamedAction> listWidget)
    {
        ActionListBaseActionEntryWidget widget = this.createBaseMacroEditScreenActionWidget(
                x, y, width, height, listIndex, originalListIndex, data, listWidget);

        widget.setNoRemoveButtons();

        return widget;
    }

    protected BaseListEntryWidget createMacroMemberWidget(
            int x, int y, int width, int height, int listIndex, int originalListIndex,
            NamedAction data, DataListWidget<NamedAction> listWidget)
    {
        ActionListBaseActionEntryWidget widget = this.createBaseMacroEditScreenActionWidget(
                x, y, width, height, listIndex, originalListIndex, data, listWidget);

        widget.setCanReOrder(true);
        widget.setActionRemoveFunction(this::removeAction);

        return widget;
    }
}
