package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.MaLiLibConfigScreen;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.action.ActionRegistry;
import fi.dy.masa.malilib.action.ActionRegistryImpl;
import fi.dy.masa.malilib.action.AliasAction;
import fi.dy.masa.malilib.action.MacroAction;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.MacroActionEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.AliasActionEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.NamedActionEntryWidget;
import fi.dy.masa.malilib.overlay.message.MessageUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class ActionListScreen extends BaseMultiListScreen
{
    protected final List<NamedAction> filteredActions = new ArrayList<>();
    protected final DataListWidget<NamedAction> actionSourceListWidget;
    protected final DataListWidget<MacroAction> macroListWidget;
    protected final DataListWidget<AliasAction> aliasListWidget;
    protected final GenericButton addAliasButton;
    protected final GenericButton addMacroButton;
    protected final LabelWidget baseActionsLabelWidget;
    protected final LabelWidget aliasesLabelWidget;
    protected final LabelWidget macrosLabelWidget;

    public ActionListScreen()
    {
        super(MaLiLibReference.MOD_ID, MaLiLibConfigScreen.ALL_TABS, MaLiLibConfigScreen.GENERIC);

        this.title = StringUtils.translate("malilib.gui.title.action_aliases_and_macros");

        this.baseActionsLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.gui.label.action_list_screen.available_actions");
        this.aliasesLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.gui.label.action_list_screen.aliases");
        this.macrosLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.gui.label.action_list_screen.macros");

        this.addAliasButton = new GenericButton(10, 48, -1, 16, "malilib.gui.button.actions.add_alias");
        this.addAliasButton.setActionListener(this::openAddAliasScreen);

        int x = this.addAliasButton.getRight() + 6;
        this.addMacroButton = new GenericButton(x, 48, -1, 16, "malilib.gui.button.actions.add_macro");
        this.addMacroButton.setActionListener(this::openCreateMacroScreen);
        this.addMacroButton.translateAndAddHoverString("malilib.gui.hover.action_list_screen.create_macro");

        this.actionSourceListWidget = this.createNamedActionListWidget();
        this.aliasListWidget = this.createAliasListWidget();
        this.macroListWidget = this.createMacroListWidget();
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        this.addWidget(this.baseActionsLabelWidget);
        this.addWidget(this.aliasesLabelWidget);
        this.addWidget(this.macrosLabelWidget);

        this.addWidget(this.addAliasButton);
        this.addWidget(this.addMacroButton);

        this.addListWidget(this.actionSourceListWidget);
        this.addListWidget(this.aliasListWidget);
        this.addListWidget(this.macroListWidget);

        int x = 10;
        int y = this.addAliasButton.getBottom() + 15;
        int w = (this.screenWidth - 30) / 2;
        int h = this.screenHeight - y - 6;
        this.actionSourceListWidget.setPositionAndSize(x, y, w, h);
        this.baseActionsLabelWidget.setPosition(x + 2, y - 10);

        h = (h - 16) / 2;
        x = this.actionSourceListWidget.getRight() + 10;
        this.aliasListWidget.setPositionAndSize(x, y, w, h);
        this.aliasesLabelWidget.setPosition(x + 2, y - 10);

        y = this.aliasListWidget.getBottom() + 16;
        this.macroListWidget.setPositionAndSize(x, y, w, h);
        this.macrosLabelWidget.setPosition(x + 2, y - 10);

        this.updateFilteredSourceActionsList("");
    }

    @Override
    public void onGuiClosed()
    {
        ((ActionRegistryImpl) ActionRegistry.INSTANCE).saveToFileIfDirty();
        super.onGuiClosed();
    }

    protected List<NamedAction> getActions()
    {
        return ActionRegistry.INSTANCE.getAllActions();
    }

    protected List<MacroAction> getMacros()
    {
        return ActionRegistry.INSTANCE.getMacros();
    }

    protected List<AliasAction> getAliases()
    {
        return ActionRegistry.INSTANCE.getAliases();
    }

    protected List<NamedAction> getFilteredActions()
    {
        return this.filteredActions;
    }

    protected boolean stringMatchesSearch(String searchTerm, String text)
    {
        return text.contains(searchTerm);
    }

    protected void updateFilteredSourceActionsList(String searchText)
    {
        this.filteredActions.clear();

        if (org.apache.commons.lang3.StringUtils.isBlank(searchText))
        {
            this.filteredActions.addAll(this.getActions());
        }
        else
        {
            searchText = searchText.toLowerCase(Locale.ROOT);

            for (NamedAction action : this.getActions())
            {
                if (this.stringMatchesSearch(searchText, action.getName().toLowerCase(Locale.ROOT)) ||
                    this.stringMatchesSearch(searchText, action.getDisplayName().toLowerCase(Locale.ROOT)))
                {
                    this.filteredActions.add(action);
                }
            }
        }

        this.actionSourceListWidget.refreshEntries();
    }

    protected DataListWidget<NamedAction> createNamedActionListWidget()
    {
        DataListWidget<NamedAction> listWidget = new DataListWidget<>(0, 0, 120, 120, this::getFilteredActions);
        listWidget.setListEntryWidgetFixedHeight(12);
        listWidget.setBorderWidth(1);
        listWidget.setFetchFromSupplierOnRefresh(true);
        listWidget.setEntryWidgetFactory(NamedActionEntryWidget::new);
        listWidget.getEntrySelectionHandler().setAllowSelection(true);
        listWidget.getEntrySelectionHandler().setAllowMultiSelection(true);
        listWidget.getEntrySelectionHandler().setModifierKeyMultiSelection(true);
        return listWidget;
    }

    protected DataListWidget<AliasAction> createAliasListWidget()
    {
        DataListWidget<AliasAction> listWidget = new DataListWidget<>(0, 0, 120, 120, this::getAliases);
        listWidget.setListEntryWidgetFixedHeight(13);
        listWidget.setBorderWidth(1);
        listWidget.setFetchFromSupplierOnRefresh(true);
        listWidget.setEntryWidgetFactory(AliasActionEntryWidget::new);
        return listWidget;
    }

    protected DataListWidget<MacroAction> createMacroListWidget()
    {
        DataListWidget<MacroAction> listWidget = new DataListWidget<>(0, 0, 120, 120, this::getMacros);
        listWidget.setListEntryWidgetFixedHeight(14);
        listWidget.setBorderWidth(1);
        listWidget.setFetchFromSupplierOnRefresh(true);
        listWidget.setEntryWidgetFactory(MacroActionEntryWidget::new);
        return listWidget;
    }

    protected void openAddAliasScreen()
    {
        NamedAction action = this.actionSourceListWidget.getLastSelectedEntry();

        if (action != null && this.actionSourceListWidget.getSelectedEntries().size() == 1)
        {
            String title = StringUtils.translate("malilib.gui.title.add_action_alias", action.getRegistryName());
            BaseScreen.openPopupScreen(new TextInputScreen(title, "", this, this::addAlias));
        }
        else
        {
            MessageUtils.error("malilib.message.error.actions_edit.add_alias_select_one");
        }
    }

    protected void openCreateMacroScreen()
    {
        String title = StringUtils.translate("malilib.gui.title.create_macro");
        BaseScreen.openPopupScreen(new TextInputScreen(title, "", this, this::openCreateMacroScreen));
    }

    protected boolean openCreateMacroScreen(String macroName)
    {
        if (ActionRegistry.INSTANCE.getAction(macroName) != null)
        {
            MessageUtils.error("malilib.message.error.actions_edit.exists", macroName);
            return false;
        }

        Set<NamedAction> actions = this.actionSourceListWidget.getSelectedEntries();
        MacroActionEditScreen screen = new MacroActionEditScreen(macroName, actions, true);
        screen.setParent(this);
        BaseScreen.openScreen(screen);
        return false; // return false so that the text input screen does not close the screen...
    }

    protected boolean addAlias(String alias)
    {
        if (org.apache.commons.lang3.StringUtils.isBlank(alias))
        {
            return false;
        }

        if (ActionRegistry.INSTANCE.getAction(alias) != null)
        {
            MessageUtils.error("malilib.message.error.actions_edit.exists", alias);
            return false;
        }

        NamedAction action = this.actionSourceListWidget.getLastSelectedEntry();

        if (action != null && ActionRegistry.INSTANCE.addAlias(new AliasAction(alias, action)))
        {
            this.aliasListWidget.refreshEntries();
            MessageUtils.success("malilib.message.success.added_alias_for_action", alias, action.getRegistryName());
            return true;
        }

        return false;
    }

    public static BaseTabbedScreen createActionListScreen(@Nullable GuiScreen currentScreen)
    {
        return new ActionListScreen();
    }
}
