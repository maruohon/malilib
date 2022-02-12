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
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.util.ElementOffset;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.render.RectangleRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.render.TextRenderUtils;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextRenderSettings;
import fi.dy.masa.malilib.render.text.TextRenderer;
import fi.dy.masa.malilib.render.text.TextStyle;
import fi.dy.masa.malilib.util.GameUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.data.EdgeInt;

public class BaseWidget
{
    public static final ImmutableList<String> EMPTY_STRING_LIST = ImmutableList.of();
    public static final RectangleRenderer DEBUG_TEXT_BG_RENDERER = (x, y, z, w, h) -> ShapeRenderUtils.renderOutlinedRectangle(x - 3, y - 3, z, w + 6, h + 6, 0xE0000000, 0xFFC0C0C0);

    private static final ArrayListMultimap<Long, String> DEBUG_STRINGS = ArrayListMultimap.create();
    private static int lastDebugOutlineColorHue;
    private static int nextWidgetId;

    protected final Minecraft mc = GameUtils.getClient();
    protected final EdgeInt margin = new EdgeInt();
    protected final EdgeInt padding = new EdgeInt();
    protected final ElementOffset iconOffset = new ElementOffset();
    protected final ElementOffset textOffset = new ElementOffset();

    @Nullable protected Icon icon;
    @Nullable protected StyledTextLine text;
    protected TextRenderer textRenderer;
    protected boolean automaticHeight;
    protected boolean automaticWidth;
    protected int maxHeight;
    protected int maxWidth;

    private final TextRenderSettings textSettings = new TextRenderSettings();
    private final int id;
    private int x;
    private int y;
    private float z;
    private int height;
    private int width;

    public BaseWidget()
    {
        this(0, 0, -1, -1);
    }

    public BaseWidget(int width, int height)
    {
        this(0, 0, width, height);
    }

    public BaseWidget(int x, int y, int width, int height)
    {
        this.id = nextWidgetId++;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textRenderer = TextRenderer.INSTANCE;
        this.padding.setChangeListener(this::updateSize);
        this.textOffset.setXOffset(4);

        this.automaticWidth = width < 0;
        this.automaticHeight = height < 0;

        this.setMaxWidth(width < -1 ? -width : width);
        this.setMaxHeight(height < -1 ? -height : height);
    }

