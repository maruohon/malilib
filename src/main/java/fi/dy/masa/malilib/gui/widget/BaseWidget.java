package fi.dy.masa.malilib.gui.widget;

import javax.annotation.Nullable;
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
    @Nullable protected StyledTextLine text;
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
    protected boolean centerTextHorizontally;
    protected boolean centerTextVertically = true;
    protected boolean hasMaxHeight;
    protected boolean hasMaxWidth;
    protected boolean textShadow = true;
    protected int lineHeight;
    protected int maxHeight;
    protected int maxWidth;
    protected int defaultTextColor = 0xFFFFFFFF;
    protected int textOffsetX = 4;
    protected int textOffsetY;

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

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public int getRight()
    {
        return this.getX() + this.getWidth();
    }

    public int getBottom()
    {
        return this.getY() + this.getHeight();
    }

    public void setX(int x)
    {
        int oldX = this.x;
        int oldY = this.y;

        this.x = x;

        this.onPositionChanged(oldX, oldY);
    }

    public void setY(int y)
    {
        int oldX = this.x;
        int oldY = this.y;

        this.y = y;

        this.onPositionChanged(oldX, oldY);
    }

    public void setPosition(int x, int y)
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

    public void setPositionAndSize(int x, int y, int width, int height)
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

    public void setRight(int xRight)
    {
        int oldX = this.x;
        int oldY = this.y;

        this.x = xRight - this.width;

        this.onPositionChanged(oldX, oldY);
    }

    public void setBottom(int yBottom)
    {
        int oldX = this.x;
        int oldY = this.y;

        this.y = yBottom - this.height;

        this.onPositionChanged(oldX, oldY);
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

    public boolean intersects(EdgeInt rectangle)
    {
        return this.getX() <= rectangle.getRight() &&
               this.getRight() >= rectangle.getLeft() &&
               this.getY() <= rectangle.getBottom() &&
               this.getBottom() >= rectangle.getTop();
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

    public boolean getCenterTextHorizontally()
    {
        return this.centerTextHorizontally;
    }

    public boolean getCenterTextVertically()
    {
        return this.centerTextVertically;
    }

    public int getDefaultTextColor()
    {
        return this.defaultTextColor;
    }

    public int getTextOffsetX()
    {
        return this.textOffsetX;
    }

    public int getTextOffsetY()
    {
        return this.textOffsetY;
    }

    public void setCenterTextHorizontally(boolean centerTextHorizontally)
    {
        this.centerTextHorizontally = centerTextHorizontally;
    }

    public void setCenterTextVertically(boolean centerTextVertically)
    {
        this.centerTextVertically = centerTextVertically;
    }

    public void setDefaultTextColor(int defaultTextColor)
    {
        this.defaultTextColor = defaultTextColor;
    }

    public void setTextOffsetX(int textOffsetX)
    {
        this.textOffsetX = textOffsetX;
    }

    public void setTextOffsetY(int textOffsetY)
    {
        this.textOffsetY = textOffsetY;
    }

    /**
     * Sets a simple single-line text to be rendered in the widget,
     * without having to add a LabelWidget for it.
     */
    public void setText(@Nullable StyledTextLine text)
    {
        this.text = text;
    }

    /**
     * Sets a simple single-line text to be rendered in the widget,
     * without having to add a LabelWidget for it.
     * @param textOffsetX an x offset for the text. By default this is 4 pixels from the left edge.
     * @param textOffsetY an y offset for the text. Note: The text is by default already centered vertically,
     *                    this is an additional offset on top of that!
     */
    public void setText(@Nullable StyledTextLine text, int textOffsetX, int textOffsetY)
    {
        this.text = text;
        this.textOffsetX = textOffsetX;
        this.textOffsetY = textOffsetY;
    }

    public int getFontHeight()
    {
        return this.fontHeight;
    }

    protected int getCenteredElementOffsetX(int elementWidth)
    {
        return (this.getWidth() - elementWidth) / 2;
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

    protected int getTextPositionX(int x, int textWidth)
    {
        int position = x + this.textOffsetX;

        if (this.centerTextHorizontally)
        {
            position += this.getCenteredElementOffsetX(textWidth);
        }

        return position;
    }

    protected int getTextPositionY(int y)
    {
        int position = y + this.textOffsetY;

        if (this.centerTextVertically)
        {
            position += this.getCenteredTextOffsetY();
        }

        return position;
    }

    public void renderTextLine(int x, int y, float z, int defaultColor, boolean shadow,
                               ScreenContext ctx, StyledTextLine text)
    {
        this.textRenderer.renderLine(x, y, z, defaultColor, shadow, text);
    }

    /**
     * Renders a plain string, by converting it to a StyledTextLine.<br>
     * <b>Note:</b> It's discouraged to use this method, if it's possible to easily
     * generate and cache the resulting StyledTextLine directly.
     */
    public void renderPlainString(int x, int y, float z, int color, boolean shadow, String str, ScreenContext ctx)
    {
        this.textRenderer.renderLine(x, y, z, color, shadow, StyledTextLine.of(str));
    }

    protected void renderText(int x, int y, float z, ScreenContext ctx)
    {
        if (this.text != null)
        {
            x = this.getTextPositionX(x, this.text.renderWidth);
            y = this.getTextPositionY(y);

            this.renderTextLine(x, y, z + 0.1f, this.defaultTextColor, this.textShadow, ctx, this.text);
        }
    }

    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        this.renderText(x, y, z, ctx);
    }

    public void renderDebug(boolean hovered, ScreenContext ctx)
    {
        int x = this.getX();
        int y = this.getY();
        float z = this.getZLevel();

        this.renderDebug(x, y, z, hovered, ctx);
    }

    public void renderDebug(int x, int y, float z, boolean hovered, ScreenContext ctx)
    {
        int w = this.getWidth();
        int h = this.getHeight();

        if (hovered || ctx.debugRenderAll)
        {
            renderDebugOutline(x, y, z, w, h, hovered, ctx);

            if (this.padding.isEmpty() == false)
            {
                int left = this.padding.getLeft();
                int top = this.padding.getTop();
                int right = this.padding.getRight();
                int bottom = this.padding.getBottom();

                renderDebugOutline(x + left, y + top, z, w - left - right, h - top - bottom, false, 0xFFFFFFFF, ctx);
            }
        }

        if (hovered || ctx.debugInfoAlways)
        {
            int posX = ctx.debugInfoAlways ? x      : ctx.mouseX;
            int posY = ctx.debugInfoAlways ? y - 12 : ctx.mouseY;
            addDebugText(posX, posY, x, y, z, w, h, this.getClass().getName());
        }
    }

    public static void renderDebugOutline(double x, double y, double z, double w, double h,
                                          boolean hovered, ScreenContext ctx)
    {
        int color = Color4f.getColorFromHue(lastDebugOutlineColorHue);
        lastDebugOutlineColorHue += 40;

        renderDebugOutline(x, y, z, w, h, hovered, color, ctx);
    }

    public static void renderDebugOutline(double x, double y, double z, double w, double h,
                                          boolean hovered, int color, ScreenContext ctx)
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

    public static void renderDebugTextAndClear(ScreenContext ctx)
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
