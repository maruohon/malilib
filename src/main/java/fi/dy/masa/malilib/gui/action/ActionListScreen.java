package fi.dy.masa.malilib.gui.action;

import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.MaLiLibConfigScreen;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.action.ActionRegistry;
import fi.dy.masa.malilib.action.ActionType;
import fi.dy.masa.malilib.action.AliasAction;
import fi.dy.masa.malilib.action.MacroAction;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.action.ParameterizableNamedAction;
import fi.dy.masa.malilib.action.ParameterizedNamedAction;
import fi.dy.masa.malilib.gui.BaseMultiListScreen;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.BaseTabbedScreen;
import fi.dy.masa.malilib.gui.TextInputScreen;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.action.ActionListBaseActionEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.action.MacroActionEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.action.ParameterizableActionEntryWidget;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.render.text.StyledText;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StringUtils;

public class ActionListScreen extends BaseMultiListScreen
{
    protected final DropDownListWidget<ActionType> allActionTypesDropdown;
    protected final DropDownListWidget<ActionType> userAddedActionTypesDropdown;
    protected final DataListWidget<NamedAction> leftSideListWidget;
    protected final DataListWidget<NamedAction> rightSideListWidget;
    protected final GenericButton addMacroButton;

    public ActionListScreen()
    {
        super(MaLiLibReference.MOD_ID, MaLiLibConfigScreen.ALL_TABS, MaLiLibConfigScreen.GENERIC);

        this.setTitle("malilib.gui.title.action_list_screen", MaLiLibReference.MOD_VERSION);

        this.allActionTypesDropdown = new DropDownListWidget<>(-1, 14, 140, 10, ActionType.VALUES, ActionType::getDisplayName);
        this.allActionTypesDropdown.setSelectedEntry(ActionType.ALL);
        this.allActionTypesDropdown.setSelectionListener((t) -> this.initScreen());
        this.allActionTypesDropdown.addHoverStrings("malilib.hover_info.action_types_explanation");

        this.userAddedActionTypesDropdown = new DropDownListWidget<>(-1, 14, 140, 10, ActionType.VALUES_USER_ADDED, ActionType::getDisplayName);
        this.userAddedActionTypesDropdown.setSelectedEntry(ActionType.USER_ADDED);
        this.userAddedActionTypesDropdown.setSelectionListener((t) -> this.initScreen());
        this.userAddedActionTypesDropdown.addHoverStrings("malilib.hover_info.action_types_explanation");

        this.addMacroButton = GenericButton.simple(14, "malilib.button.label.action_list_screen.create_macro", this::openCreateMacroScreenNameInput);
        this.addMacroButton.translateAndAddHoverString("malilib.gui.hover.action_list_screen.create_macro");
        this.addMacroButton.setEnabledStatusSupplier(this::canCreateMacro);

        this.leftSideListWidget = this.createLeftSideActionListWidget();
        this.rightSideListWidget = this.createRightSideActionListWidget();
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        this.leftSideListWidget.refreshEntries();
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.addMacroButton);

        this.addWidget(this.allActionTypesDropdown);
        this.addWidget(this.userAddedActionTypesDropdown);

        this.addListWidget(this.leftSideListWidget);
        this.addListWidget(this.rightSideListWidget);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = 10;
        int y = this.y + 44;
        int w = (this.screenWidth - 30) / 2;

        this.allActionTypesDropdown.setPosition(x, y);
        this.addMacroButton.setY(y);
        this.addMacroButton.setRight(x + w);
        y += 16;

        int h = this.screenHeight - y - 6;
        this.leftSideListWidget.setPositionAndSize(x, y, w, h);

