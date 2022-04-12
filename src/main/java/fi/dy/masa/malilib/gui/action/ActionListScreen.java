package fi.dy.masa.malilib.gui.action;

import java.util.Set;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.MaLiLibConfigScreen;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.action.ActionGroup;
import fi.dy.masa.malilib.action.MacroAction;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.BaseTabbedScreen;
import fi.dy.masa.malilib.gui.TextInputScreen;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.action.ActionListBaseActionEntryWidget;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.StringUtils;

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
        this.userAddedActionTypesDropdown.setSelectedEntry(ActionGroup.USER_ADDED);
        this.userAddedActionTypesDropdown.setSelectionListener((t) -> this.initScreen());
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
        return this.createBaseActionListWidget(this::getRightSideActions, true);
    }

    protected boolean canCreateMacro()
    {
        return this.leftSideListWidget.getEntrySelectionHandler().getSelectedEntries().isEmpty() == false;
    }

    protected boolean canExecuteAction()
    {
        return this.leftSideListWidget.getEntrySelectionHandler().getSelectedEntries().size() == 1;
    }

    protected void executeSelectedAction()
    {
        NamedAction action = this.leftSideListWidget.getEntrySelectionHandler().getLastSelectedEntry();

        if (action != null)
        {
            action.execute();
        }
    }

    protected void openMacroNameInput()
    {
        String title = StringUtils.translate("malilib.title.screen.create_macro");
        TextInputScreen screen = new TextInputScreen(title, "", this::openMacroEditScreen, this);
        screen.setCloseScreenWhenApplied(false);
        screen.setLabelText("malilib.label.misc.name.colon");
        screen.setInfoText("malilib.info.action.action_name_immutable");
        BaseScreen.openPopupScreen(screen);
    }

    protected boolean openMacroEditScreen(String macroName)
    {
        Set<NamedAction> actions = this.leftSideListWidget.getSelectedEntries();
        MacroAction macro = new MacroAction(macroName, ImmutableList.copyOf(actions));

        if (Registry.ACTION_REGISTRY.addMacro(macro))
        {
            this.leftSideListWidget.getEntrySelectionHandler().clearSelection();
            ActionListBaseActionEntryWidget.openMacroEditScreen(macro, this);
            
            return true;
        }

        return false;
    }

    public static BaseTabbedScreen createActionListScreen(@Nullable GuiScreen currentScreen)
    {
        return new ActionListScreen();
    }
}
