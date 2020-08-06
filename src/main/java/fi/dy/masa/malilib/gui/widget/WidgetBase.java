package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.opengl.GL11;
import com.google.common.collect.ArrayListMultimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.gui.interfaces.IBackgroundRenderer;
import fi.dy.masa.malilib.gui.interfaces.ITextRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.Color4f;

public abstract class WidgetBase
{
    private static final ArrayListMultimap<Long, String> DEBUG_STRINGS = ArrayListMultimap.create();
    private static int lastDebugOutlineColorHue;
    private static int nextWidgetId;

    public static final IBackgroundRenderer DEBUG_TEXT_BG_RENDERER = (x, y, w, h, z) -> { RenderUtils.drawOutlinedBox(x - 3, y - 3, w + 6, h + 6, 0xE0000000, 0xFFC0C0C0, z); };

    protected final Minecraft mc;
    protected final FontRenderer textRenderer;
    protected final List<String> hoverStrings = new ArrayList<>();
    protected final int fontHeight;
    private final int id;
    private int x;
    private int y;
    private int xRight;
    private int width;
    private int height;
    private int zLevel;
    private boolean keepOnScreen;
    private boolean rightAlign;
    protected boolean automaticHeight;
    protected boolean automaticWidth;

    public WidgetBase(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.mc = Minecraft.getMinecraft();
        this.textRenderer = this.mc.fontRenderer;
        this.fontHeight = this.textRenderer.FONT_HEIGHT;
        this.id = nextWidgetId++;

        if (width < 0)
        {
            this.automaticWidth = true;
        }

        if (height < 0)
        {
            this.automaticHeight = true;
        }
    }

    public final int getX()
    {
        return this.x;
    }

    public final int getY()
    {
        return this.y;
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

    public final void setPositionAndSize(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.onPositionOrSizeChanged();
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
        this.onPositionOrSizeChanged();
    }

    /**
     * This method is called after the widget size is changed
     */
    protected void onSizeChanged()
    {
        this.onPositionOrSizeChanged();
    }

    /**
     * This method is called after either the widget position or size is changed.
     * This is meant for cases where it's necessary or beneficial to avoid the
     * calls for both size and position changes separately.
     */
    protected void onPositionOrSizeChanged()
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

    public void updateWidth()
    {
    }

    public void updateHeight()
    {
    }

    public int getZLevel()
    {
        return this.zLevel;
    }

    /**
     * Returns the unique(-ish) ID of this widget.
     * The ID is increment by one for each widget that is created (starting from 0 for each game launch).
     * This ID is mainly meant for things like identifying the top-most hovered widget.
     * @return
     */
    public int getId()
    {
        return this.id;
    }

    public WidgetBase setZLevel(int zLevel)
    {
        this.zLevel = zLevel;
        return this;
    }

    public WidgetBase setZLevelBasedOnParent(int parentZLevel)
    {
        this.setZLevel(parentZLevel + this.getSubWidgetZLevelIncrement());
        return this;
    }

    /**
     * This method is called whenever a widget gets added to its parent widget or GUI.
     * By default it updates the widget's own rendering Z-level based on the parent's Z-level.
     * @param parentZLevel
     * @return
     */
    public WidgetBase onWidgetAdded(int parentZLevel)
    {
        this.setZLevelBasedOnParent(parentZLevel);
        return this;
    }

    protected int getSubWidgetZLevelIncrement()
    {
        return 4;
    }

    protected int getCenteredTextOffsetY()
    {
        return (this.getHeight() - this.fontHeight) / 2 + 1;
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        int x = this.getX();
        int y = this.getY();

        return mouseX >= x && mouseX < x + this.getWidth() &&
               mouseY >= y && mouseY < y + this.getHeight();
    }

    public boolean isHoveredForRender(int mouseX, int mouseY)
    {
        return this.isMouseOver(mouseX, mouseY);
    }

    public boolean getShouldReceiveOutsideClicks()
    {
        return false;
    }

    public boolean getShouldReceiveOutsideScrolls()
    {
        return false;
    }

    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isMouseOver(mouseX, mouseY) || this.getShouldReceiveOutsideClicks())
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
        if (this.isMouseOver(mouseX, mouseY) || this.getShouldReceiveOutsideScrolls())
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
     * Returns true if this widget can be hovered (for hover info etc.) at the given point
     */
    public boolean canHoverAt(int mouseX, int mouseY, int mouseButton)
    {
        return true;
    }

    public boolean hasHoverText()
    {
        return this.hoverStrings.isEmpty() == false;
    }

