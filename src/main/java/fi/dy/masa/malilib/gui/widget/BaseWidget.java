package fi.dy.masa.malilib.gui.widget;

import org.lwjgl.opengl.GL11;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.render.RectangleRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.text.TextRenderFunction;
import fi.dy.masa.malilib.util.data.Color4f;

public class BaseWidget
{
    public static final ImmutableList<String> EMPTY_STRING_LIST = ImmutableList.of();
    public static final RectangleRenderer DEBUG_TEXT_BG_RENDERER = (x, y, w, h, z) -> RenderUtils.renderOutlinedBox(x - 3, y - 3, w + 6, h + 6, 0xE0000000, 0xFFC0C0C0, z);

    private static final ArrayListMultimap<Long, String> DEBUG_STRINGS = ArrayListMultimap.create();
    private static int lastDebugOutlineColorHue;

    protected final Minecraft mc;
    protected final FontRenderer textRenderer;
    protected final int fontHeight;
    private int x;
    private int y;
    private float zLevel;
    private int xRight;
    private int width;
    private int height;
    protected int maxWidth;
    protected int maxHeight;
    private boolean keepOnScreen;
    private boolean rightAlign;
    protected boolean automaticHeight;
    protected boolean automaticWidth;
    protected boolean hasMaxHeight;
    protected boolean hasMaxWidth;

    public BaseWidget(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.mc = Minecraft.getMinecraft();
        this.textRenderer = this.mc.fontRenderer;
        this.fontHeight = this.textRenderer.FONT_HEIGHT;

        this.automaticWidth = width < 0;
        this.automaticHeight = height < 0;
        this.hasMaxWidth = width < -1;
        this.hasMaxHeight = height < -1;
        this.maxWidth = this.hasMaxWidth ? -width : width;
        this.maxHeight = this.hasMaxHeight ? -height : height;
    }

    public final int getX()
    {
        return this.x;
    }

    public final int getY()
    {
        return this.y;
    }

    public final int getRight()
    {
        return this.getX() + this.getWidth();
    }

    public final int getBottom()
    {
        return this.getY() + this.getHeight();
    }

    public final void setX(int x)
    {
        int oldX = this.x;
        int oldY = this.y;

        this.x = x;

        this.onPositionChanged(oldX, oldY);
    }

    public final void setY(int y)
    {
        int oldX = this.x;
        int oldY = this.y;

        this.y = y;

        this.onPositionChanged(oldX, oldY);
    }

    public final void setPosition(int x, int y)
    {
        int oldX = this.x;
        int oldY = this.y;

        this.x = x;
        this.y = y;

        this.onPositionChanged(oldX, oldY);
    }

    public void setPositionNoUpdate(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public final void setPositionAndSize(int x, int y, int width, int height)
    {
        int oldX = this.x;
        int oldY = this.y;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.onPositionOrSizeChanged(oldX, oldY);
    }

    public void setRightX(int x)
    {
        this.xRight = x;
        this.updateHorizontalPositionIfRightAligned();
    }

    public void setRightAlign(boolean rightAlign, int xRight, boolean keepOnScreen)
    {
        this.rightAlign = rightAlign;
        this.keepOnScreen = keepOnScreen;

        if (rightAlign)
        {
            this.setRightX(xRight);
        }
    }

    protected void updateHorizontalPositionIfRightAligned()
    {
        if (this.rightAlign)
        {
            int oldX = this.x;
            int oldY = this.y;

            this.x = this.xRight - this.width;

            if (this.keepOnScreen && this.x < 0)
            {
                this.xRight += -this.x + 4;
                this.x = 4;
            }

            this.onPositionChanged(oldX, oldY);
        }
    }

    /**
     * This method is called after the widget position is changed
     * @param oldX the x position before the position was changed
     * @param oldY the y position before the position was changed
     */
    protected void onPositionChanged(int oldX, int oldY)
    {
    }

    /**
     * This method is called after the widget size is changed
     */
    protected void onSizeChanged()
    {
    }

    /**
     * This method is called after either the widget position or size is changed.
     * This is meant for cases where it's necessary or beneficial to avoid the
     * calls for both size and position changes separately.
     */
    protected void onPositionOrSizeChanged(int oldX, int oldY)
    {
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

        this.updateHorizontalPositionIfRightAligned();
        this.onSizeChanged();
    }

    public void setHeight(int height)
    {
        this.height = height;
        this.onSizeChanged();
    }

    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;

        this.updateHorizontalPositionIfRightAligned();
        this.onSizeChanged();
    }

    /**
     * This is called when the container widget or screen
     * changes its geometry, in case this widget wants to somehow
     * react to that change and maybe update its sub widget positions.
     */
    public void onContainerGeometryChanged()
    {
    }

    public void updateWidth()
    {
    }

    public void updateHeight()
    {
    }

    public float getZLevel()
    {
        return this.zLevel;
    }

