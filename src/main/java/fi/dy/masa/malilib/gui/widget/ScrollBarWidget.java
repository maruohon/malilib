package fi.dy.masa.malilib.gui.widget;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.render.RenderUtils;

public class ScrollBarWidget extends BaseWidget
{
    @Nullable protected final Icon barTexture;
    @Nullable protected Icon arrowTextureUp;
    @Nullable protected Icon arrowTextureDown;
    protected final Minecraft mc = Minecraft.getMinecraft();
    protected boolean mouseOver = false;
    protected boolean dragging = false;
    protected boolean renderScrollbarBackgroundColor = true;
    protected int currentValue = 0;
    protected int maxValue = 100;
    protected int backgroundColor = 0x55FFFFFF;
    protected int scrollBarColor = 0xFFFFFFFF;
    protected int dragStartValue = 0;
    protected int dragStartY = 0;
    protected int totalHeight;

    public ScrollBarWidget(int x, int y, int width, int height)
    {
        this(x, y, width, height, null);
    }

    public ScrollBarWidget(int x, int y, int width, int height, @Nullable Icon barTexture)
    {
        super(x, y, width, height);

        this.barTexture = barTexture;
    }

    public ScrollBarWidget setRenderBackgroundColor(boolean render)
    {
        this.renderScrollbarBackgroundColor = render;
        return this;
    }

    public ScrollBarWidget setScrollBarColor(int color)
    {
        this.scrollBarColor = color;
        return this;
    }

    public ScrollBarWidget setBackgroundColor(int color)
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
    public ScrollBarWidget setArrowTextures(@Nullable Icon iconUp, @Nullable Icon iconDown)
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

    public void setTotalHeight(int totalHeight)
    {
        this.totalHeight = totalHeight;
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
            this.setValue(this.getValue() - this.getMoveAmountForArrowClick());
        }
        else if (this.isMouseOverDownArrow(mouseX, mouseY))
        {
            this.setValue(this.getValue() + this.getMoveAmountForArrowClick());
        }
        else if (mouseButton == 0 && this.wasMouseOver())
        {
            this.setIsDragging(true);
        }
        else
        {
            float relVal = (float) (mouseY - this.getY()) / (float) Math.max(1, this.getHeight());
            this.setValue((int) (relVal * this.maxValue));
        }

        return true;
    }

    /**
     * Returns the move amount for clicking on the up or down
     * arrows, based on whether or not shift and/or ctrl are held
     * @return
     */
    protected int getMoveAmountForArrowClick()
    {
        int amount = 1;

        if (BaseScreen.isShiftDown())
        {
            amount *= 5;
        }

        if (BaseScreen.isCtrlDown())
        {
            amount *= 4;
        }

        return amount;
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
        if (this.getRenderArrows())
        {
            int x = this.getX();
            int y = this.getY();
            int width = this.getWidth();

            return mouseX >= x && mouseX < x + width &&
                   mouseY >= y && mouseY < y + this.arrowTextureUp.getHeight();
        }

        return false;
    }

    public boolean isMouseOverDownArrow(int mouseX, int mouseY)
    {
        if (this.getRenderArrows())
        {
            int x = this.getX();
            int y = this.getY();
            int height = this.getHeight();

            return mouseX >= x && mouseX < x + this.getWidth() &&
                   mouseY >= y + height - this.arrowTextureDown.getHeight() && mouseY < y + height;
        }

        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        int x = this.getX();
        int y = this.getY();
        int width = this.getWidth();
        int height = this.getHeight();
        int totalHeight = this.totalHeight;

        RenderUtils.color(1f, 1f, 1f, 1f);

        if (this.renderScrollbarBackgroundColor)
        {
            RenderUtils.drawRect(x, y, width, height, this.backgroundColor, this.getZLevel());
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

            int slideHeight = Math.max(0, height - upArH - downArH);
            totalHeight = Math.max(0, totalHeight - upArH - downArH);
            float relative = Math.min(1.0F, (float) slideHeight / (float) totalHeight);
            int barHeight = (int) (relative * slideHeight);
            int barTravel = slideHeight - barHeight;
            int barPosition = y + upArH + (this.maxValue > 0 ? (int) ((this.currentValue / (float) this.maxValue) * barTravel) : 0);

            if (useArrows)
            {
                this.arrowTextureUp.renderAt(x, y, this.getZLevel(), false, this.isMouseOverUpArrow(mouseX, mouseY));
                this.arrowTextureDown.renderAt(x, y + this.getHeight() - downArH, this.getZLevel(), false, this.isMouseOverDownArrow(mouseX, mouseY));
            }

            if (this.barTexture != null && barHeight >= 4)
            {
                RenderUtils.bindTexture(this.barTexture.getTexture());
                int u = this.barTexture.getU();
                int v = this.barTexture.getV();
                int w = this.barTexture.getWidth();
                int h = this.barTexture.getHeight();
                int z = this.getZLevel();

                RenderUtils.drawTexturedRect(x + 1, barPosition                , u, v        , w, barHeight - 2, z);
                RenderUtils.drawTexturedRect(x + 1, barPosition + barHeight - 2, u, v + h - 2, w, 2            , z);
            }
            else
            {
                RenderUtils.drawRect(x + 1, barPosition, width - 2, barHeight, this.scrollBarColor, this.getZLevel());
            }

            // FIXME?
            this.mouseOver = mouseX > x && mouseX < x + width && mouseY > barPosition && mouseY < barPosition + barHeight;

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
