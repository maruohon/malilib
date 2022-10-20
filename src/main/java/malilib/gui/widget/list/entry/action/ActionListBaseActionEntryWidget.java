package malilib.gui.widget.list.entry.action;

import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import malilib.action.AliasAction;
import malilib.action.MacroAction;
import malilib.action.NamedAction;
import malilib.gui.BaseScreen;
import malilib.gui.TextInputScreen;
import malilib.gui.action.MacroActionEditScreen;
import malilib.gui.icon.DefaultIcons;
import malilib.gui.util.GuiUtils;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.entry.BaseOrderableListEditEntryWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.overlay.message.MessageDispatcher;
import malilib.registry.Registry;
import malilib.render.text.StyledTextLine;
import malilib.render.text.StyledTextUtils;
import malilib.util.data.LeftRight;

public class ActionListBaseActionEntryWidget extends BaseOrderableListEditEntryWidget<NamedAction>
{
    protected final GenericButton createAliasButton;
    protected final GenericButton editButton;
    protected final GenericButton removeActionButton;
    @Nullable protected BiConsumer<Integer, NamedAction> actionRemoveFunction;
    @Nullable protected BiConsumer<Integer, NamedAction> actionEditFunction;
    protected boolean addCreateAliasButton;
    protected boolean addEditButton;
    protected boolean addRemoveButton;
    protected boolean noRemoveButtons;
    protected int nextElementRight;

    public ActionListBaseActionEntryWidget(NamedAction data,
                                           DataListEntryWidgetData constructData)
    {
        super(data, constructData);

        this.canReOrder = false;
        this.useAddButton = false;
        this.useRemoveButton = false; // don't add the simple list remove button
        this.useMoveButtons = false;

        StyledTextLine nameText = data.getColoredWidgetDisplayName();
        this.setText(StyledTextUtils.clampStyledTextToMaxWidth(nameText, this.getWidth() - 20, LeftRight.RIGHT, " ..."));

        this.createAliasButton = GenericButton.create(14, "malilibdev.button.action_list_screen_widget.create_alias",
                                                      this::openAddAliasScreen);
        this.createAliasButton.translateAndAddHoverString("malilibdev.hover.button.create_alias_for_action");

        this.editButton = GenericButton.create(14, "malilibdev.button.misc.edit", this::editAction);

        this.removeActionButton = GenericButton.create(DefaultIcons.LIST_REMOVE_MINUS_13, this::removeAction);
        this.removeActionButton.translateAndAddHoverString("malilibdev.hover.button.list.remove");

        this.getBorderRenderer().getHoverSettings().setBorderWidthAndColor(1, 0xFFF0B000);
        this.getBackgroundRenderer().getHoverSettings().setEnabled(false);

        this.getHoverInfoFactory().setTextLineProvider("action_info", data::getHoverInfo);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        if (this.addCreateAliasButton)
        {
            this.addWidget(this.createAliasButton);
        }

        if (this.addEditButton)
        {
            this.addWidget(this.editButton);
        }

        if (this.addRemoveButton)
        {
            this.addWidget(this.removeActionButton);
        }
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        this.nextElementRight = this.getRight() - (this.noRemoveButtons ? 1 : 15);

        if (this.addRemoveButton)
        {
            this.removeActionButton.setRight(this.getRight() - 1);
            this.removeActionButton.centerVerticallyInside(this);
        }

        if (this.addCreateAliasButton)
        {
            this.createAliasButton.setRight(this.nextElementRight);
            this.createAliasButton.centerVerticallyInside(this);
            this.nextElementRight = this.createAliasButton.getX() - 2;
        }

        if (this.addEditButton)
        {
            this.editButton.setRight(this.nextElementRight);
            this.editButton.centerVerticallyInside(this);
            this.nextElementRight = this.editButton.getX() - 2;
        }
    }

    public void setAddCreateAliasButton(boolean addCreateAliasButton)
    {
        this.addCreateAliasButton = addCreateAliasButton;
    }

    public void setNoRemoveButtons()
    {
        this.noRemoveButtons = true;
    }

    public void setActionEditFunction(@Nullable BiConsumer<Integer, NamedAction> actionEditFunction)
    {
        this.actionEditFunction = actionEditFunction;
        this.addEditButton = true;
    }

    public void setActionRemoveFunction(@Nullable BiConsumer<Integer, NamedAction> actionRemoveFunction)
    {
        this.actionRemoveFunction = actionRemoveFunction;
        this.addRemoveButton = true;
    }

    public void setEditButtonHoverText(String translationKey)
    {
        this.editButton.getHoverInfoFactory().removeAll();
        this.editButton.translateAndAddHoverString(translationKey);
    }

    protected void editAction()
    {
        if (this.actionEditFunction != null)
        {
            this.actionEditFunction.accept(this.originalListIndex, this.data);
        }
    }

    protected void removeAction()
    {
        if (this.actionRemoveFunction != null)
        {
            this.actionRemoveFunction.accept(this.originalListIndex, this.data);
        }
    }

    protected void openAddAliasScreen()
    {
        TextInputScreen screen = new TextInputScreen("malilibdev.title.screen.create_alias_action", "", this::addAlias);
        screen.setParent(GuiUtils.getCurrentScreen());
        screen.setLabelText("malilibdev.label.actions.create_alias.alias_name");
        BaseScreen.openPopupScreen(screen);
    }

    protected boolean addAlias(String aliasName)
    {
        if (org.apache.commons.lang3.StringUtils.isBlank(aliasName))
        {
            return false;
        }

        AliasAction action = this.data.createAlias(aliasName);

        if (Registry.ACTION_REGISTRY.addAlias(action))
        {
            this.listWidget.refreshEntries();
            MessageDispatcher.success("malilibdev.message.info.added_alias_for_action",
                                      aliasName, this.data.getRegistryName());
            return true;
        }

        return false;
    }

    public static void openMacroEditScreen(NamedAction action, BaseScreen parent)
    {
        if (action instanceof MacroAction)
        {
            MacroActionEditScreen screen = new MacroActionEditScreen((MacroAction) action);
            screen.setParent(parent);
            BaseScreen.openScreen(screen);
        }
    }
}
