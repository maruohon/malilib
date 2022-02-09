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
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.StringUtils;

public class ActionListScreen extends BaseActionListScreen
{
    protected final DropDownListWidget<ActionGroup> userAddedActionTypesDropdown;
    protected final GenericButton addMacroButton;

    public ActionListScreen()
    {
        super(MaLiLibReference.MOD_ID, MaLiLibConfigScreen.ALL_TABS, MaLiLibConfigScreen.GENERIC);

        this.setTitle("malilib.gui.title.action_list_screen", MaLiLibReference.MOD_VERSION);

        this.userAddedActionTypesDropdown = new DropDownListWidget<>(-1, 14, 140, 10, ActionGroup.VALUES_USER_ADDED, ActionGroup::getDisplayName);
        this.userAddedActionTypesDropdown.setSelectedEntry(ActionGroup.USER_ADDED);
        this.userAddedActionTypesDropdown.setSelectionListener((t) -> this.initScreen());
        this.userAddedActionTypesDropdown.addHoverStrings("malilib.hover_info.action_types_explanation");

        this.addMacroButton = GenericButton.simple(14, "malilib.button.label.action_list_screen.create_macro", this::openMacroNameInput);
        this.addMacroButton.translateAndAddHoverString("malilib.gui.hover.action_list_screen.create_macro");
        this.addMacroButton.setEnabledStatusSupplier(this::canCreateMacro);

        this.rightSideListWidget = this.createRightSideActionListWidget();
    }

    @Override
    protected void addActionListScreenWidgets()
    {
        super.addActionListScreenWidgets();

        this.addWidget(this.addMacroButton);
        this.addWidget(this.userAddedActionTypesDropdown);
        this.addListWidget(this.rightSideListWidget);
    }

    @Override
    protected void updateActionListScreenWidgetPositions(int x, int y, int w)
    {
        super.updateActionListScreenWidgetPositions(x, y, w);

        this.addMacroButton.setY(y);
        this.addMacroButton.setRight(this.leftSideListWidget.getRight());

        x = this.leftSideListWidget.getRight() + this.centerGap;
        y = this.leftSideListWidget.getY();
        int h = this.screenHeight - y - 6;
        this.rightSideListWidget.setPositionAndSize(x, y, w, h);
        this.userAddedActionTypesDropdown.setPosition(x, y - 16);
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
        return this.createBaseActionListWidget(this::getRightSideActions);
    }

    protected boolean canCreateMacro()
    {
        return this.leftSideListWidget.getEntrySelectionHandler().getSelectedEntries().isEmpty() == false;
    }

    protected void openMacroNameInput()
    {
        String title = StringUtils.translate("malilib.gui.title.create_macro");
        TextInputScreen screen = new TextInputScreen(title, "", this::openMacroEditScreen, this);
        screen.setCloseScreenWhenApplied(false);
        screen.setLabelText("malilib.label.name.colon");
        screen.setInfoText("malilib.info.action.action_name_immutable");
        BaseScreen.openPopupScreen(screen);
    }

    protected boolean openMacroEditScreen(String macroName)
    {
        Set<NamedAction> actions = this.leftSideListWidget.getSelectedEntries();
        MacroAction macro = new MacroAction(macroName, ImmutableList.copyOf(actions));

        if (Registry.ACTION_REGISTRY.addMacro(macro) == false)
        {
            return false;
        }

        this.leftSideListWidget.getEntrySelectionHandler().clearSelection();
        MacroActionEditScreen screen = new MacroActionEditScreen(macro);
        screen.setParent(this);
        BaseScreen.openScreen(screen);

        return true;
    }

    public static BaseTabbedScreen createActionListScreen(@Nullable GuiScreen currentScreen)
    {
        return new ActionListScreen();
    }
}
