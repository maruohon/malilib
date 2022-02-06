package fi.dy.masa.malilib.gui.widget.list.entry.action;

import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.action.ActionRegistry;
import fi.dy.masa.malilib.action.MacroAction;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.action.MacroActionEditScreen;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;

public class MacroActionEntryWidget extends ActionListBaseActionEntryWidget
{
    protected final GenericButton editButton;

    public MacroActionEntryWidget(int x, int y, int width, int height, int listIndex,
                                  int originalListIndex, @Nullable NamedAction data,
                                  @Nullable DataListWidget<NamedAction> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        this.editButton = GenericButton.simple(14, "malilib.label.edit", this::editMacro);

        this.setActionRemoveFunction(ActionRegistry::removeMacro);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();
        this.addWidget(this.editButton);
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        this.editButton.setRight(this.createAliasButton.getX() - 2);
        this.editButton.centerVerticallyInside(this);
    }

    protected void editMacro()
    {
        List<NamedAction> actions = ((MacroAction) this.data).getActionList();
        MacroActionEditScreen screen = new MacroActionEditScreen(this.data.getName(), actions, false);
        screen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(screen);
    }
}
