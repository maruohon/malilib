package fi.dy.masa.malilib.gui.action;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.action.ActionContext;
import fi.dy.masa.malilib.action.ActionExecutionWidgetManager;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.position.EdgeInt;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.InfoIconWidget;
import fi.dy.masa.malilib.gui.widget.IntegerEditWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.MenuEntryWidget;
import fi.dy.masa.malilib.gui.widget.MenuWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.button.OnOffButton;
import fi.dy.masa.malilib.input.ActionResult;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.data.Vec2i;

public class ActionWidgetScreen extends BaseScreen
{
    protected final List<ActionExecutionWidget> widgetList = new ArrayList<>();
    protected final List<ActionExecutionWidget> selectedWidgets = new ArrayList<>();
    protected final GenericButton addWidgetButton;
    protected final GenericButton editModeButton;
    protected final GenericButton gridEnabledButton;
    protected final LabelWidget editModeLabel;
    protected final LabelWidget nameLabel;
    protected final LabelWidget gridLabel;
    protected final IntegerEditWidget gridEditWidget;
    protected final BaseTextFieldWidget nameTextField;
    protected final InfoIconWidget infoWidget;
    @Nullable protected MenuWidget menuWidget;
    protected final String name;
    protected Vec2i selectionStart = Vec2i.ZERO;
    protected boolean dirty;
    protected boolean editMode;
    protected boolean gridEnabled = true;
    protected boolean hasGroupSelection;
    protected boolean dragSelecting;
    protected int gridSize = 8;

    public ActionWidgetScreen(String name)
    {
        this.name = name;

        this.editModeLabel = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.edit_mode.colon");
        this.editModeButton = OnOffButton.simpleSlider(16, () -> this.editMode, this::toggleEditMode);

        this.infoWidget = new InfoIconWidget(0, 0, DefaultIcons.INFO_ICON_18, "");

        this.nameLabel = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.name.colon");
        this.nameTextField = new BaseTextFieldWidget(0, 0, 100, 16, name);

        this.addWidgetButton = new GenericButton(0, 0, -1, 16, "malilib.label.button.add_action");
        this.addWidgetButton.setActionListener(this::openAddWidgetScreen);

        this.gridLabel = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.grid.colon");
        this.gridEnabledButton = OnOffButton.simpleSlider(16, () -> this.gridEnabled, this::toggleGridEnabled);
        this.gridEditWidget = new IntegerEditWidget(0, 0, 40, 16, this.gridSize, 1, 256, this::setGridSize);

        ImmutableList<ActionExecutionWidget> list = ActionExecutionWidgetManager.INSTANCE.getWidgetList(this.name);

        if (list != null)
        {
            this.widgetList.addAll(list);

            for (ActionExecutionWidget widget : this.widgetList)
            {
                widget.setDirtyListener(this::markDirty);
            }
        }
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        int x = this.x + 10;
        int y = this.y + 2;

        this.editModeLabel.setPosition(x, y + 4);
        this.editModeButton.setPosition(this.editModeLabel.getRight() + 6, y);

        this.addWidget(this.editModeLabel);
        this.addWidget(this.editModeButton);

        if (this.editMode)
        {
            this.gridLabel.setPosition(this.editModeButton.getRight() + 16, y + 4);
            this.gridEnabledButton.setPosition(this.gridLabel.getRight() + 6, y);
            this.gridEditWidget.setPosition(this.gridEnabledButton.getRight() + 6, y);

            y += 20;
            this.nameLabel.setPosition(x, y + 4);
            this.nameTextField.setPosition(this.nameLabel.getRight() + 6, y);

            y += 20;
            this.addWidgetButton.setPosition(x, y);

            y += 20;
            this.infoWidget.setPosition(x, y);

            this.addWidget(this.nameLabel);
            this.addWidget(this.nameTextField);
            this.addWidget(this.addWidgetButton);
            this.addWidget(this.infoWidget);

            this.addWidget(this.gridLabel);
            this.addWidget(this.gridEnabledButton);

            if (this.gridEnabled)
            {
                this.addWidget(this.gridEditWidget);
            }
        }

        for (ActionExecutionWidget widget : this.widgetList)
        {
            this.addWidget(widget);
        }
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();

        for (ActionExecutionWidget widget : this.widgetList)
        {
            widget.setDirtyListener(null);
        }

        if (this.dirty)
        {
            ActionExecutionWidgetManager.INSTANCE.removeWidgetList(this.name);
            String newName = this.nameTextField.getText();
            ActionExecutionWidgetManager.INSTANCE.putWidgetList(newName, ImmutableList.copyOf(this.widgetList));
            ActionExecutionWidgetManager.INSTANCE.saveToFileIfDirty();
        }
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.editMode)
        {
            if (mouseButton == 0)
            {
                if (this.menuWidget != null)
                {
                    this.menuWidget.tryMouseClick(mouseX, mouseY, mouseButton);
                    this.closeMenu();

                    return true;
                }

                if (this.hasGroupSelection && this.leftClickWithGroupSelection(mouseX, mouseY))
                {
                    return true;
                }

                if (this.hoveredWidget == null)
                {
                    this.selectionStart = new Vec2i(mouseX, mouseY);
                    this.dragSelecting = true;
                    return true;
                }
            }
            else if (mouseButton == 1 && isShiftDown() == false)
            {
                if (this.hasGroupSelection)
                {
                    this.openGroupMenu(mouseX, mouseY);
                    return true;
                }
                else
                {
                    // Close any possible previous menu
                    this.closeMenu();

                    ActionExecutionWidget widget = this.getHoveredActionWidget(mouseX, mouseY);

                    if (widget != null)
                    {
                        this.openSingleWidgetMenu(mouseX, mouseY, widget);
                        return true;
                    }
                }
            }
        }

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    protected boolean leftClickWithGroupSelection(int mouseX, int mouseY)
    {
        boolean clickedWidget = this.getHoveredActionWidget(mouseX, mouseY) != null;

        if (clickedWidget)
        {
            for (ActionExecutionWidget widget : this.selectedWidgets)
            {
                widget.startDragging(mouseX, mouseY);
            }

            return true;
        }
        else if (isCtrlDown() == false && this.hoveredWidget == null)
        {
            this.clearSelectedWidgets();
        }

        return false;
    }

