package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.widget.button.BaseButton;
import fi.dy.masa.malilib.gui.widget.button.ButtonActionListener;
import fi.dy.masa.malilib.listener.TextChangeListener;
import fi.dy.masa.malilib.render.RenderUtils;

public abstract class ContainerWidget extends BackgroundWidget
{
    protected final List<BaseWidget> subWidgets = new ArrayList<>();
    protected final List<Runnable> tasks = new ArrayList<>();

    public ContainerWidget(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    /**
     * This method should be overridden to add any widgets from
     * (final) fields to the ContainerWidget lists.
     * This should be called whenever the collection of (active)
     * sub widgets should change for whatever reason (such as a widget
     * becoming active or inactive and maybe covering other widgets).
     */
    public void reAddSubWidgets()
    {
        this.clearWidgets();
    }

    @Override
    protected void onPositionChanged(int oldX, int oldY)
    {
        int diffX = this.getX() - oldX;
        int diffY = this.getY() - oldY;

        if (diffX != 0 || diffY != 0)
        {
            this.moveSubWidgets(diffX, diffY);
        }
    }

    @Override
    protected void onSizeChanged()
    {
        this.updateSubWidgetsToGeometryChanges();
    }

    @Override
    protected void onPositionOrSizeChanged(int oldX, int oldY)
    {
        int diffX = this.getX() - oldX;
        int diffY = this.getY() - oldY;

        if (diffX != 0 || diffY != 0)
        {
            this.moveSubWidgets(diffX, diffY);
        }
        else
        {
            this.updateSubWidgetsToGeometryChanges();
        }
    }

    /**
     * Moves all the sub widgets by the specified amount.
     * Used for example when the window is resized or maybe some
     * widgets are dragged around.
     * @param diffX
     * @param diffY
     */
    public void moveSubWidgets(int diffX, int diffY)
    {
        for (BaseWidget widget : this.subWidgets)
        {
            widget.setPosition(widget.getX() + diffX, widget.getY() + diffY);
        }
    }

    /**
     *
     * This method should be overridden to update any sub widget
     * positions to the current position of this container widget,
     * or to any other changes such as width or height changes.
     */
    public void updateSubWidgetsToGeometryChanges()
    {
        for (BaseWidget widget : this.subWidgets)
        {
            widget.onContainerGeometryChanged();
        }
    }

    public <T extends BaseWidget> T addWidgetIfNotNull(@Nullable T widget)
    {
        if (widget != null)
        {
            this.addWidget(widget);
        }

        return widget;
    }

    private void addTask(Runnable task)
    {
        this.tasks.add(task);
    }

    protected void runTasks()
    {
        if (this.tasks.isEmpty() == false)
        {
            for (Runnable task : this.tasks)
            {
                task.run();
            }

            this.tasks.clear();
        }
    }

    public <T extends BaseWidget> T addWidget(T widget)
    {
        this.subWidgets.add(widget);
        this.onSubWidgetAdded(widget);

        return widget;
    }

    public <T extends BaseButton> T addButton(T button, ButtonActionListener listener)
    {
        button.setActionListener(listener);
        this.addWidget(button);
        return button;
    }

    public <T extends BaseTextFieldWidget> T addTextField(T widget, TextChangeListener listener)
    {
        widget.setListener(listener);
        this.addWidget(widget);
        return widget;
    }

    public LabelWidget addLabel(int x, int y, int textColor, String... lines)
    {
        return this.addLabel(x, y, -1, -1, textColor, lines);
    }

    public LabelWidget addLabel(int x, int y, int width, int height, int textColor, String... lines)
    {
        return this.addWidget(new LabelWidget(x, y, width, height, textColor, lines));
    }

    public void removeWidget(BaseWidget widget)
    {
        this.subWidgets.remove(widget);
    }

    public void clearWidgets()
    {
        this.subWidgets.clear();
    }

    public void onSubWidgetAdded(BaseWidget widget)
    {
        widget.setTaskQueue(this::addTask);
        widget.onWidgetAdded(this.getZLevel());
    }

    @Override
    public void onWidgetAdded(float parentZLevel)
    {
        super.onWidgetAdded(parentZLevel);
        this.reAddSubWidgets();
        this.updateSubWidgetsToGeometryChanges();
    }

    @Override
    public void setZLevel(float zLevel)
    {
        for (BaseWidget widget : this.subWidgets)
        {
            widget.setZLevelBasedOnParent(zLevel);
        }

        super.setZLevel(zLevel);
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY)
    {
        if (super.isMouseOver(mouseX, mouseY))
        {
            return true;
        }

        // Let the sub widgets check if the mouse is over them,
        // in case they extend beyond the bounds of this container widget.
        for (BaseWidget widget : this.subWidgets)
        {
            if (widget.isMouseOver(mouseX, mouseY))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.subWidgets.isEmpty() == false)
        {
            for (BaseWidget widget : this.subWidgets)
            {
                if (widget.tryMouseClick(mouseX, mouseY, mouseButton))
                {
                    this.runTasks();
                    // Don't call super if the button press got handled
                    return true;
                }
            }

            this.runTasks();
        }

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (this.subWidgets.isEmpty() == false)
        {
            for (BaseWidget widget : this.subWidgets)
            {
                widget.onMouseReleased(mouseX, mouseY, mouseButton);
            }

            this.runTasks();
        }
    }

    @Override
    protected boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (this.subWidgets.isEmpty() == false)
        {
            for (BaseWidget widget : this.subWidgets)
            {
                if (widget.tryMouseScroll(mouseX, mouseY, mouseWheelDelta))
                {
                    this.runTasks();
                    return true;
                }
            }

            this.runTasks();
        }

        return super.onMouseScrolled(mouseX, mouseY, mouseWheelDelta);
    }

