package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.button.BaseButton;
import fi.dy.masa.malilib.gui.button.ButtonActionListener;
import fi.dy.masa.malilib.listener.TextChangeListener;
import fi.dy.masa.malilib.render.RenderUtils;

public abstract class ContainerWidget extends BackgroundWidget
{
    protected final List<BaseWidget> subWidgets = new ArrayList<>();

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

    /**
     * This method should be overridden to update any sub widget
     * positions to the current position of this container widget,
     * or to any other changes such as width or height changes.
     */
    public void updateSubWidgetPositions(int oldX, int oldY)
    {
        int diffX = this.getX() - oldX;
        int diffY = this.getY() - oldY;

        if (diffX != 0 || diffY != 0)
        {
            for (BaseWidget widget : this.subWidgets)
            {
                widget.setPosition(widget.getX() + diffX, widget.getY() + diffY);
            }
        }
    }

    @Override
    protected void onPositionChanged(int oldX, int oldY)
    {
        super.onPositionChanged(oldX, oldY);
        this.updateSubWidgetPositions(oldX, oldY);
    }

    @Override
    protected void onSizeChanged()
    {
        super.onSizeChanged();
        this.updateSubWidgetPositions(this.getX(), this.getY());
    }

    @Override
    protected void onPositionOrSizeChanged(int oldX, int oldY)
    {
        this.updateSubWidgetPositions(oldX, oldY);
    }

    public  <T extends BaseWidget> T addWidget(T widget)
    {
        this.subWidgets.add(widget);
        this.onSubWidgetAdded(widget);

        return widget;
    }

    public  <T extends BaseButton> T addButton(T button, ButtonActionListener listener)
    {
        button.setActionListener(listener);
        this.addWidget(button);
        return button;
    }

    public  <T extends BaseTextFieldWidget> T addTextField(T widget, TextChangeListener listener)
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
        widget.onWidgetAdded(this.getZLevel());
    }

    @Override
    public BaseWidget setZLevel(int zLevel)
    {
        for (BaseWidget widget : this.subWidgets)
        {
            widget.setZLevelBasedOnParent(zLevel);
        }

        return super.setZLevel(zLevel);
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
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.subWidgets.isEmpty() == false)
        {
            for (BaseWidget widget : this.subWidgets)
            {
                if (widget.onMouseClicked(mouseX, mouseY, mouseButton))
                {
                    // Don't call super if the button press got handled
                    return true;
                }
            }
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
        }

        this.onMouseReleasedImpl(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (this.subWidgets.isEmpty() == false)
        {
            for (BaseWidget widget : this.subWidgets)
            {
                if (widget.onMouseScrolled(mouseX, mouseY, mouseWheelDelta))
                {
                    return true;
                }
            }
        }

        return super.onMouseScrolled(mouseX, mouseY, mouseWheelDelta);
    }

    @Override
    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        if (this.subWidgets.isEmpty() == false)
        {
            for (BaseWidget widget : this.subWidgets)
            {
                if (widget.onKeyTyped(typedChar, keyCode))
                {
                    // Don't call super if the key press got handled
                    return true;
                }
            }
        }

        return this.onKeyTypedImpl(typedChar, keyCode);
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
    public void render(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        this.render(mouseX, mouseY, isActiveGui, this.getId() == hoveredWidgetId);
        this.drawSubWidgets(mouseX, mouseY, isActiveGui, hoveredWidgetId);

        RenderUtils.color(1f, 1f, 1f, 1f);
    }

    protected void drawSubWidgets(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        if (this.subWidgets.isEmpty() == false)
        {
            for (BaseWidget widget : this.subWidgets)
            {
                widget.render(mouseX, mouseY, isActiveGui, hoveredWidgetId);
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