    @Override
    public boolean onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (this.dragSelecting)
        {
            int minX = Math.min(mouseX, this.selectionStart.x);
            int minY = Math.min(mouseY, this.selectionStart.y);
            int maxX = Math.max(mouseX, this.selectionStart.x);
            int maxY = Math.max(mouseY, this.selectionStart.y);
            EdgeInt selection = new EdgeInt(minY, maxX, maxY, minX);

            for (ActionExecutionWidget widget : this.widgetList)
            {
                if (widget.intersects(selection))
                {
                    widget.toggleSelected();
                    this.selectedWidgets.remove(widget);

                    if (widget.isSelected())
                    {
                        this.selectedWidgets.add(widget);
                    }
                }
            }

            this.dragSelecting = false;
            this.hasGroupSelection = this.selectedWidgets.isEmpty() == false;
        }

        return super.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseMoved(int mouseX, int mouseY)
    {
        return super.onMouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == Keyboard.KEY_DELETE)
        {
            this.deleteSelectedWidgets();
            return true;
        }

        return super.onKeyTyped(keyCode, scanCode, modifiers);
    }

    @Nullable
    protected ActionExecutionWidget getHoveredActionWidget(int mouseX, int mouseY)
    {
        for (ActionExecutionWidget widget : this.widgetList)
        {
            if (widget.isMouseOver(mouseX, mouseY))
            {
                return widget;
            }
        }

        return null;
    }

    protected void closeMenu()
    {
        this.removeWidget(this.menuWidget);
        this.menuWidget = null;
    }

    protected void openSingleWidgetMenu(int mouseX, int mouseY, ActionExecutionWidget widget)
    {
        this.menuWidget = new MenuWidget(mouseX + 4, mouseY, 10, 10);
        this.menuWidget.setZLevel(this.zLevel + 20);

        StyledTextLine textEdit = StyledTextLine.translate("malilib.label.edit");
        StyledTextLine textRemove = StyledTextLine.translate("malilib.label.delete.colored");
        this.menuWidget.setMenuEntries(new MenuEntryWidget(textEdit, () -> this.editActionWidget(widget)),
                                       new MenuEntryWidget(textRemove, () -> this.removeActionWidget(widget)));

        this.addWidget(this.menuWidget);
        this.menuWidget.updateSubWidgetsToGeometryChanges();
    }

    protected void openGroupMenu(int mouseX, int mouseY)
    {
        this.menuWidget = new MenuWidget(mouseX + 4, mouseY, 10, 10);
        this.menuWidget.setZLevel(this.zLevel + 20);

        StyledTextLine text = StyledTextLine.translate("malilib.label.delete_selected.colored");
        this.menuWidget.setMenuEntries(new MenuEntryWidget(text, this::deleteSelectedWidgets));

        this.addWidget(this.menuWidget);
        this.menuWidget.updateSubWidgetsToGeometryChanges();
    }

    protected void deleteSelectedWidgets()
    {
        if (this.selectedWidgets.isEmpty() == false)
        {
            this.widgetList.removeAll(this.selectedWidgets);

            for (ActionExecutionWidget widget : this.selectedWidgets)
            {
                this.removeWidget(widget);
            }

            this.selectedWidgets.clear();
            this.hasGroupSelection = false;
            this.markDirty();
        }
    }

    protected void openAddWidgetScreen()
    {
        AddActionExecutionWidgetScreen screen = new AddActionExecutionWidgetScreen(this::addActionWidget);
        screen.setParent(this);
        BaseScreen.openPopupScreen(screen);
    }

    protected void clearSelectedWidgets()
    {
        for (ActionExecutionWidget widget : this.selectedWidgets)
        {
            widget.setSelected(false);
        }

        this.selectedWidgets.clear();
        this.hasGroupSelection = false;
    }

    protected void toggleGridEnabled()
    {
        this.gridEnabled = ! this.gridEnabled;

        this.removeWidget(this.gridEditWidget);

        if (this.gridEnabled)
        {
            this.addWidget(this.gridEditWidget);
        }

        this.updateWidgetState();
    }

    protected void toggleEditMode()
    {
        this.editMode = ! this.editMode;
        this.updateWidgetState();
        this.initScreen();
    }

    protected void setGridSize(int gridSize)
    {
        this.gridSize = gridSize;
        this.updateWidgetState();
    }

    protected void updateWidgetState()
    {
        int gridSize = this.gridEnabled ? this.gridSize : -1;

        for (ActionExecutionWidget widget : this.widgetList)
        {
            widget.setGridSize(gridSize);
            widget.setEditMode(this.editMode);
        }
    }

    protected void addActionWidget(ActionExecutionWidget widget)
    {
        int gridSize = this.gridEnabled ? this.gridSize : -1;

        widget.setDirtyListener(this::markDirty);
        widget.setEditMode(this.editMode);
        widget.setGridSize(gridSize);
        widget.setPosition(this.x + this.screenWidth / 2, this.y + 10);

        this.widgetList.add(widget);
        this.addWidget(widget);
        this.markDirty();
    }

    protected void removeActionWidget(ActionExecutionWidget widget)
    {
        this.widgetList.remove(widget);
        this.selectedWidgets.remove(widget);
        this.removeWidget(widget);
        this.markDirty();
    }

    protected void editActionWidget(ActionExecutionWidget widget)
    {
    }

    protected void markDirty()
    {
        this.dirty = true;
    }

    @Override
    protected void drawContents(int mouseX, int mouseY, float partialTicks)
    {
        if (this.editMode)
        {
            if (this.gridEnabled)
            {
                int color = 0x30FFFFFF;

                ShapeRenderUtils.renderGrid(this.x, this.y, this.zLevel + 0.1f,
                                            this.screenWidth, this.screenHeight, this.gridSize, 1, color);
            }

            if (this.dragSelecting)
            {
                int minX = Math.min(mouseX, this.selectionStart.x);
                int minY = Math.min(mouseY, this.selectionStart.y);
                int maxX = Math.max(mouseX, this.selectionStart.x);
                int maxY = Math.max(mouseY, this.selectionStart.y);

                ShapeRenderUtils.renderOutlinedRectangle(minX, minY, this.zLevel + 0.5f,
                                                         maxX - minX, maxY - minY, 0x30FFFFFF, 0xFFFFFFFF);
            }
        }

        super.drawContents(mouseX, mouseY, partialTicks);
    }

    public static ActionResult openActionWidgetScreen(ActionContext ctx, String arg)
    {
        ActionWidgetScreen screen = new ActionWidgetScreen(arg);
        BaseScreen.openScreen(screen);
        return ActionResult.SUCCESS;
    }
}