    /**
     * Returns the unique(-ish) ID of this widget.
     * The ID is increment by one for each widget that is created (starting from 0 for each game launch).
     * This ID is mainly meant for things like identifying the top-most hovered widget.
     */
    public int getId()
    {
        return this.id;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public float getZ()
    {
        return this.z;
    }

    public int getRight()
    {
        return this.getX() + this.getWidth();
    }

    public int getBottom()
    {
        return this.getY() + this.getHeight();
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public int getMaxWidth()
    {
        return this.maxWidth;
    }

    public int getMaxHeight()
    {
        return this.maxHeight;
    }

    public boolean hasMaxWidth()
    {
        return this.maxWidth > 0;
    }

    public boolean hasMaxHeight()
    {
        return this.maxHeight > 0;
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

    public void setZ(float z)
    {
        this.z = z;
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

    public void setWidth(int width)
    {
        this.width = width;
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
        this.onSizeChanged();
    }

    /**
     * Sets the maximum width, and enables the maximum width if the value is > 0
     */
    public void setMaxWidth(int maxWidth)
    {
        this.maxWidth = maxWidth;
    }

    /**
     * Sets the maximum height, and enables the maximum height if the value is > 0
     */
    public void setMaxHeight(int maxHeight)
    {
        this.maxHeight = maxHeight;
    }

    public void setAutomaticWidth(boolean automaticWidth)
    {
        this.automaticWidth = automaticWidth;
    }

    public void setAutomaticHeight(boolean automaticHeight)
    {
        this.automaticHeight = automaticHeight;
    }

    public void centerVerticallyInside(BaseWidget containerWidget)
    {
        this.centerVerticallyInside(containerWidget, 0);
    }

    public void centerVerticallyInside(BaseWidget containerWidget, int offset)
    {
        int yOffset = (containerWidget.getHeight() - this.getHeight()) / 2 + offset;
        this.setY(containerWidget.getY() + yOffset);
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
     * @param oldX the x position before the position was changed
     * @param oldY the y position before the position was changed
     */
    protected void onPositionOrSizeChanged(int oldX, int oldY)
    {
    }

    /**
     * This is called when the container widget or screen
     * changes its geometry, in case this widget wants to somehow
     * react to that change and maybe update its sub widget positions.
     */
    public void onContainerGeometryChanged()
    {
    }

    public void updateSize()
    {
        this.updateWidth();
        this.updateHeight();
    }

    public void updateWidth()
    {
    }

    public void updateHeight()
    {
    }

    public void clampToScreen()
    {
        int screenRight = GuiUtils.getScaledWindowWidth();
        int screenBottom = GuiUtils.getScaledWindowHeight();
        int x = this.getX();
        int y = this.getY();

        if (this.getRight() > screenRight)
        {
            x = screenRight - this.getWidth() - 2;
        }

        if (this.getBottom() > screenBottom)
        {
            y = screenBottom - this.getHeight() - 2;
        }

        x = Math.max(x, 0);
        y = Math.max(y, 0);

        this.setPosition(x, y);
    }

    public boolean intersects(EdgeInt rectangle)
    {
        return this.getX()      <= rectangle.getRight() &&
               this.getRight()  >= rectangle.getLeft() &&
               this.getY()      <= rectangle.getBottom() &&
               this.getBottom() >= rectangle.getTop();
    }

    public void setLineHeight(int lineHeight)
    {
        this.getTextSettings().setLineHeight(lineHeight);
    }

    public void setZLevelBasedOnParent(float parentZLevel)
    {
        this.setZ(parentZLevel + this.getSubWidgetZLevelIncrement());
    }

    /**
     * This method is called whenever a widget gets added to its parent widget or GUI.
     * By default, it updates the widget's own rendering Z-level based on the parent's Z-level.
     */
    public void onWidgetAdded(float parentZLevel)
    {
        this.setZLevelBasedOnParent(parentZLevel);
    }

    protected int getSubWidgetZLevelIncrement()
    {
        return 2;
    }

    public boolean canInteract()
    {
        return false;
    }

    public EdgeInt getMargin()
    {
        return this.margin;
    }

    public EdgeInt getPadding()
    {
        return this.padding;
    }

    public ElementOffset getIconOffset()
    {
        return this.iconOffset;
    }

    public ElementOffset getTextOffset()
    {
        return this.textOffset;
    }

    public TextRenderSettings getTextSettings()
    {
        return this.textSettings;
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
        this.textOffset.setXOffset(textOffsetX);
        this.textOffset.setYOffset(textOffsetY);
    }

    /**
     * Sets the starting style for the text. Note that the text must have been set already,
     * otherwise this does nothing.
     * @param style the new starting style for the text, to which any styles defined in the text will be merged to
     */
    public void setStartingStyleForText(TextStyle style)
    {
        if (this.text != null)
        {
            this.setText(this.text.withStartingStyle(style));
        }
    }

    @Nullable
    public Icon getIcon()
    {
        return this.icon;
    }

    /**
     * Sets a simple icon to be rendered in the widget,
     * without having to add a nested IconWidget or per-widget code.
     */
    public void setIcon(@Nullable Icon icon)
    {
        this.icon = icon;
    }

    public void bindTexture(ResourceLocation texture)
    {
        RenderUtils.bindTexture(texture);
    }

    public int getLineHeight()
    {
        return this.getTextSettings().getLineHeight();
    }

    public int getFontHeight()
    {
        return this.textRenderer.getFontHeight();
    }

    public int getStringWidth(String str)
    {
        return this.textRenderer.getStringWidth(str);
    }

    public int getRawStyledTextWidth(String str)
    {
        return StyledTextLine.raw(str).renderWidth;
    }

    protected int getTextPositionX(int x, int textWidth)
    {
        return this.textOffset.getElementPositionX(x, this.getWidth(), textWidth);
    }

    protected int getTextPositionY(int y)
    {
        return this.textOffset.getElementPositionY(y, this.getHeight(), this.getFontHeight());
    }

    protected int getIconPositionX(int x, int iconWidth)
    {
        return this.iconOffset.getElementPositionX(x, this.getWidth(), iconWidth);
    }

    protected int getIconPositionY(int y, int iconHeight)
    {
        return this.iconOffset.getElementPositionY(y, this.getHeight(), iconHeight);
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
            int color = this.getTextSettings().getTextColor();
            boolean shadow = this.getTextSettings().getTextShadowEnabled();

            this.renderTextLine(x, y, z + 0.0125f, color, shadow, ctx, this.text);
        }
    }

    protected void renderIcon(int x, int y, float z, boolean enabled, ScreenContext ctx)
    {
        if (this.icon != null)
        {
            x = this.getIconPositionX(x, this.icon.getWidth());
            y = this.getIconPositionY(y, this.icon.getHeight());

            this.icon.renderAt(x, y, z + 0.0125f, enabled, false);
        }
    }

    public void renderAt(int x, int y, float z, ScreenContext ctx)
    {
        this.renderIcon(x, y, z, true, ctx);
        this.renderText(x, y, z, ctx);
    }

    public void renderDebug(boolean hovered, ScreenContext ctx)
    {
        int x = this.getX();
        int y = this.getY();
        float z = this.getZ();

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
