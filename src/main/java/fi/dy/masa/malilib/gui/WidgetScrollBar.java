package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

public class WidgetScrollBar extends WidgetBase
{
    @Nullable protected final IGuiIcon barTexture;
    @Nullable protected IGuiIcon arrowTextureUp;
    @Nullable protected IGuiIcon arrowTextureDown;
    protected final Minecraft mc = Minecraft.getMinecraft();
    protected boolean mouseOver = false;
    protected boolean dragging = false;
    protected boolean renderScrollbarBackground = true;
    protected int currentValue = 0;
    protected int maxValue = 100;
    protected int backgroundColor = 0x55FFFFFF;
    protected int foregroundColor = 0xFFFFFFFF;
    protected int dragStartValue = 0;
    protected int dragStartY = 0;

    public WidgetScrollBar(int x, int y, int width, int height)
    {
        this(x, y, width, height, null);
    }

    public WidgetScrollBar(int x, int y, int width, int height, @Nullable IGuiIcon barTexture)
    {
        super(x, y, width, height);

        this.barTexture = barTexture;
    }

    public WidgetScrollBar setRenderBarBackground(boolean render)
    {
        this.renderScrollbarBackground = render;
        return this;
    }

    public WidgetScrollBar setBackgroundColor(int color)
    {
        this.backgroundColor = color;
        return this;
    }

    /**
     * Sets the arrow up and down textures.
     * If both of them are set, then they will be used and rendered,
     * otherwise the arrows will not be used and rendered.
     * @param iconUp
     * @param iconDown
     * @return
     */
    public WidgetScrollBar setArrowTextures(@Nullable IGuiIcon iconUp, @Nullable IGuiIcon iconDown)
    {
        this.arrowTextureUp = iconUp;
        this.arrowTextureDown = iconDown;
        return this;
    }

    public boolean getRenderArrows()
    {
        return this.arrowTextureUp != null && this.arrowTextureDown != null;
    }

    public int getValue()
    {
        return this.currentValue;
    }

    public void setValue(int value)
    {
        this.currentValue = MathHelper.clamp(value, 0, this.maxValue);
    }

    public void offsetValue(int offset)
    {
        this.setValue(this.currentValue + offset);
    }

    public int getMaxValue()
    {
        return this.maxValue;
    }

    public void setMaxValue(int maxValue)
    {
        this.maxValue = Math.max(0, maxValue);
        this.currentValue = Math.min(this.currentValue, this.maxValue);
    }

    public boolean wasMouseOver()
    {
        return this.mouseOver;
    }

    public void setIsDragging(boolean isDragging)
    {
        this.dragging = isDragging;
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isMouseOverUpArrow(mouseX, mouseY))
        {
            this.setValue(this.getValue() - (GuiBase.isShiftDown() ? 5 : 1));
        }
        else if (this.isMouseOverDownArrow(mouseX, mouseY))
        {
            this.setValue(this.getValue() + (GuiBase.isShiftDown() ? 5 : 1));
        }
        else if (mouseButton == 0 && this.wasMouseOver())
        {
            this.setIsDragging(true);
        }
        else
        {
            float relVal = (float) (mouseY - this.y) / (float) Math.max(1, this.height);
            this.setValue((int) (relVal * this.maxValue));
        }

        return true;
    }

    @Override
    public void onMouseReleasedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            this.setIsDragging(false);
        }
    }

    public boolean isMouseOverUpArrow(int mouseX, int mouseY)
    {
        return this.getRenderArrows() &&
                mouseX >= this.x && mouseX < this.x + this.width &&
                mouseY >= this.y && mouseY < this.y + this.arrowTextureUp.getHeight();
    }

    public boolean isMouseOverDownArrow(int mouseX, int mouseY)
    {
        return this.getRenderArrows() &&
                mouseX >= this.x && mouseX < this.x + this.width &&
                mouseY >= this.y + this.height - this.arrowTextureDown.getHeight() && mouseY < this.y + this.height;
    }

    public void render(int mouseX, int mouseY, int height, int totalHeight)
    {
        this.height = height;

        if (this.renderScrollbarBackground)
        {
            RenderUtils.drawRect(this.x, this.y, this.width, height, this.backgroundColor);
        }

        if (totalHeight > 0)
        {
            boolean useArrows = this.getRenderArrows();
            int upArH = 0;
            int downArH = 0;

            if (useArrows)
            {
                upArH = this.arrowTextureUp.getHeight();
                downArH = this.arrowTextureDown.getHeight();
            }

            int slideHeight = Math.max(0, height - upArH - downArH - 2);
            totalHeight = Math.max(0, totalHeight - upArH - downArH - 2);
            float relative = Math.min(1.0F, (float) slideHeight / (float) totalHeight);
            int barHeight = (int) (relative * slideHeight);
            int barTravel = slideHeight - barHeight;
            int barPosition = this.y + 1 + upArH + (this.maxValue > 0 ? (int) ((this.currentValue / (float) this.maxValue) * barTravel) : 0);

            RenderUtils.color(1f, 1f, 1f, 1f);

            if (useArrows)
            {
                RenderUtils.bindTexture(this.arrowTextureUp.getTexture());
                this.arrowTextureUp.renderAt(this.x, this.y, this.zLevel, false, this.isMouseOverUpArrow(mouseX, mouseY));

                RenderUtils.bindTexture(this.arrowTextureDown.getTexture());
                this.arrowTextureDown.renderAt(this.x, this.y + this.height - downArH, this.zLevel, false, this.isMouseOverDownArrow(mouseX, mouseY));
            }

            if (this.barTexture != null && barHeight >= 4)
            {
                RenderUtils.bindTexture(this.barTexture.getTexture());
                int u = this.barTexture.getU();
                int v = this.barTexture.getV();
                int w = this.barTexture.getWidth();
                int h = this.barTexture.getHeight();

                RenderUtils.drawTexturedRect(this.x + 1, barPosition                , u, v        , w, barHeight - 2);
                RenderUtils.drawTexturedRect(this.x + 1, barPosition + barHeight - 2, u, v + h - 2, w, 2            );
            }
            else
            {
                RenderUtils.drawRect(this.x + 1, barPosition, this.width - 2, barHeight, this.foregroundColor);
            }

            this.mouseOver = mouseX > this.x && mouseX < this.x + this.width && mouseY > barPosition && mouseY < barPosition + barHeight;
            this.handleDrag(mouseY, barTravel);
        }
    }

    public void handleDrag(int mouseY, int barTravel)
    {
        if (this.dragging)
        {
            float valuePerPixel = (float) this.maxValue / barTravel;
            this.setValue((int) (this.dragStartValue + ((mouseY - this.dragStartY) * valuePerPixel)));
        }
        else
        {
            this.dragStartY = mouseY;
            this.dragStartValue = this.currentValue;
        }
    }
}
