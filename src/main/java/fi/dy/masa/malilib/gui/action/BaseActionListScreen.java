package fi.dy.masa.malilib.gui.action;

import java.util.List;
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
import fi.dy.masa.malilib.gui.ScreenTab;
import fi.dy.masa.malilib.gui.widget.BaseWidget;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.action.ActionListBaseActionEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.action.MacroActionEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.action.ParameterizableActionEntryWidget;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.render.text.StyledTextLine;

public abstract class BaseActionListScreen extends BaseMultiListScreen
{
    protected final DropDownListWidget<ActionGroup> allActionTypesDropdown;
    protected final DataListWidget<NamedAction> leftSideListWidget;
    protected int centerGap = 10;

    public BaseActionListScreen(String screenId, List<? extends ScreenTab> screenTabs, @Nullable ScreenTab defaultTab)
    {
        super(screenId, screenTabs, defaultTab);

        this.allActionTypesDropdown = new DropDownListWidget<>(-1, 14, 140, 10, ActionGroup.VALUES, ActionGroup::getDisplayName);
        this.allActionTypesDropdown.setSelectedEntry(ActionGroup.ALL);
        this.allActionTypesDropdown.setSelectionListener((t) -> this.initScreen());
        this.allActionTypesDropdown.addHoverStrings("malilib.hover_info.action_types_explanation");

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
        listWidget.setEntryWidgetFactory(BaseActionListScreen::createEntryWidget);
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

    protected abstract DataListWidget<NamedAction> createRightSideActionListWidget();

    public static ActionListBaseActionEntryWidget createEntryWidget(int x, int y, int width, int height,
                                                                    int listIndex, int originalListIndex,
                                                                    NamedAction data,
                                                                    DataListWidget<NamedAction> listWidget)
    {
        if (data instanceof AliasAction)
        {
            ActionListBaseActionEntryWidget widget = new ActionListBaseActionEntryWidget(
                    x, y, width, height, listIndex, originalListIndex, data, listWidget);
            widget.setActionRemoveFunction(BaseActionListScreen::removeAliasFromRegistry);
            setWidgetStartingStyleFrom(widget, "malilib.style.action_list_screen.widget.alias");
            return widget;
        }
        else if (data instanceof ParameterizedNamedAction)
        {
            ActionListBaseActionEntryWidget widget = new ActionListBaseActionEntryWidget(
                    x, y, width, height, listIndex, originalListIndex, data, listWidget);
            widget.setActionRemoveFunction(BaseActionListScreen::removeParameterizedActionFromRegistry);
            setWidgetStartingStyleFrom(widget, "malilib.style.action_list_screen.widget.parameterized");
            return widget;
        }
        else if (data instanceof ParameterizableNamedAction)
        {
            ParameterizableActionEntryWidget widget = new ParameterizableActionEntryWidget(
                    x, y, width, height, listIndex, originalListIndex, data, listWidget);
            widget.setParameterizationButtonHoverText("malilib.button.hover.parameterize_action");
            setWidgetStartingStyleFrom(widget, "malilib.style.action_list_screen.widget.parameterizable");
            return widget;
        }
        else if (data instanceof MacroAction)
        {
            ActionListBaseActionEntryWidget widget = new MacroActionEntryWidget(
                    x, y, width, height, listIndex, originalListIndex, data, listWidget);
            widget.setActionRemoveFunction(BaseActionListScreen::removeMacroFromRegistry);
            setWidgetStartingStyleFrom(widget, "malilib.style.action_list_screen.widget.macro");
            return widget;
        }

        return new ActionListBaseActionEntryWidget(x, y, width, height, listIndex, originalListIndex, data, listWidget);
    }

    protected static void removeAliasFromRegistry(NamedAction action)
    {
        String regName = action.getRegistryName();

        if (regName != null)
        {
            Registry.ACTION_REGISTRY.removeAlias(regName);
        }
    }

    protected static void removeMacroFromRegistry(NamedAction action)
    {
        String regName = action.getRegistryName();

        if (regName != null)
        {
            Registry.ACTION_REGISTRY.removeMacro(regName);
        }
    }

    protected static void removeParameterizedActionFromRegistry(NamedAction action)
    {
        String regName = action.getRegistryName();

        if (regName != null)
        {
            Registry.ACTION_REGISTRY.removeParameterizedAction(regName);
        }
    }

    protected static void setWidgetStartingStyleFrom(BaseWidget widget, String translationKey)
    {
        widget.setStartingStyleForText(StyledTextLine.translate(translationKey).getLastStyle());
    }
}