    public void clearHoverStrings()
    {
        this.hoverStrings.clear();
    }

    public void addHoverStrings(String... hoverStrings)
    {
        this.addHoverStrings(Arrays.asList(hoverStrings));
    }

    public void addHoverStrings(List<String> hoverStrings)
    {
        for (String str : hoverStrings)
        {
            this.addHoverString(str);
        }
    }

    public void addHoverString(@Nullable String translationKey, Object... args)
    {
        if (translationKey != null)
        {
            String str = StringUtils.translate(translationKey, args);

            String[] parts = str.split("\\n");

            for (String part : parts)
            {
                this.hoverStrings.add(part);
            }
        }
    }

    public List<WidgetTextFieldBase> getAllTextFields()
    {
        return Collections.emptyList();
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
        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, 0f, this.getZLevel() + 0.1f);

        this.textRenderer.drawString(text, x, y, color);

        GlStateManager.popMatrix();
    }

    public void drawCenteredString(int x, int y, int color, String text)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, 0f, this.getZLevel() + 0.1f);

        this.textRenderer.drawString(text, x - this.getStringWidth(text) / 2, y, color);

        GlStateManager.popMatrix();
    }

    public void drawStringWithShadow(int x, int y, int color, String text)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, 0f, this.getZLevel() + 0.1f);

        this.textRenderer.drawStringWithShadow(text, x, y, color);

        GlStateManager.popMatrix();
    }

    public void drawCenteredStringWithShadow(int x, int y, int color, String text)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, 0f, this.getZLevel() + 0.1f);

        this.textRenderer.drawStringWithShadow(text, x - this.getStringWidth(text) / 2, y, color);

        GlStateManager.popMatrix();
    }

    public ITextRenderer getTextRenderer(boolean useTextShadow, boolean centered)
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

    public void render(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        this.render(mouseX, mouseY, isActiveGui, this.id == hoveredWidgetId);
    }

    public void render(int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
    }

    public boolean shouldRenderHoverInfo(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        return this.getId() == hoveredWidgetId && this.canHoverAt(mouseX, mouseY, 0);
    }

    public void postRenderHovered(int mouseX, int mouseY, boolean isActiveGui, int hoveredWidgetId)
    {
        if (this.hasHoverText() && this.shouldRenderHoverInfo(mouseX, mouseY, isActiveGui, hoveredWidgetId))
        {
            RenderUtils.drawHoverText(mouseX, mouseY, this.getZLevel(), this.getHoverStrings());
            RenderUtils.disableItemLighting();
        }
    }

    @Nullable
    public WidgetBase getTopHoveredWidget(int mouseX, int mouseY, @Nullable WidgetBase highestFoundWidget)
    {
        if (this.isHoveredForRender(mouseX, mouseY) &&
            (highestFoundWidget == null || this.getZLevel() > highestFoundWidget.getZLevel()))
        {
            return this;
        }

        return highestFoundWidget;
    }

    @Nullable
    public static WidgetBase getTopHoveredWidgetFromList(List<? extends WidgetBase> widgets, int mouseX, int mouseY, @Nullable WidgetBase highestFoundWidget)
    {
        if (widgets.isEmpty() == false)
        {
            for (WidgetBase widget : widgets)
            {
                highestFoundWidget = widget.getTopHoveredWidget(mouseX, mouseY, highestFoundWidget);
            }
        }

        return highestFoundWidget;
    }

    public void renderDebug(int mouseX, int mouseY, boolean hovered, boolean renderAll, boolean infoAlways)
    {
        int x = this.getX();
        int y = this.getY();
        int z = this.getZLevel();
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

        Long posLong = Long.valueOf((long) posY << 32 | (long) mouseX);
        DEBUG_STRINGS.put(posLong, str);
    }

    public static void renderDebugTextAndClear()
    {
        if (DEBUG_STRINGS.isEmpty() == false)
        {
            for (Long posLong : DEBUG_STRINGS.keySet())
            {
                int x = (int) (posLong.longValue() & 0xFFFFFFFF);
                int y = (int) ((posLong.longValue() >>> 32) & 0xFFFFFFFF);
                RenderUtils.drawHoverText(x, y, 10, DEBUG_STRINGS.get(posLong), 0xFFFF4040, DEBUG_TEXT_BG_RENDERER);
            }

            DEBUG_STRINGS.clear();
            RenderUtils.disableItemLighting();
        }

        lastDebugOutlineColorHue = 0;
    }
}
