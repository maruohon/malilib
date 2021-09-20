package fi.dy.masa.malilib.gui.widget.list.entry;

import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.action.MacroAction;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.action.MacroActionEditScreen;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StyledTextUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public class MacroActionEntryWidget extends BaseDataListEntryWidget<MacroAction>
{
    protected final GenericButton editButton;
    protected final GenericButton removeButton;

    public MacroActionEntryWidget(int x, int y, int width, int height, int listIndex,
                                  int originalListIndex, @Nullable MacroAction data,
                                  @Nullable DataListWidget<? extends MacroAction> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        StyledTextLine nameText = data.getWidgetDisplayName();
        this.setText(StyledTextUtils.clampStyledTextToMaxWidth(nameText, width - 16, LeftRight.RIGHT, " ..."));

        this.editButton = GenericButton.simple(12, "malilib.label.edit", this::editMacro);

        this.removeButton = GenericButton.createIconOnly(DefaultIcons.LIST_REMOVE_MINUS_9, this::removeMacro);
        this.removeButton.translateAndAddHoverStrings("malilib.gui.button.hover.list.remove");

        this.getBorderRenderer().getHoverSettings().setBorderWidthAndColor(1, 0xFF00FF60);
        this.getBackgroundRenderer().getHoverSettings().setEnabled(false);

        this.getHoverInfoFactory().setTextLineProvider("action_info", data::getHoverInfo);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.editButton);
        this.addWidget(this.removeButton);
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int y = this.getY();
        this.removeButton.setPosition(this.getRight() - 12, y + 2);
        this.editButton.setPosition(this.removeButton.getX() - this.editButton.getWidth() - 4, y + 1);
    }

    protected void editMacro()
    {
        List<NamedAction> actions = this.data.getActionList();
        MacroActionEditScreen screen = new MacroActionEditScreen(this.data.getName(), actions, false);
        screen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(screen);
    }

    protected void removeMacro()
    {
        Registry.ACTION_REGISTRY.removeMacro(this.data.getRegistryName());
        this.listWidget.refreshEntries();
    }
}
