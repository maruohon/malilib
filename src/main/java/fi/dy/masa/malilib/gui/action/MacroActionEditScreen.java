package fi.dy.masa.malilib.gui.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.action.ActionRegistry;
import fi.dy.masa.malilib.action.ActionRegistryImpl;
import fi.dy.masa.malilib.action.MacroAction;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.BaseMultiListScreen;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.NamedActionEntryWidget;

public class MacroActionEditScreen extends BaseMultiListScreen
{
    protected final DataListWidget<NamedAction> actionSourceListWidget;
    protected final DataListWidget<NamedAction> macroActionsListWidget;
    protected final ImmutableList<NamedAction> originalMacroActionsList;
    protected final List<NamedAction> macroActionsList;
    protected final LabelWidget allActionsLabelWidget;
    protected final LabelWidget macroActionsLabelWidget;
    protected final LabelWidget macroNameLabelWidget;
    protected final GenericButton addActionsButton;
    protected final GenericButton removeActionsButton;
    protected final BaseTextFieldWidget nameTextFieldWidget;
    protected final String originalName;
    protected final boolean creating;

    public MacroActionEditScreen(String name, Collection<NamedAction> actions, boolean creating)
    {
        super("", Collections.emptyList(), null);

        this.originalMacroActionsList = ImmutableList.copyOf(actions);
        this.originalName = name;
        this.creating = creating;

        this.setTitle("malilib.gui.title.edit_macro");

        this.macroNameLabelWidget = new LabelWidget("malilib.label.name.colon");
        this.allActionsLabelWidget = new LabelWidget("malilib.gui.label.action_list_screen.available_actions");
        this.macroActionsLabelWidget = new LabelWidget("malilib.gui.label.macro_edit_screen.macro_actions");

        this.nameTextFieldWidget = new BaseTextFieldWidget(200, 16, name);

        this.addActionsButton = GenericButton.createIconOnly(DefaultIcons.LIST_ADD_PLUS_13);
        this.addActionsButton.setActionListener(this::addSelectedActions);
        this.addActionsButton.translateAndAddHoverString("malilib.hover_info.macro_edit_screen.add_actions");

        this.removeActionsButton = GenericButton.createIconOnly(DefaultIcons.LIST_REMOVE_MINUS_13);
        this.removeActionsButton.setActionListener(this::removeSelectedActions);
        this.removeActionsButton.translateAndAddHoverString("malilib.hover_info.macro_edit_screen.remove_actions");

        this.actionSourceListWidget = this.createNamedActionListWidget(this::getActions, true);
        this.macroActionsListWidget = this.createNamedActionListWidget(Collections::emptyList, false);

        // fetch the backing list reference from the list widget
        this.macroActionsList = this.macroActionsListWidget.getCurrentContents();
        this.macroActionsList.addAll(actions);
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        this.actionSourceListWidget.refreshEntries();
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.macroNameLabelWidget);
        this.addWidget(this.nameTextFieldWidget);

        this.addWidget(this.allActionsLabelWidget);
        this.addWidget(this.macroActionsLabelWidget);

        this.addWidget(this.addActionsButton);
        this.addWidget(this.removeActionsButton);

        this.addListWidget(this.actionSourceListWidget);
        this.addListWidget(this.macroActionsListWidget);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = 10;
        int y = 20;

        this.macroNameLabelWidget.setPosition(x + 2, y + 4);
        this.nameTextFieldWidget.setPosition(this.macroNameLabelWidget.getRight() + 6, y);

        y += 22;
        int w = (this.screenWidth - 40) / 2;
        int h = this.screenHeight - y - 16;

        this.allActionsLabelWidget.setPosition(x + 2, y);
        this.actionSourceListWidget.setPositionAndSize(x, y + 10, w, h);

        x = this.actionSourceListWidget.getRight();
        y += 10;
        this.addActionsButton.setPosition(x + 3, y);
        this.removeActionsButton.setPosition(x + 3, y + 30);

        x += 19;
        this.macroActionsLabelWidget.setPosition(x + 2, y - 10);
        this.macroActionsListWidget.setPositionAndSize(x, y, w, h);
    }

    @Override
    public void onGuiClosed()
    {
        String name = this.nameTextFieldWidget.getText();

        if (this.creating ||
            this.originalName.equals(name) == false ||
            this.originalMacroActionsList.equals(this.macroActionsList) == false)
        {
            if (this.creating == false)
            {
                ActionRegistry.INSTANCE.removeMacro(this.originalName);
            }

            MacroAction macro = new MacroAction(name, ImmutableList.copyOf(this.macroActionsList));
            ActionRegistry.INSTANCE.addMacro(macro);

            ((ActionRegistryImpl) ActionRegistry.INSTANCE).saveToFileIfDirty();
        }

        super.onGuiClosed();
    }

    protected List<NamedAction> getActions()
    {
        return ActionRegistry.INSTANCE.getAllActions();
    }

    protected void addSelectedActions()
    {
        Set<NamedAction> selectedActions = this.actionSourceListWidget.getSelectedEntries();

        if (selectedActions.isEmpty() == false)
        {
            this.macroActionsList.addAll(selectedActions);
            this.macroActionsListWidget.refreshEntries();
        }
    }

    protected void removeSelectedActions()
    {
        Set<Integer> selectedActions = this.macroActionsListWidget.getEntrySelectionHandler().getSelectedEntryIndices();

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

            this.macroActionsListWidget.getEntrySelectionHandler().clearSelection();
            this.macroActionsListWidget.refreshEntries();
        }
    }

    protected DataListWidget<NamedAction> createNamedActionListWidget(Supplier<List<NamedAction>> listSupplier,
                                                                      boolean isSourceList)
    {
        DataListWidget<NamedAction> listWidget = new DataListWidget<>(0, 0, 200, 200, listSupplier);
        listWidget.setListEntryWidgetFixedHeight(12);
        listWidget.setNormalBorderWidth(1);
        listWidget.setFetchFromSupplierOnRefresh(isSourceList);
        listWidget.setEntryWidgetFactory(isSourceList ? NamedActionEntryWidget::new : OrderableNamedActionEntryWidget::new);
        listWidget.getEntrySelectionHandler().setAllowSelection(true);
        listWidget.getEntrySelectionHandler().setAllowMultiSelection(true);
        listWidget.getEntrySelectionHandler().setModifierKeyMultiSelection(true);

        if (isSourceList)
        {
            listWidget.addDefaultSearchBar();
            listWidget.setEntryFilterStringFactory(NamedAction::getSearchString);
        }

        return listWidget;
    }

    public static class OrderableNamedActionEntryWidget extends NamedActionEntryWidget
    {

        public OrderableNamedActionEntryWidget(int x, int y, int width, int height, int listIndex,
                                               int originalListIndex, @Nullable NamedAction data,
                                               @Nullable DataListWidget<NamedAction> listWidget)
        {
            super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

            this.canReOrder = true;
        }
    }
}