    @Override
    public boolean onMouseMoved(int mouseX, int mouseY)
    {
        for (BaseWidget widget : this.subWidgets)
        {
            if (widget.onMouseMoved(mouseX, mouseY))
            {
                this.runTasks();
                return true;
            }
        }

        this.runTasks();

        return super.onMouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean onKeyTyped(char typedChar, int keyCode, int scanCode, int modifiers)
    {
        if (this.subWidgets.isEmpty() == false)
        {
            for (BaseWidget widget : this.subWidgets)
            {
                if (widget.onKeyTyped(typedChar, keyCode, scanCode, modifiers))
                {
                    // Don't call super if the key press got handled
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    @Nullable
    public BaseWidget getTopHoveredWidget(int mouseX, int mouseY, @Nullable BaseWidget highestFoundWidget)
    {
        highestFoundWidget = super.getTopHoveredWidget(mouseX, mouseY, highestFoundWidget);
        return BaseWidget.getTopHoveredWidgetFromList(this.subWidgets, mouseX, mouseY, highestFoundWidget);
    }

    @Override
    public List<BaseTextFieldWidget> getAllTextFields()
    {
        List<BaseTextFieldWidget> textFields = new ArrayList<>();

        if (this.subWidgets.isEmpty() == false)
        {
            for (BaseWidget widget : this.subWidgets)
            {
                textFields.addAll(widget.getAllTextFields());
            }
        }

        return textFields;
    }

    @Override
    public void renderAt(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        super.renderAt(x, y, z, mouseX, mouseY, isActiveGui, hoveredWidgetId);
        this.renderSubWidgets(x, y, z, mouseX, mouseY, isActiveGui, hoveredWidgetId);

        RenderUtils.color(1f, 1f, 1f, 1f);
    }

    protected void renderSubWidgets(int x, int y, float z, int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        if (this.subWidgets.isEmpty() == false)
        {
            int diffX = x - this.getX();
            int diffY = y - this.getY();
            float diffZ = z - this.getZLevel();

            for (BaseWidget widget : this.subWidgets)
            {
                int wx = widget.getX() + diffX;
                int wy = widget.getY() + diffY;
                float wz = widget.getZLevel() + diffZ;
                widget.renderAt(wx, wy, wz, mouseX, mouseY, isActiveGui, hoveredWidgetId);
            }
        }
    }

    @Override
    public void renderDebug(int mouseX, int mouseY, boolean hovered, boolean renderAll, boolean infoAlways)
    {
        super.renderDebug(mouseX, mouseY, hovered, renderAll, infoAlways);
        BaseScreen.renderWidgetDebug(this.subWidgets, mouseX, mouseY, renderAll, infoAlways);
    }
}
