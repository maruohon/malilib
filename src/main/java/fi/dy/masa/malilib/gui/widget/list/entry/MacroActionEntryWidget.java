package fi.dy.masa.malilib.gui.widget.list.entry;

import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.action.ActionRegistry;
import fi.dy.masa.malilib.action.MacroAction;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.MacroActionEditScreen;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.ScreenContext;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StyledTextUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public class MacroActionEntryWidget extends BaseDataListEntryWidget<MacroAction>
{
    protected final StyledTextLine nameText;
    protected final GenericButton editButton;
    protected final GenericButton removeButton;

    public MacroActionEntryWidget(int x, int y, int width, int height, int listIndex,
                                  int originalListIndex, @Nullable MacroAction data,
                                  @Nullable DataListWidget<? extends MacroAction> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        this.editButton = new GenericButton(0, 0, -1, 12, "malilib.label.edit");
        this.editButton.setActionListener(this::editMacro);

        this.removeButton = GenericButton.createIconOnly(0, 0, DefaultIcons.LIST_REMOVE_MINUS_9);
        this.removeButton.translateAndAddHoverStrings("malilib.gui.button.hover.list.remove");
        this.removeButton.setActionListener(this::removeMacro);

        this.renderHoverBackground = false;
        this.setHoveredBorderWidth(1);
        this.setHoveredBorderColor(0xFF00FF60);

        StyledTextLine fullName = StyledTextLine.of(data.getWidgetDisplayName());
        this.nameText = StyledTextUtils.clampStyledTextToMaxWidth(fullName, width - 16, LeftRight.RIGHT, " ...");

        this.getHoverInfoFactory().setStringListProvider("action_info", data::getHoverInfo);
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
        ActionRegistry.INSTANCE.removeMacro(this.data.getRegistryName());
        this.listWidget.refreshEntries();
    }

    @Override
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        super.renderAt(x, y, z, ctx);

        int ty = y + this.getHeight() / 2 - this.fontHeight / 2;
        this.renderTextLine(x + 4, ty, z + 0.1f, 0xFFFFFFFF, true, ctx, this.nameText);
    }
}
