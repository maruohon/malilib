package fi.dy.masa.malilib.gui;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.math.MathHelper;

public class GuiScrollBar
{
    protected boolean mouseOver = false;
    protected boolean dragging = false;
    protected int currentValue = 0;
    protected int maxValue = 100;
    protected int backgroundColor = 0x44FFFFFF;
    protected int foregroundColor = 0xFFFFFFFF;
    protected int dragStartValue = 0;
    protected int dragStartY = 0;

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

    public void render(int mouseX, int mouseY, float partialTicks, int xPosition, int yPosition, int width, int height, int totalHeight)
    {
        DrawableHelper.fill(xPosition, yPosition, xPosition + width, yPosition + height, this.backgroundColor);

        if (totalHeight > 0)
        {
            int slideHeight = height - 2;
            float relative = Math.min(1.0F, (float) slideHeight / (float) totalHeight);
            int barHeight = (int) (relative * slideHeight);
            int barTravel = slideHeight - barHeight;
            int barPosition = yPosition + 1 + (this.maxValue > 0 ? (int) ((this.currentValue / (float) this.maxValue) * barTravel) : 0);

            DrawableHelper.fill(xPosition + 1, barPosition, xPosition + width - 1, barPosition + barHeight, this.foregroundColor);

            this.mouseOver = mouseX > xPosition && mouseX < xPosition + width && mouseY > barPosition && mouseY < barPosition + barHeight;
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