    public void setZLevel(float zLevel)
    {
        this.zLevel = zLevel;
    }

    public void setZLevelBasedOnParent(float parentZLevel)
    {
        this.setZLevel(parentZLevel + this.getSubWidgetZLevelIncrement());
    }

    /**
     * This method is called whenever a widget gets added to its parent widget or GUI.
     * By default it updates the widget's own rendering Z-level based on the parent's Z-level.
     */
    public void onWidgetAdded(float parentZLevel)
    {
        this.setZLevelBasedOnParent(parentZLevel);
    }

    protected int getSubWidgetZLevelIncrement()
    {
        return 2;
    }

    protected int getCenteredTextOffsetY()
    {
        return (this.getHeight() - this.fontHeight) / 2 + 1;
    }

    public void bindTexture(ResourceLocation texture)
    {
        RenderUtils.bindTexture(texture);
    }

    public int getStringWidth(String text)
    {
        return this.textRenderer.getStringWidth(text);
    }

    public void drawString(int x, int y, float z, int color, String text)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, 0f, z + 0.05f);

        this.textRenderer.drawString(text, x, y, color);

        GlStateManager.popMatrix();
    }

    public void drawCenteredString(int x, int y, float z, int color, String text)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, 0f, z + 0.05f);

        this.textRenderer.drawString(text, x - this.getStringWidth(text) / 2, y, color);

        GlStateManager.popMatrix();
    }

    public void drawStringWithShadow(int x, int y, float z, int color, String text)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, 0f, z + 0.05f);

        this.textRenderer.drawStringWithShadow(text, x, y, color);

        GlStateManager.popMatrix();
    }

    public void drawCenteredStringWithShadow(int x, int y, float z, int color, String text)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, 0f, z + 0.05f);

        this.textRenderer.drawStringWithShadow(text, x - this.getStringWidth(text) / 2f, y, color);

        GlStateManager.popMatrix();
    }

    public TextRenderFunction getTextRenderer(boolean useTextShadow, boolean centered)
    {
        if (centered)
        {
            return useTextShadow ? this::drawCenteredStringWithShadow : this::drawCenteredString;
        }
        else
        {
            return useTextShadow ? this::drawStringWithShadow : this::drawString;
        }
    }

    public void renderDebug(int mouseX, int mouseY, boolean hovered, boolean renderAll, boolean infoAlways)
    {
        int x = this.getX();
        int y = this.getY();
        float z = this.getZLevel();
        int w = this.getWidth();
        int h = this.getHeight();

        if (hovered || renderAll)
        {
            renderDebugOutline(x, y, z, w, h, hovered);
        }

        if (hovered || infoAlways)
        {
            int posX = infoAlways ? x      : mouseX;
            int posY = infoAlways ? y - 12 : mouseY;
            addDebugText(posX, posY, x, y, z, w, h, this.getClass().getName());
        }
    }

    public static void renderDebugOutline(double x, double y, double z, double w, double h, boolean hovered)
    {
        int color = Color4f.getColorFromHue(lastDebugOutlineColorHue);
        lastDebugOutlineColorHue += 40;

        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >>  8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        float lineWidth = hovered ? 3f : 1.0f;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(lineWidth);

        double x1 = x -     lineWidth / 4;
        double x2 = x + w + lineWidth / 4;
        double y1 = y -     lineWidth / 4;
        double y2 = y + h + lineWidth / 4;
        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);

        buffer.pos(x1, y1, z).color(r, g, b, a).endVertex();
        buffer.pos(x1, y2, z).color(r, g, b, a).endVertex();
        buffer.pos(x2, y2, z).color(r, g, b, a).endVertex();
        buffer.pos(x2, y1, z).color(r, g, b, a).endVertex();

        tessellator.draw();

        GlStateManager.enableTexture2D();
    }

    public static void addDebugText(int mouseX, int mouseY, int x, int y, double z, int w, int h, String text)
    {
        String str = String.format("§7x: §6%d ... %d§7, y: §6%d ... %d§7, z: §d%.1f§7 w: §a%d§7, h: §a%d§7 - §3%s", x, x + w - 1, y, y + h - 1, z, w, h, text);
        int posY = mouseY - 2;

        Long posLong = (long) posY << 32 | (long) mouseX;
        DEBUG_STRINGS.put(posLong, str);
    }

    public static void renderDebugTextAndClear()
    {
        if (DEBUG_STRINGS.isEmpty() == false)
        {
            for (Long posLong : DEBUG_STRINGS.keySet())
            {
                int x = (int) posLong.longValue();
                int y = (int) (posLong.longValue() >>> 32);
                RenderUtils.renderHoverText(x, y, 10, DEBUG_STRINGS.get(posLong), 0xFFFF4040, DEBUG_TEXT_BG_RENDERER);
            }

            DEBUG_STRINGS.clear();
            RenderUtils.disableItemLighting();
        }

        lastDebugOutlineColorHue = 0;
    }
}
