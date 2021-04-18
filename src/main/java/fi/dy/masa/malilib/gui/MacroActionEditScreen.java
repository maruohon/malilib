package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.action.ActionRegistry;
import fi.dy.masa.malilib.action.ActionRegistryImpl;
import fi.dy.masa.malilib.action.MacroAction;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.NamedActionEntryWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class MacroActionEditScreen extends BaseMultiListScreen
{
    protected final List<NamedAction> filteredSourceActions = new ArrayList<>();
    protected final ImmutableList<NamedAction> originalMacroActionsList;
    protected final List<NamedAction> macroActionsList = new ArrayList<>();
    protected final DataListWidget<NamedAction> actionSourceListWidget;
    protected final DataListWidget<NamedAction> macroActionsListWidget;
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
        this.macroActionsList.addAll(actions);
        this.originalName = name;
        this.creating = creating;

        this.title = StringUtils.translate("malilib.gui.title.edit_macro");

        this.macroNameLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.name.colon");
        this.allActionsLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.gui.label.action_list_screen.available_actions");
        this.macroActionsLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.gui.label.macro_edit_screen.macro_actions");

        this.nameTextFieldWidget = new BaseTextFieldWidget(0, 0, 200, 16, name);

        this.addActionsButton = GenericButton.createIconOnly(0, 0, DefaultIcons.LIST_ADD_PLUS_13);
        this.addActionsButton.setActionListener(this::addSelectedActions);
        this.addActionsButton.translateAndAddHoverString("malilib.hover_info.macro_edit_screen.add_actions");

        this.removeActionsButton = GenericButton.createIconOnly(0, 0, DefaultIcons.LIST_REMOVE_MINUS_13);
        this.removeActionsButton.setActionListener(this::removeSelectedActions);
        this.removeActionsButton.translateAndAddHoverString("malilib.hover_info.macro_edit_screen.remove_actions");

        this.actionSourceListWidget = this.createNamedActionListWidget(this::getFilteredActions);
        this.macroActionsListWidget = this.createNamedActionListWidget(this::getMacroActions);
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        this.addWidget(this.macroNameLabelWidget);
        this.addWidget(this.nameTextFieldWidget);

        this.addWidget(this.allActionsLabelWidget);
        this.addWidget(this.macroActionsLabelWidget);

        this.addWidget(this.addActionsButton);
        this.addWidget(this.removeActionsButton);

        this.addListWidget(this.actionSourceListWidget);
        this.addListWidget(this.macroActionsListWidget);

        int x = 10;
        int y = 20;

        this.macroNameLabelWidget.setPosition(x + 2, y + 4);
        this.nameTextFieldWidget.setPosition(this.macroNameLabelWidget.getRight() + 6, y);

        y += 22;
        int w = (this.screenWidth - 40) / 2;
        int h = this.screenHeight - y - 6;
        this.allActionsLabelWidget.setPosition(x + 2, y);
        this.actionSourceListWidget.setPositionAndSize(x, y + 10, w, h);

        x = this.actionSourceListWidget.getRight();
        this.addActionsButton.setPosition(x + 3, y + 10);
        this.removeActionsButton.setPosition(x + 3, y + 40);

        x += 19;
        this.macroActionsLabelWidget.setPosition(x + 2, y);
        this.macroActionsListWidget.setPositionAndSize(x, y + 10, w, h);

        this.updateFilteredSourceActionsList("");
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

    protected List<NamedAction> getMacroActions()
    {
        return this.macroActionsList;
    }

    protected List<NamedAction> getFilteredActions()
    {
        return this.filteredSourceActions;
    }

    protected boolean stringMatchesSearch(String searchTerm, String text)
    {
        return text.contains(searchTerm);
    }

    protected void updateFilteredSourceActionsList(String searchText)
    {
        this.filteredSourceActions.clear();

        if (org.apache.commons.lang3.StringUtils.isBlank(searchText))
        {
            this.filteredSourceActions.addAll(this.getActions());
        }
        else
        {
            searchText = searchText.toLowerCase(Locale.ROOT);

            for (NamedAction action : this.getActions())
            {
                if (this.stringMatchesSearch(searchText, action.getName().toLowerCase(Locale.ROOT)) ||
                            this.stringMatchesSearch(searchText, action.getDisplayName().toLowerCase(Locale.ROOT)))
                {
                    this.filteredSourceActions.add(action);
                }
            }
        }

        this.actionSourceListWidget.refreshEntries();
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

    protected DataListWidget<NamedAction> createNamedActionListWidget(Supplier<List<NamedAction>> listSupplier)
    {
        DataListWidget<NamedAction> listWidget = new DataListWidget<>(0, 0, 200, 200, listSupplier);
        listWidget.setListEntryWidgetFixedHeight(12);
        listWidget.setBorderWidth(1);
        listWidget.setFetchFromSupplierOnRefresh(true);
        listWidget.setEntryWidgetFactory(NamedActionEntryWidget::new);
        listWidget.getEntrySelectionHandler().setAllowSelection(true);
        listWidget.getEntrySelectionHandler().setAllowMultiSelection(true);
        listWidget.getEntrySelectionHandler().setModifierKeyMultiSelection(true);
        return listWidget;
    }
}
