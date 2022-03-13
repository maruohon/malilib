package fi.dy.masa.malilib.gui.widget;

import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;

public class ScrollBarWidget extends InteractableWidget
{
    @Nullable protected final Icon barTexture;
    @Nullable protected MultiIcon arrowTextureUp;
    @Nullable protected MultiIcon arrowTextureDown;
    @Nullable protected EventListener changeListener;
    protected boolean mouseOver = false;
    protected boolean dragging = false;
    protected boolean renderScrollbarBackgroundColor = true;
    protected int currentValue = 0;
    protected int maxValue = 100;
    protected int backgroundColor = 0xFF505050;
    protected int scrollBarColor = 0xFFFFFFFF;
    protected int dragStartValue = 0;
    protected int dragStartY = 0;
    protected int totalHeight;

    public ScrollBarWidget(int width, int height)
    {
        this(width, height, null);
    }

    public ScrollBarWidget(int width, int height, @Nullable Icon barTexture)
    {
        super(width, height);

        this.barTexture = barTexture;
        this.arrowTextureUp = DefaultIcons.SMALL_ARROW_UP;
        this.arrowTextureDown = DefaultIcons.SMALL_ARROW_DOWN;
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

    public ScrollBarWidget setValueChangeListener(EventListener listener)
    {
        this.changeListener = listener;
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
    public ScrollBarWidget setArrowTextures(@Nullable MultiIcon iconUp, @Nullable MultiIcon iconDown)
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

    public void setValueNoNotify(int value)
    {
        this.currentValue = MathHelper.clamp(value, 0, this.maxValue);
    }

    public void setValue(int value)
    {
        int old = this.currentValue;
        this.setValueNoNotify(value);

        if (this.changeListener != null && old != this.currentValue)
        {
            this.changeListener.onEvent();
        }
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
        this.setValue(this.currentValue);
    }

    public void setMaxValueNoNotify(int maxValue)
    {
        this.maxValue = Math.max(0, maxValue);
        this.setValueNoNotify(this.currentValue);
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
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
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

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            this.setIsDragging(false);
        }
    }

    @Override
    protected boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        int amount = mouseWheelDelta < 0 ? 1 : -1;
        this.offsetValue(amount);
        return true;
    }

    @Override
    public boolean onMouseMoved(int mouseX, int mouseY)
    {
        int totalHeight = this.totalHeight;

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

            int height = this.getHeight();
            int slideHeight = Math.max(0, height - upArH - downArH);
            totalHeight = Math.max(0, totalHeight - upArH - downArH);
            float relative = Math.min(1.0F, (float) slideHeight / (float) totalHeight);
            int barHeight = (int) Math.max((relative * slideHeight), 3);
            int barTravel = slideHeight - barHeight;

            this.handleDrag(mouseY, barTravel);

            return this.dragging;
        }

        return false;
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
    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        int width = this.getWidth();
        int height = this.getHeight();
        int totalHeight = this.totalHeight;
        int mouseX = ctx.mouseX;
        int mouseY = ctx.mouseY;

        RenderUtils.color(1f, 1f, 1f, 1f);

        if (this.renderScrollbarBackgroundColor)
        {
            ShapeRenderUtils.renderRectangle(x, y, z, width, height, this.backgroundColor);
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
            int barHeight = (int) Math.max((relative * slideHeight), 3);
            int barTravel = slideHeight - barHeight;
            int barPosition = y + upArH + (this.maxValue > 0 ? (int) ((this.currentValue / (float) this.maxValue) * barTravel) : 0);

            if (useArrows)
            {
                this.arrowTextureUp.renderAt(x, y, z, false, this.isMouseOverUpArrow(mouseX, mouseY));
                this.arrowTextureDown.renderAt(x, y + this.getHeight() - downArH, z, false, this.isMouseOverDownArrow(mouseX, mouseY));
            }

            if (this.barTexture != null && barHeight >= 4)
            {
                RenderUtils.bindTexture(this.barTexture.getTexture());
                int u = this.barTexture.getU();
                int v = this.barTexture.getV();
                int w = this.barTexture.getWidth();
                int h = this.barTexture.getHeight();

                ShapeRenderUtils.renderTexturedRectangle256(x + 1, barPosition                , z, u, v        , w, barHeight - 2);
                ShapeRenderUtils.renderTexturedRectangle256(x + 1, barPosition + barHeight - 2, z, u, v + h - 2, w, 2);
            }
            else
            {
                ShapeRenderUtils.renderRectangle(x + 1, barPosition, z, width - 2, barHeight, this.scrollBarColor);
            }

            // FIXME?
            this.mouseOver = mouseX >= x && mouseX < x + width && mouseY >= barPosition && mouseY < barPosition + barHeight;
        }
    }
}
