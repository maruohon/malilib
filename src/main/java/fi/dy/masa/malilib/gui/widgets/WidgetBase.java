package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class WidgetBase
{
    protected final Minecraft mc;
    protected final FontRenderer textRenderer;
    protected final List<String> hoverStrings = new ArrayList<>();
    protected final int fontHeight;
    protected int x;
    protected int y;
    protected int xRight;
    protected int width;
    protected int height;
    protected float zLevel;
    protected boolean rightAlign;

    public WidgetBase(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.mc = Minecraft.getMinecraft();
        this.textRenderer = this.mc.fontRenderer;
        this.fontHeight = this.textRenderer.FONT_HEIGHT;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void setRightAlign(boolean rightAlign, int xRight)
    {
        this.rightAlign = rightAlign;

        if (rightAlign)
        {
            this.xRight = xRight;
            this.updatePositionIfRightAligned();
        }
    }

    protected void updatePositionIfRightAligned()
    {
        if (this.rightAlign)
        {
            this.x = this.xRight - this.width;
        }
    }

    public void setZLevel(float zLevel)
    {
        this.zLevel = zLevel;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public void setWidth(int width)
    {
        this.width = width;
        this.updatePositionIfRightAligned();
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return mouseX >= this.x && mouseX < this.x + this.width &&
               mouseY >= this.y && mouseY < this.y + this.height;
    }

    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isMouseOver(mouseX, mouseY))
        {
            return this.onMouseClickedImpl(mouseX, mouseY, mouseButton);
        }

        return false;
    }

    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        return false;
    }

    public void onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        this.onMouseReleasedImpl(mouseX, mouseY, mouseButton);
    }

    public void onMouseReleasedImpl(int mouseX, int mouseY, int mouseButton)
    {
    }

    public boolean onMouseScrolled(int mouseX, int mouseY, double mouseWheelDelta)
    {
        if (this.isMouseOver(mouseX, mouseY))
        {
            return this.onMouseScrolledImpl(mouseX, mouseY, mouseWheelDelta);
        }

        return false;
    }

    public boolean onMouseScrolledImpl(int mouseX, int mouseY, double mouseWheelDelta)
    {
        return false;
    }

    public boolean onKeyTyped(char typedChar, int keyCode)
    {
        return this.onKeyTypedImpl(typedChar, keyCode);
    }

    protected boolean onKeyTypedImpl(char typedChar, int keyCode)
    {
        return false;
    }

    /**
     * Returns true if this widget can be selected by clicking at the given point
     */
    public boolean canSelectAt(int mouseX, int mouseY, int mouseButton)
    {
        return this.isMouseOver(mouseX, mouseY);
    }

    public boolean hasHoverText()
    {
        return this.hoverStrings.isEmpty() == false;
    }

    public void clearHoverStrings()
    {
        this.hoverStrings.clear();
    }

    public void setHoverStrings(String... hoverStrings)
    {
        this.setHoverStrings(Arrays.asList(hoverStrings));
    }

    public void setHoverStrings(List<String> hoverStrings)
    {
        this.hoverStrings.clear();

        for (String str : hoverStrings)
        {
            str = StringUtils.translate(str);

            String[] parts = str.split("\\\\n");

            for (String part : parts)
            {
                this.hoverStrings.add(StringUtils.translate(part));
            }
        }
    }

    public List<String> getHoverStrings()
    {
        return this.hoverStrings;
    }

    public void bindTexture(ResourceLocation texture)
    {
        RenderUtils.bindTexture(texture);
    }

    public int getStringWidth(String text)
    {
        return this.textRenderer.getStringWidth(text);
    }

    public void drawString(int x, int y, int color, String text)
    {
        this.textRenderer.drawString(text, x, y, color);
    }

    public void drawCenteredString(int x, int y, int color, String text)
    {
        this.textRenderer.drawString(text, x - this.getStringWidth(text) / 2, y, color);
    }

    public void drawStringWithShadow(int x, int y, int color, String text)
    {
        this.textRenderer.drawStringWithShadow(text, x, y, color);
    }

    public void drawCenteredStringWithShadow(int x, int y, int color, String text)
    {
        this.textRenderer.drawStringWithShadow(text, x - this.getStringWidth(text) / 2, y, color);
    }

    public void render(int mouseX, int mouseY, boolean selected)
    {
    }

    public void postRenderHovered(int mouseX, int mouseY, boolean selected)
    {
        if (this.hasHoverText() && this.isMouseOver(mouseX, mouseY))
        {
            RenderUtils.drawHoverText(mouseX, mouseY, this.getHoverStrings());
            RenderUtils.disableItemLighting();
        }
    }
}
