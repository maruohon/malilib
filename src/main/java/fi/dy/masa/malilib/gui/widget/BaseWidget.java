package fi.dy.masa.malilib.gui.widget;

import org.lwjgl.opengl.GL11;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.gui.position.EdgeInt;
import fi.dy.masa.malilib.render.RectangleRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.TextRenderUtils;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextRenderer;
import fi.dy.masa.malilib.util.data.Color4f;

public class BaseWidget
{
    public static final ImmutableList<String> EMPTY_STRING_LIST = ImmutableList.of();
    public static final RectangleRenderer DEBUG_TEXT_BG_RENDERER = (x, y, z, w, h) -> ShapeRenderUtils.renderOutlinedRectangle(x - 3, y - 3, z, w + 6, h + 6, 0xE0000000, 0xFFC0C0C0);

    private static final ArrayListMultimap<Long, String> DEBUG_STRINGS = ArrayListMultimap.create();
    private static int lastDebugOutlineColorHue;

    protected final Minecraft mc;
    protected final TextRenderer textRenderer;
    protected final EdgeInt margin = new EdgeInt();
    protected final EdgeInt padding = new EdgeInt();
    protected final int fontHeight;
    private int x;
    private int y;
    private int height;
    private int width;
    private int xRight;
    private float zLevel;
    private boolean keepOnScreen;
    private boolean rightAlign;
    protected boolean automaticHeight;
    protected boolean automaticWidth;
    protected boolean hasMaxHeight;
    protected boolean hasMaxWidth;
    protected int lineHeight;
    protected int maxHeight;
    protected int maxWidth;

    public BaseWidget(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.mc = Minecraft.getMinecraft();
        this.textRenderer = TextRenderer.INSTANCE;
        this.fontHeight = this.textRenderer.getFontHeight();
        this.lineHeight = this.fontHeight + 2;
        this.padding.setChangeListener(this::updateSize);

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

    public EdgeInt getMargin()
    {
        return this.margin;
    }

    public EdgeInt getPadding()
    {
        return this.padding;
    }

    public void setMargin(EdgeInt margin)
    {
        this.margin.setFrom(margin);
    }

    public void setMargin(int top, int right, int bottom, int left)
    {
        this.margin.setAll(top, right, bottom, left);
    }

    public void setPadding(EdgeInt padding)
    {
        this.padding.setFrom(padding);
    }

    public void setPadding(int top, int right, int bottom, int left)
    {
        this.padding.setAll(top, right, bottom, left);
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

    public void updateSize()
    {
        this.updateWidth();
        this.updateHeight();
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

    public int getLineHeight()
    {
        return this.lineHeight;
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

    public void setLineHeight(int lineHeight)
    {
        this.lineHeight = lineHeight;
    }

    public int getMaxWidth()
    {
        return this.maxWidth;
    }

    public int getMaxHeight()
    {
        return this.maxHeight;
    }

    public void setMaxWidth(int maxWidth)
    {
        this.maxWidth = maxWidth;
        this.hasMaxWidth = maxWidth > 0;
    }

    public void setMaxHeight(int maxHeight)
    {
        this.maxHeight = maxHeight;
        this.hasMaxHeight = maxHeight > 0;
    }

    public void setAutomaticWidth(boolean automaticWidth)
    {
        this.automaticWidth = automaticWidth;
    }

    public void setAutomaticHeight(boolean automaticHeight)
    {
        this.automaticHeight = automaticHeight;
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

    public int getFontHeight()
    {
        return this.fontHeight;
    }

    protected int getCenteredTextOffsetY()
    {
        return (this.getHeight() - this.fontHeight) / 2 + 1;
    }

    public void bindTexture(ResourceLocation texture)
    {
        RenderUtils.bindTexture(texture);
    }

    public int getStringWidth(String str)
    {
        return this.textRenderer.getStringWidth(str);
    }

    public int getRawStringWidth(String str)
    {
        return StyledTextLine.raw(str).renderWidth;
    }

    public void renderTextLine(int x, int y, float z, int defaultColor, boolean shadow, StyledTextLine text)
    {
        this.textRenderer.renderLine(x, y, z, defaultColor, shadow, text);
    }

    /**
     * Renders a plain string, by converting it to a StyledTextLine.<br>
     * <b>Note:</b> It's discouraged to use this method, if it's possible to easily
     * generate and cache the resulting StyledTextLine directly.
     */
    public void renderPlainString(int x, int y, float z, int color, boolean shadow, String str)
    {
        this.textRenderer.renderLine(x, y, z, color, shadow, StyledTextLine.of(str));
    }

    public void renderDebug(int mouseX, int mouseY, boolean hovered, boolean renderAll, boolean infoAlways)
    {
        int x = this.getX();
        int y = this.getY();
        float z = this.getZLevel();

        this.renderDebug(x, y, z, mouseX, mouseY, hovered, renderAll, infoAlways);
    }

    public void renderDebug(int x, int y, float z, int mouseX, int mouseY, boolean hovered, boolean renderAll, boolean infoAlways)
    {
        int w = this.getWidth();
        int h = this.getHeight();

        if (hovered || renderAll)
        {
            renderDebugOutline(x, y, z, w, h, hovered);

            if (this.padding.isEmpty() == false)
            {
                int left = this.padding.getLeft();
                int top = this.padding.getTop();
                int right = this.padding.getRight();
                int bottom = this.padding.getBottom();

                renderDebugOutline(x + left, y + top, z, w - left - right, h - top - bottom, false, 0xFFFFFFFF);
            }
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

        renderDebugOutline(x, y, z, w, h, hovered, color);
    }

    public static void renderDebugOutline(double x, double y, double z, double w, double h, boolean hovered, int color)
    {
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >>  8 & 255) / 255.0F;
        float b = (float) (color       & 255) / 255.0F;
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
        String str = String.format("§7x: §6%d ... %d§7, y: §6%d ... %d§7, z: §d%.1f§7 w: §a%d§7, h: §a%d§7 - §3%s",
                                   x, x + w - 1, y, y + h - 1, z, w, h, text);
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
                TextRenderUtils.renderHoverText(x, y, 10, DEBUG_STRINGS.get(posLong), 0xFFFF4040, DEBUG_TEXT_BG_RENDERER);
            }

            DEBUG_STRINGS.clear();
            RenderUtils.disableItemLighting();
        }

        lastDebugOutlineColorHue = 0;
    }
}
