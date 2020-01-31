package fi.dy.masa.malilib.gui.widgets;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
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

public abstract class WidgetBase
{
    private static final ArrayListMultimap<Long, String> DEBUG_STRINGS = ArrayListMultimap.create();
    private static int lastDebugOutlineColor;

    public static final IBackgroundRenderer DEBUG_TEXT_BG_RENDERER = (x, y, w, h, z) -> { RenderUtils.drawOutlinedBox(x - 2, y - 2, w + 4, h + 4, 0xE0000000, 0xFFC0C0C0, z); };

    protected final Minecraft mc;
    protected final FontRenderer textRenderer;
    protected final List<String> hoverStrings = new ArrayList<>();
    protected final int fontHeight;
    private int x;
    private int y;
    private int xRight;
    private int width;
    private int height;
    private int zLevel;
    private boolean rightAlign;

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

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public int getZLevel()
    {
        return this.zLevel;
    }

    public WidgetBase setZLevel(int zLevel)
    {
        this.zLevel = zLevel;
        return this;
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

    public void setPosition(int x, int y)
    {
        this.setX(x);
        this.setY(y);
    }

    public void setRightX(int x)
    {
        this.xRight = x;
        this.updatePositionIfRightAligned();
    }

    public void setRightAlign(boolean rightAlign, int xRight)
    {
        this.rightAlign = rightAlign;

        if (rightAlign)
        {
            this.setRightX(xRight);
        }
    }

    protected void updatePositionIfRightAligned()
    {
        if (this.rightAlign)
        {
            this.x = this.xRight - this.width;

            if (this.x < 0)
            {
                this.xRight += -this.x + 4;
                this.x = 4;
            }
        }
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
     * Returns true if this widget can be selected by clicking at the given point
     */
    public boolean canSelectAt(int mouseX, int mouseY, int mouseButton)
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

            String[] parts = str.split("\\\\n");

            for (String part : parts)
            {
                this.hoverStrings.add(part);
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
        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, 0f, this.zLevel + 0.1f);

        this.textRenderer.drawString(text, x, y, color);

        GlStateManager.popMatrix();
    }

    public void drawCenteredString(int x, int y, int color, String text)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, 0f, this.zLevel + 0.1f);

        this.textRenderer.drawString(text, x - this.getStringWidth(text) / 2, y, color);

        GlStateManager.popMatrix();
    }

    public void drawStringWithShadow(int x, int y, int color, String text)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, 0f, this.zLevel + 0.1f);

        this.textRenderer.drawStringWithShadow(text, x, y, color);

        GlStateManager.popMatrix();
    }

    public void drawCenteredStringWithShadow(int x, int y, int color, String text)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, 0f, this.zLevel + 0.1f);

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

    public void render(int mouseX, int mouseY, boolean selected)
    {
    }

    public boolean shouldRenderHoverInfo(int mouseX, int mouseY)
    {
        return this.canSelectAt(mouseX, mouseY, 0);
    }

    public void postRenderHovered(int mouseX, int mouseY, boolean selected)
    {
        if (this.hasHoverText() && this.shouldRenderHoverInfo(mouseX, mouseY))
        {
            RenderUtils.drawHoverText(mouseX, mouseY, this.getZLevel(), this.getHoverStrings());
            RenderUtils.disableItemLighting();
        }
    }

    public void renderDebug(int mouseX, int mouseY, boolean hovered, boolean renderAll, boolean infoAlways)
    {
        int x = this.getX();
        int y = this.getY();
        double z = this.getZLevel();
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
        int color = 0xFF000000 | (Color.HSBtoRGB((float) (lastDebugOutlineColor % 360) / 360f, 1f, 1f) & 0x00FFFFFF);
        lastDebugOutlineColor += 40;

        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >>  8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        float lineWidth = hovered ? 3f : 1.0f;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(lineWidth);

        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);

        buffer.pos(x    , y    , z).color(r, g, b, a).endVertex();
        buffer.pos(x    , y + h, z).color(r, g, b, a).endVertex();
        buffer.pos(x + w, y + h, z).color(r, g, b, a).endVertex();
        buffer.pos(x + w, y    , z).color(r, g, b, a).endVertex();

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

        lastDebugOutlineColor = 0;
    }
}
