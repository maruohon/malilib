package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;

public abstract class WidgetContainer extends WidgetBase
{
    protected final List<WidgetBase> subWidgets = new ArrayList<>();
    @Nullable protected WidgetBase hoveredSubWidget = null;

    public WidgetContainer(int x, int y, int width, int height, float zLevel)
    {
        super(x, y, width, height, zLevel);
    }

    protected void addWidget(WidgetBase widget)
    {
        this.subWidgets.add(widget);
    }

    protected void addButton(ButtonBase button, IButtonActionListener listener)
    {
        button.setActionListener(listener);
        this.addWidget(button);
    }

    protected void addLabel(int x, int y, int width, int height, int textColor, String... lines)
    {
        if (lines != null && lines.length >= 1)
        {
            Minecraft mc = Minecraft.getMinecraft();

            if (width == -1)
            {
                for (String line : lines)
                {
                    width = Math.max(width, mc.fontRenderer.getStringWidth(line));
                }
            }

            WidgetLabel label = new WidgetLabel(x, y, width, height, this.zLevel, textColor, lines);
            this.addWidget(label);
        }
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        boolean handled = false;

        if (this.isMouseOver(mouseX, mouseY))
        {
            if (this.subWidgets.isEmpty() == false)
            {
                for (WidgetBase widget : this.subWidgets)
                {
                    if (widget.isMouseOver(mouseX, mouseY) && widget.onMouseClicked(mouseX, mouseY, mouseButton))
                    {
                        // Don't call super if the button press got handled
                        handled = true;
                    }
                }
            }

            if (handled == false)
            {
                handled = this.onMouseClickedImpl(mouseX, mouseY, mouseButton);
            }
        }

        return handled;
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
        if (this.isMouseOver(mouseX, mouseY))
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

            return this.onMouseScrolledImpl(mouseX, mouseY, mouseWheelDelta);
        }

        return false;
    }

    @Override
    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        boolean handled = false;

        if (this.subWidgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.subWidgets)
            {
                if (widget.onKeyTyped(typedChar, keyCode))
                {
                    // Don't call super if the key press got handled
                    handled = true;
                }
            }
        }

        if (handled == false)
        {
            handled = this.onKeyTypedImpl(typedChar, keyCode);
        }

        return handled;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        this.drawSubWidgets(mouseX, mouseY);
    }

    @Override
    public void postRenderHovered(int mouseX, int mouseY, boolean selected)
    {
        this.drawHoveredSubWidget(mouseX, mouseY);
    }

    protected void drawSubWidgets(int mouseX, int mouseY)
    {
        this.hoveredSubWidget = null;

        if (this.subWidgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.subWidgets)
            {
                widget.render(mouseX, mouseY, false);

                if (widget.isMouseOver(mouseX, mouseY))
                {
                    this.hoveredSubWidget = widget;
                }
            }
        }
    }

    protected void drawHoveredSubWidget(int mouseX, int mouseY)
    {
        if (this.hoveredSubWidget != null)
        {
            this.hoveredSubWidget.postRenderHovered(mouseX, mouseY, false);

            RenderHelper.disableStandardItemLighting();
        }
    }
}
