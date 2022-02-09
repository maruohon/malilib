package fi.dy.masa.malilib.gui.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import fi.dy.masa.malilib.action.ActionUtils;
import fi.dy.masa.malilib.action.MacroAction;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.action.ParameterizableNamedAction;
import fi.dy.masa.malilib.action.ParameterizedNamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.DualTextInputScreen;
import fi.dy.masa.malilib.gui.SettingsExportImportScreen;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseListEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.action.ActionListBaseActionEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.action.ParameterizableActionEntryWidget;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class MacroActionEditScreen extends BaseActionListScreen
{
    protected final ImmutableList<NamedAction> originalMacroActionsList;
    protected final List<NamedAction> macroActionsList;
    protected final LabelWidget macroActionsLabelWidget;
    protected final GenericButton addActionsButton;
    protected final GenericButton exportImportButton;
    protected final MacroAction macro;

    public MacroActionEditScreen(MacroAction macro)
    {
        super("", Collections.emptyList(), null);

        this.macro = macro;
        this.originalMacroActionsList = macro.getActionList();

        this.setTitle("malilib.gui.title.edit_macro");

        String label = StringUtils.translate("malilib.gui.label.macro_edit_screen.macro_actions", macro.getName());
        this.macroActionsLabelWidget = new LabelWidget(label);

        this.addActionsButton = GenericButton.simple(15, "malilib.label.button.macro_edit_screen.add_actions", this::addSelectedActions);
        this.addActionsButton.translateAndAddHoverString("malilib.hover_info.macro_edit_screen.add_actions");
        this.addActionsButton.setEnabledStatusSupplier(this::canAddActions);

        this.exportImportButton = GenericButton.simple(15, "malilib.label.button.export_slash_import", this::openExportImportScreen);

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
        this.addWidget(this.exportImportButton);
        this.addListWidget(this.rightSideListWidget);
    }

    @Override
    protected void updateActionListScreenWidgetPositions(int x, int y, int w)
    {
        super.updateActionListScreenWidgetPositions(x, y, w);

        this.addActionsButton.setY(y);
        this.addActionsButton.setRight(this.leftSideListWidget.getRight());

        x = this.leftSideListWidget.getRight() + this.centerGap;
        y = this.leftSideListWidget.getY();
        int h = this.screenHeight - y - 6;
        this.rightSideListWidget.setPositionAndSize(x, y, w, h);
        this.macroActionsLabelWidget.setPosition(x + 2, y - 10);
        this.exportImportButton.setY(y - 16);
        this.exportImportButton.setRight(this.rightSideListWidget.getRight());
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

    protected boolean canAddActions()
    {
        return this.leftSideListWidget.getEntrySelectionHandler().getSelectedEntries().isEmpty() == false;
    }

    protected void addSelectedActions()
    {
        Set<NamedAction> selectedActions = this.leftSideListWidget.getSelectedEntries();

        if (selectedActions.isEmpty() == false)
        {
            if (ActionUtils.containsMacroLoop(this.macro, selectedActions))
            {
                MessageDispatcher.error("malilib.message.error.action.macro_add_actions_loop_detected");
                return;
            }

            this.macroActionsList.addAll(selectedActions);
            this.rightSideListWidget.refreshEntries();
        }
    }

    protected boolean addAction(NamedAction action)
    {
        if (ActionUtils.containsMacroLoop(this.macro, Collections.singletonList(action)))
        {
            MessageDispatcher.error("malilib.message.error.action.macro_add_actions_loop_detected");
            return false;
        }

        this.macroActionsList.add(action);
        this.rightSideListWidget.refreshEntries();

        return true;
    }

    protected void removeAction(int originalListIndex, NamedAction action)
    {
        if (originalListIndex >= 0 && originalListIndex < this.macroActionsList.size())
        {
            this.macroActionsList.remove(originalListIndex);
        }

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

    protected void openParameterizedActionEditScreen(int originalListIndex)
    {
        if (originalListIndex >= 0 && originalListIndex < this.macroActionsList.size())
        {
            NamedAction action = this.macroActionsList.get(originalListIndex);

            if (action instanceof ParameterizedNamedAction)
            {
                ParameterizedNamedAction parAction = (ParameterizedNamedAction) action;
                DualTextInputScreen screen = ParameterizableActionEntryWidget.createParameterizationPrompt(
                        action.getName(), parAction.getArgument(),
                        (str1, str2) -> this.editParameterizedAction(originalListIndex, parAction, str1, str2));
                BaseScreen.openPopupScreen(screen);
            }
        }
    }

    protected boolean editParameterizedAction(int originalListIndex,
                                              ParameterizedNamedAction originalAction,
                                              String newName,
                                              String newArgument)
    {
        NamedAction newAction = originalAction.createCopy(newName, newArgument);
        return this.editParameterizedAction(originalListIndex, newAction);
    }

    protected boolean editParameterizedAction(int originalListIndex, NamedAction action)
    {
        if (originalListIndex >= 0 && originalListIndex < this.macroActionsList.size())
        {
            this.macroActionsList.set(originalListIndex, action);
            this.rightSideListWidget.refreshEntries();
            return true;
        }

        return false;
    }

    protected void openExportImportScreen()
    {
        String title = "malilib.title.screen.macro_edit.export_import";
        MacroAction macro = new MacroAction(this.macro.getName(), ImmutableList.copyOf(this.macroActionsList));
        String settingsStr = JsonUtils.jsonToString(macro.toJson(), false);
        SettingsExportImportScreen screen = new SettingsExportImportScreen(title, settingsStr, this::importOverwrite);
        screen.setAppendStringConsumer(this::importAppend);
        screen.setRadioWidgetHoverText("malilib.hover.macro_action_export_import_screen.append_overwrite");
        screen.setParent(this);
        BaseScreen.openPopupScreen(screen);
    }

    protected boolean importOverwrite(String settingsStr)
    {
        MacroAction macro = this.readMacroFromSettings(settingsStr);

        if (macro != null)
        {
            this.macroActionsList.clear();
            this.macroActionsList.addAll(macro.getActionList());
            this.rightSideListWidget.refreshEntries();
            return true;
        }

        MessageDispatcher.error("malilib.message.error.macro_import_from_string_failed");

        return false;
    }

    protected boolean importAppend(String settingsStr)
    {
        MacroAction macro = this.readMacroFromSettings(settingsStr);

        if (macro != null)
        {
            this.macroActionsList.addAll(macro.getActionList());
            this.rightSideListWidget.refreshEntries();
            return true;
        }

        MessageDispatcher.error("malilib.message.error.macro_import_from_string_failed");

        return false;
    }

    @Nullable
    protected MacroAction readMacroFromSettings(String settingsStr)
    {
        JsonElement el = JsonUtils.parseJsonFromString(settingsStr);

        if (el != null && el.isJsonObject())
        {
            return MacroAction.macroActionFromJson(el.getAsJsonObject());
        }

        return null;
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
            parWidget.setParameterizationButtonHoverText("malilib.hover.button.parameterize_action_for_macro");
            widget = parWidget;
        }
        else
        {
            widget = new ActionListBaseActionEntryWidget(
                    x, y, width, height, listIndex, originalListIndex, data, listWidget);
        }

        return widget;
    }

    protected BaseListEntryWidget createMacroSourceActionsWidget(int x, int y, int width, int height,
            int listIndex, int originalListIndex, NamedAction data, DataListWidget<NamedAction> listWidget)
    {
        ActionListBaseActionEntryWidget widget = this.createBaseMacroEditScreenActionWidget(
                x, y, width, height, listIndex, originalListIndex, data, listWidget);

        widget.setNoRemoveButtons();

        return widget;
    }

    protected BaseListEntryWidget createMacroMemberWidget(int x, int y, int width, int height,
            int listIndex, int originalListIndex, NamedAction data, DataListWidget<NamedAction> listWidget)
    {
        ActionListBaseActionEntryWidget widget = this.createBaseMacroEditScreenActionWidget(
                x, y, width, height, listIndex, originalListIndex, data, listWidget);

        if (data instanceof ParameterizedNamedAction)
        {
            widget.setActionEditFunction((i, a) -> this.openParameterizedActionEditScreen(i));
            widget.setEditButtonHoverText("malilib.hover.button.re_parameterize_action_for_macro");
        }
        else if (data instanceof MacroAction)
        {
            widget.setActionEditFunction((i, a) -> ActionListBaseActionEntryWidget.openMacroEditScreen(a, this));
        }

        widget.setCanReOrder(true);
        widget.setActionRemoveFunction(this::removeAction);

        return widget;
    }
}