        x = this.leftSideListWidget.getRight() + 10;
        this.userAddedActionTypesDropdown.setPosition(x, y - 16);
        this.rightSideListWidget.setPositionAndSize(x, y, w, h);
    }

    @Override
    public void onGuiClosed()
    {
        Registry.ACTION_REGISTRY.saveToFileIfDirty();
        super.onGuiClosed();
    }

    protected boolean canCreateMacro()
    {
        return this.leftSideListWidget.getEntrySelectionHandler().getSelectedEntries().isEmpty() == false;
    }

    protected List<NamedAction> getLeftSideActions()
    {
        return this.allActionTypesDropdown.getSelectedEntry().getActions();
    }

    protected List<NamedAction> getRightSideActions()
    {
        return this.userAddedActionTypesDropdown.getSelectedEntry().getActions();
    }

    protected DataListWidget<NamedAction> createBaseActionListWidget()
    {
        DataListWidget<NamedAction> listWidget = new DataListWidget<>(0, 0, 120, 120, this::getLeftSideActions);

        listWidget.setListEntryWidgetFixedHeight(14);
        listWidget.getBorderRenderer().getNormalSettings().setBorderWidth(1);
        listWidget.setFetchFromSupplierOnRefresh(true);
        listWidget.setEntryWidgetFactory(ActionListScreen::createEntryWidget);
        listWidget.addDefaultSearchBar();
        listWidget.setEntryFilterStringFactory(NamedAction::getSearchString);

        return listWidget;
    }

    protected DataListWidget<NamedAction> createLeftSideActionListWidget()
    {
        DataListWidget<NamedAction> listWidget = this.createBaseActionListWidget();
        listWidget.getEntrySelectionHandler()
                .setAllowSelection(true)
                .setAllowMultiSelection(true)
                .setModifierKeyMultiSelection(true);
        return listWidget;
    }

    protected DataListWidget<NamedAction> createRightSideActionListWidget()
    {
        return this.createBaseActionListWidget();
    }

    public static ActionListBaseActionEntryWidget createEntryWidget(int x, int y, int width, int height,
                                                                    int listIndex, int originalListIndex,
                                                                    NamedAction data,
                                                                    DataListWidget<NamedAction> listWidget)
    {
        if (data instanceof AliasAction)
        {
            ActionListBaseActionEntryWidget widget = new ActionListBaseActionEntryWidget(
                    x, y, width, height, listIndex, originalListIndex, data, listWidget);
            widget.setActionRemoveFunction(ActionRegistry::removeAlias);
            setWidgetStartingStyleFrom(widget, "malilib.style.action_list_screen.widget.alias");
            return widget;
        }
        else if (data instanceof ParameterizedNamedAction)
        {
            ActionListBaseActionEntryWidget widget = new ActionListBaseActionEntryWidget(
                    x, y, width, height, listIndex, originalListIndex, data, listWidget);
            widget.setActionRemoveFunction(ActionRegistry::removeParameterizedAction);
            setWidgetStartingStyleFrom(widget, "malilib.style.action_list_screen.widget.parameterized");
            return widget;
        }
        else if (data instanceof ParameterizableNamedAction)
        {
            ActionListBaseActionEntryWidget widget = new ParameterizableActionEntryWidget(
                    x, y, width, height, listIndex, originalListIndex, data, listWidget);
            setWidgetStartingStyleFrom(widget, "malilib.style.action_list_screen.widget.parameterizable");
            return widget;
        }
        else if (data instanceof MacroAction)
        {
            ActionListBaseActionEntryWidget widget = new MacroActionEntryWidget(
                    x, y, width, height, listIndex, originalListIndex, data, listWidget);
            setWidgetStartingStyleFrom(widget, "malilib.style.action_list_screen.widget.macro");
            return widget;
        }

        return new ActionListBaseActionEntryWidget(x, y, width, height, listIndex, originalListIndex, data, listWidget);
    }

    protected void openCreateMacroScreenNameInput()
    {
        String title = StringUtils.translate("malilib.gui.title.create_macro");
        TextInputScreen screen = new TextInputScreen(title, "", this::openCreateMacroScreen, this);
        screen.setCloseScreenWhenApplied(false);
        screen.setLabelText(StyledText.translate("malilib.label.name.colon"));
        BaseScreen.openPopupScreen(screen);
    }

    protected boolean openCreateMacroScreen(String macroName)
    {
        if (Registry.ACTION_REGISTRY.getAction(macroName) != null)
        {
            MessageDispatcher.error("malilib.message.error.action.action_name_exists", macroName);
            return false;
        }

        Set<NamedAction> actions = this.leftSideListWidget.getSelectedEntries();
        MacroActionEditScreen screen = new MacroActionEditScreen(macroName, actions, true);
        screen.setParent(this);
        BaseScreen.openScreen(screen);

        return true;
    }

    protected static void setWidgetStartingStyleFrom(BaseWidget widget, String translationKey)
    {
        widget.setStartingStyleForText(StyledTextLine.translate(translationKey).getLastStyle());
    }

    public static BaseTabbedScreen createActionListScreen(@Nullable GuiScreen currentScreen)
    {
        return new ActionListScreen();
    }
}
