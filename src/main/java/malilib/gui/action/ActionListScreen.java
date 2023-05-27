package malilib.gui.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.google.common.collect.ImmutableList;

import malilib.MaLiLibConfigScreen;
import malilib.MaLiLibConfigs;
import malilib.MaLiLibReference;
import malilib.action.ActionGroup;
import malilib.action.AliasAction;
import malilib.action.MacroAction;
import malilib.action.NamedAction;
import malilib.action.ParameterizedNamedAction;
import malilib.gui.BaseScreen;
import malilib.gui.BaseTabbedScreen;
import malilib.gui.TextInputScreen;
import malilib.gui.widget.DropDownListWidget;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.DataListWidget;
import malilib.gui.widget.list.entry.action.ActionListBaseActionEntryWidget;
import malilib.overlay.message.MessageDispatcher;
import malilib.registry.Registry;
import malilib.util.StringUtils;
import malilib.util.data.AppendOverwrite;

public class ActionListScreen extends BaseActionListScreen
{
    protected final DropDownListWidget<ActionGroup> userAddedActionTypesDropdown;
    protected final GenericButton addMacroButton;
    protected final GenericButton executeActionButton;

    public ActionListScreen()
    {
        super(MaLiLibReference.MOD_ID, MaLiLibConfigScreen.ALL_TABS, MaLiLibConfigScreen.GENERIC);

        this.setTitle("malilib.title.screen.configs.action_list_screen", MaLiLibReference.MOD_VERSION);

        this.userAddedActionTypesDropdown = new DropDownListWidget<>(14, 10, ActionGroup.VALUES_USER_ADDED, ActionGroup::getDisplayName);
        this.userAddedActionTypesDropdown.setSelectedEntry(MaLiLibConfigs.Internal.ACTION_LIST_RIGHT_SIDE_DROPDOWN.getValue());
        this.userAddedActionTypesDropdown.setSelectionListener(this::onRightSideDropdownActionGroupSet);
        this.userAddedActionTypesDropdown.translateAndAddHoverString("malilib.hover.action.action_types_explanation");

        this.addMacroButton = GenericButton.create(14, "malilib.button.action_list_screen.create_macro", this::openMacroNameInput);
        this.addMacroButton.translateAndAddHoverString("malilib.hover.button.action_list_screen.create_macro");
        this.addMacroButton.setEnabledStatusSupplier(this::canCreateMacro);

        this.executeActionButton = GenericButton.create(14, "malilib.button.action_list_screen.execute_action", this::executeSelectedAction);
        this.executeActionButton.translateAndAddHoverString("malilib.hover.button.action_list_screen.execute_action");
        this.executeActionButton.setEnabledStatusSupplier(this::canExecuteAction);

        this.rightSideListWidget = this.createRightSideActionListWidget();

        this.createSwitchModConfigScreenDropDown(MaLiLibReference.MOD_INFO);
    }

    @Override
    protected void addActionListScreenWidgets()
    {
        super.addActionListScreenWidgets();

        this.addWidget(this.addMacroButton);
        this.addWidget(this.executeActionButton);
        this.addWidget(this.userAddedActionTypesDropdown);
        this.addListWidget(this.rightSideListWidget);
    }

    @Override
    protected void updateActionListScreenWidgetPositions(int x, int y, int w)
    {
        super.updateActionListScreenWidgetPositions(x, y, w);

        this.executeActionButton.setX(x);

        x = this.leftSideListWidget.getRight();
        this.addMacroButton.setRight(x);
        this.addMacroButton.setY(y);

        x += this.centerGap;
        y = this.leftSideListWidget.getY();
        int h = this.screenHeight - y;

        this.leftSideListWidget.setHeight(h - 22);
        this.executeActionButton.setY(this.leftSideListWidget.getBottom() + 2);

        this.userAddedActionTypesDropdown.setPosition(x, y - 16);
        this.rightSideListWidget.setPositionAndSize(x, y, w, h - 6);
    }

    @Override
    protected void saveChangesOnScreenClose()
    {
        Registry.ACTION_REGISTRY.saveToFileIfDirty();
    }

    protected ImmutableList<NamedAction> getRightSideActions()
    {
        return this.userAddedActionTypesDropdown.getSelectedEntry().getActions();
    }

