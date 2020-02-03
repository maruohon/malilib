package fi.dy.masa.malilib.gui.widgets;

import net.minecraft.item.ItemStack;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.RenderUtils;

public class WidgetItemStack extends WidgetBackground
{
    protected ItemStack stack;
    protected boolean doHighlight;
    protected int highlightColor;
    protected float scale = 1f;

    public WidgetItemStack(int x, int y, ItemStack stack)
    {
        super(x, y, 16, 16);

        this.setBorderWidth(0);
        this.setBackgroundColor(0xC0C0C0C0);
        this.setBackgroundEnabled(true);

        this.setStack(stack);
    }

    public WidgetItemStack setStack(ItemStack stack)
    {
        this.stack = stack;

        this.updateWidth();
        this.updateHeight();

        return this;
    }

    public WidgetItemStack setDoHighlight(boolean doHighlight)
    {
        this.doHighlight = doHighlight;
        return this;
    }

    public WidgetItemStack setHighlightColor(int color)
    {
        this.highlightColor = color;
        return this;
    }

    public WidgetItemStack setScale(float scale)
    {
        this.scale = scale;
        return this;
    }

    @Override
    public int updateWidth()
    {
        int width = 16;

        if (this.backgroundEnabled)
        {
            width += this.paddingX * 2 + this.borderWidth * 2;
        }

        this.setWidth(width);

        return this.getWidth();
    }

    @Override
    public int updateHeight()
    {
        int height = 16;

        if (this.backgroundEnabled)
        {
            height += this.paddingY * 2 + this.borderWidth * 2;
        }

        this.setHeight(height);

        return this.getHeight();
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        this.renderWidgetBackground();

        int x = this.getX();
        int y = this.getY();
        int z = this.getZLevel();
        int width = this.getWidth();
        int height = this.getHeight();

        if (this.backgroundEnabled)
        {
            x += this.paddingX + this.borderWidth;
            y += this.paddingY + this.borderWidth;
        }

        if (this.doHighlight && this.isMouseOver(mouseX, mouseY))
        {
            RenderUtils.drawRect(x, y, width, height, this.highlightColor, z);
        }

        if (this.stack.isEmpty() == false)
        {
            InventoryOverlay.renderStackAt(this.stack, x, y, z, this.scale, this.mc);
        }
    }
}
