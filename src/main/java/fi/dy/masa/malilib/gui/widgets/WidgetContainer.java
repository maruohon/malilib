package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ITextFieldListener;
import fi.dy.masa.malilib.render.RenderUtils;

public abstract class WidgetContainer extends WidgetBackground
{
    protected final List<WidgetBase> subWidgets = new ArrayList<>();

    public WidgetContainer(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    protected <T extends WidgetBase> T addWidget(T widget)
    {
        this.subWidgets.add(widget);
        this.onSubWidgetAdded(widget);

        return widget;
    }

    protected <T extends ButtonBase> T addButton(T button, IButtonActionListener listener)
    {
        button.setActionListener(listener);
        this.addWidget(button);
        return button;
    }

    protected <T extends WidgetTextFieldBase> T addTextField(T widget, ITextFieldListener listener)
    {
        widget.setListener(listener);
        this.addWidget(widget);
        return widget;
    }

    protected WidgetLabel addLabel(int x, int y, int textColor, String... lines)
    {
        return this.addLabel(x, y, -1, -1, textColor, lines);
    }

    protected WidgetLabel addLabel(int x, int y, int width, int height, int textColor, String... lines)
    {
        return this.addWidget(new WidgetLabel(x, y, width, height, textColor, lines));
    }

    protected void removeWidget(WidgetBase widget)
    {
        this.subWidgets.remove(widget);
    }

    protected void clearWidgets()
    {
        this.subWidgets.clear();
    }

    protected void onSubWidgetAdded(WidgetBase widget)
    {
        widget.onWidgetAdded(this.getZLevel());
    }

    @Override
    public WidgetBase setZLevel(int zLevel)
    {
        for (WidgetBase widget : this.subWidgets)
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
        for (WidgetBase widget : this.subWidgets)
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
            for (WidgetBase widget : this.subWidgets)
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
            for (WidgetBase widget : this.subWidgets)
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
            for (WidgetBase widget : this.subWidgets)
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
            for (WidgetBase widget : this.subWidgets)
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
    public WidgetBase getTopHoveredWidget(int mouseX, int mouseY, @Nullable WidgetBase highestFoundWidget)
    {
        highestFoundWidget = super.getTopHoveredWidget(mouseX, mouseY, highestFoundWidget);
        return WidgetBase.getTopHoveredWidgetFromList(this.subWidgets, mouseX, mouseY, highestFoundWidget);
    }

    @Override
    public List<WidgetTextFieldBase> getAllTextFields()
    {
        List<WidgetTextFieldBase> textFields = new ArrayList<>();

        if (this.subWidgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.subWidgets)
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
            for (WidgetBase widget : this.subWidgets)
            {
                widget.render(mouseX, mouseY, isActiveGui, hoveredWidgetId);
            }
        }
    }

    @Override
    public void renderDebug(int mouseX, int mouseY, boolean hovered, boolean renderAll, boolean infoAlways)
    {
        super.renderDebug(mouseX, mouseY, hovered, renderAll, infoAlways);
        GuiBase.renderWidgetDebug(this.subWidgets, mouseX, mouseY, renderAll, infoAlways);
    }
}