    @Override
    protected DataListWidget<NamedAction> createRightSideActionListWidget()
    {
        DataListWidget<NamedAction> listWidget = this.createBaseActionListWidget(this::getRightSideActions, true);
        listWidget.addDefaultSearchBar();
        return listWidget;
    }

    protected boolean canCreateMacro()
    {
        return this.leftSideListWidget.getEntrySelectionHandler().getSelectedEntryCount() > 0;
    }

    protected boolean canExecuteAction()
    {
        return this.leftSideListWidget.getEntrySelectionHandler().getSelectedEntryCount() == 1;
    }

    protected void executeSelectedAction()
    {
        NamedAction action = this.leftSideListWidget.getEntrySelectionHandler().getLastSelectedEntry();

        if (action != null)
        {
            action.execute();
        }
    }

    protected void onRightSideDropdownActionGroupSet(ActionGroup group)
    {
        if (ActionGroup.VALUES_USER_ADDED.contains(group))
        {
            MaLiLibConfigs.Internal.ACTION_LIST_RIGHT_SIDE_DROPDOWN.setValue(group);
        }

        this.initScreen();
    }

    protected void openMacroNameInput()
    {
        String title = StringUtils.translate("malilib.title.screen.create_macro");
        TextInputScreen screen = new TextInputScreen(title, "", this::openMacroEditScreen);
        screen.setCloseScreenWhenApplied(false);
        screen.setLabelText("malilib.label.misc.name.colon");
        screen.setInfoText("malilib.info.action.action_name_immutable");
        screen.setParent(this);
        BaseScreen.openPopupScreen(screen);
    }

    protected boolean openMacroEditScreen(String macroName)
    {
        Collection<NamedAction> actions = this.leftSideListWidget.getSelectedEntries();
        MacroAction macro = new MacroAction(macroName, ImmutableList.copyOf(actions));

        if (Registry.ACTION_REGISTRY.addMacro(macro))
        {
            this.leftSideListWidget.getEntrySelectionHandler().clearSelection();
            ActionListBaseActionEntryWidget.openMacroEditScreen(macro, this);
            
            return true;
        }

        return false;
    }

    @Override
    protected void importEntries(List<NamedAction> list, AppendOverwrite mode)
    {
        ActionGroup group = this.userAddedActionTypesDropdown.getSelectedEntry();

        if (mode == AppendOverwrite.OVERWRITE)
        {
            this.removeActionsOfTypeForOverwrite(group);
        }

        int count = 0;

        for (NamedAction action : list)
        {
            if ((group == ActionGroup.ALIAS || group == ActionGroup.USER_ADDED)
                && action instanceof AliasAction
                && Registry.ACTION_REGISTRY.addAlias((AliasAction) action))
            {
                ++count;
                continue;
            }

            if ((group == ActionGroup.MACRO || group == ActionGroup.USER_ADDED)
                && action instanceof MacroAction
                && Registry.ACTION_REGISTRY.addMacro((MacroAction) action))
            {
                ++count;
                continue;
            }

            if ((group == ActionGroup.PARAMETERIZED || group == ActionGroup.USER_ADDED)
                && action instanceof ParameterizedNamedAction
                && Registry.ACTION_REGISTRY.addParameterizedAction((ParameterizedNamedAction) action))
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

    protected void removeActionsOfTypeForOverwrite(ActionGroup group)
    {
        // Create a copy of the list since we will be removing stuff
        ArrayList<NamedAction> allActions = new ArrayList<>(Registry.ACTION_REGISTRY.getAllActions());

        if (group == ActionGroup.ALIAS)
        {
            for (NamedAction action : allActions)
            {
                if (action instanceof AliasAction)
                {
                    Registry.ACTION_REGISTRY.removeAlias(action);
                }
            }
        }
        else if (group == ActionGroup.MACRO)
        {
            for (NamedAction action : allActions)
            {
                if (action instanceof MacroAction)
                {
                    Registry.ACTION_REGISTRY.removeMacro(action);
                }
            }
        }
        else if (group == ActionGroup.PARAMETERIZED)
        {
            for (NamedAction action : allActions)
            {
                if (action instanceof ParameterizedNamedAction)
                {
                    Registry.ACTION_REGISTRY.removeParameterizedAction(action);
                }
            }
        }
        else if (group == ActionGroup.USER_ADDED)
        {
            Registry.ACTION_REGISTRY.clearUserAddedActions();
        }
    }

    public static BaseTabbedScreen createActionListScreen()
    {
        return new ActionListScreen();
    }
}
