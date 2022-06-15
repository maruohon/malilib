package fi.dy.masa.malilib.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.Entity;
import fi.dy.masa.malilib.config.value.HudAlignment;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextRenderer;
import fi.dy.masa.malilib.util.game.wrap.EntityWrap;
import fi.dy.masa.malilib.util.game.wrap.GameUtils;
import fi.dy.masa.malilib.util.position.Vec2i;

public class TextRenderUtils
{
    public static void renderText(int x, int y, int color, String text)
    {
        String[] parts = text.split("\\\\n");
        FontRenderer textRenderer = GameUtils.getClient().fontRenderer;

        for (String line : parts)
        {
            textRenderer.drawString(line, x, y, color);
            y += textRenderer.FONT_HEIGHT + 1;
        }
    }

    public static void renderText(int x, int y, int color, List<String> lines)
    {
        if (lines.isEmpty() == false)
        {
            FontRenderer textRenderer = GameUtils.getClient().fontRenderer;

            for (String line : lines)
            {
                textRenderer.drawString(line, x, y, color);
                y += textRenderer.FONT_HEIGHT + 2;
            }
        }
    }

    public static int renderText(int xOff, int yOff, int z, double scale, int textColor, int bgColor,
                                 HudAlignment alignment, boolean useBackground, boolean useShadow, List<String> lines)
    {
        FontRenderer fontRenderer = GameUtils.getClient().fontRenderer;
        final int scaledWidth = GuiUtils.getScaledWindowWidth();
        final int lineHeight = fontRenderer.FONT_HEIGHT + 2;
        final int contentHeight = lines.size() * lineHeight - 2;
        int bgMargin = 2;

        // Only Chuck Norris can divide by zero
        if (scale == 0d)
        {
            return 0;
        }

        if (scale != 1d)
        {
            xOff = (int) (xOff * scale);
            yOff = (int) (yOff * scale);

            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, 0);
        }

        double posX = xOff + bgMargin;
        double posY = yOff + bgMargin;

        posY = GuiUtils.getHudPosY((int) posY, yOff, contentHeight, scale, alignment);
        posY += GuiUtils.getHudOffsetForPotions(alignment, scale, GameUtils.getClientPlayer());

        for (String line : lines)
        {
            final int width = fontRenderer.getStringWidth(line);

            if (alignment == HudAlignment.TOP_RIGHT || alignment == HudAlignment.BOTTOM_RIGHT)
            {
                posX = (scaledWidth / scale) - width - xOff - bgMargin;
            }
            else if (alignment == HudAlignment.CENTER)
            {
                posX = (scaledWidth / scale / 2) - (width / 2) - xOff;
            }

            final int x = (int) posX;
            final int y = (int) posY;
            posY += lineHeight;

            if (useBackground)
            {
                ShapeRenderUtils.renderRectangle(x - bgMargin, y - bgMargin, z, width + bgMargin, bgMargin + fontRenderer.FONT_HEIGHT, bgColor);
            }

            if (useShadow)
            {
                fontRenderer.drawStringWithShadow(line, x, y, textColor);
            }
            else
            {
                fontRenderer.drawString(line, x, y, textColor);
            }
        }

        if (scale != 1d)
        {
            GlStateManager.popMatrix();
        }

        return contentHeight + bgMargin * 2;
    }

    public static Vec2i getScreenClampedHoverTextStartPosition(int x, int y, int renderWidth, int renderHeight)
    {
        Screen screen = GuiUtils.getCurrentScreen();
        int maxWidth = screen != null ? screen.width : GuiUtils.getScaledWindowWidth();
        int maxHeight = screen != null ? screen.height : GuiUtils.getScaledWindowHeight();
        int textStartX = x;
        int textStartY = Math.max(0, y - renderHeight - 6);

        // The text can't fit from the cursor to the right edge of the screen
        if (textStartX + renderWidth > maxWidth)
        {
            int leftX = x - renderWidth - 2;

            // If the text fits from the cursor to the left edge of the screen...
            if (leftX >= 0)
            {
                textStartX = leftX;
            }
            // otherwise move it to touching the edge of the screen that the cursor is furthest from
            else
            {
                textStartX = x > (maxWidth / 2) ? 0 : Math.max(0, maxWidth - renderWidth - 1);
            }
        }

        // The hover info would overlap the cursor vertically
        // (because the hover info was clamped to the top of the screen),
        // move it below the cursor instead
        if (y >= textStartY && y < textStartY + renderHeight &&
            x >= textStartX && x < textStartX + renderWidth)
        {
            textStartY = y + 12;

            // Would clip at the bottom of the screen
            if (textStartY + renderHeight >= maxHeight)
            {
                textStartY = maxHeight - renderHeight;
            }
        }

        return new Vec2i(textStartX, textStartY);
    }

    public static void renderHoverText(int x, int y, float z, String text)
    {
        renderHoverText(x, y, z, Collections.singletonList(text));
    }

    public static void renderHoverText(int x, int y, float z, List<String> textLines)
    {
        renderHoverText(x, y, z, textLines, 0xFFC0C0C0 , TextRenderUtils::renderDefaultHoverTextBackground);
    }

    public static void renderHoverText(int x, int y, float z, List<String> textLines,
                                       int textColor, RectangleRenderer backgroundRenderer)
    {
        if (textLines.isEmpty() == false && GuiUtils.getCurrentScreen() != null)
        {
            List<String> linesNew = new ArrayList<>();
            FontRenderer font = GameUtils.getClient().fontRenderer;
            int maxLineLength = 0;

            for (String lineOrig : textLines)
            {
                String[] lines = lineOrig.split("\\\\n");

                for (String line : lines)
                {
                    int length = font.getStringWidth(line);

                    if (length > maxLineLength)
                    {
                        maxLineLength = length;
                    }

                    linesNew.add(line);
                }
            }

            textLines = linesNew;

            int lineHeight = font.FONT_HEIGHT + 1;
            int textHeight = textLines.size() * lineHeight - 2;
            int backgroundWidth = maxLineLength + 8;
            int backgroundHeight = textHeight + 8;
            Vec2i startPos = getScreenClampedHoverTextStartPosition(x, y, backgroundWidth, backgroundHeight);
            int textStartX = startPos.x + 4;
            int textStartY = startPos.y + 4;

            GlStateManager.disableRescaleNormal();
            RenderUtils.disableItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();

            backgroundRenderer.render(startPos.x, startPos.y, z, backgroundWidth, backgroundHeight);

            for (String str : textLines)
            {
                font.drawStringWithShadow(str, textStartX, textStartY, textColor);
                textStartY += lineHeight;
            }

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    public static void renderStyledHoverText(int x, int y, float z, List<StyledTextLine> textLines)
    {
        renderStyledHoverText(x, y, z, textLines, 0xFFB0B0B0 , TextRenderUtils::renderDefaultHoverTextBackground);
    }

    public static void renderStyledHoverText(int x, int y, float z, List<StyledTextLine> textLines,
                                             int textColor, RectangleRenderer backgroundRenderer)
    {
        if (textLines.isEmpty() == false && GuiUtils.getCurrentScreen() != null)
        {
            TextRenderer textRenderer = TextRenderer.INSTANCE;
            final int lineHeight = textRenderer.getLineHeight();
            int maxLineLength = StyledTextLine.getRenderWidth(textLines);
            int textHeight = textLines.size() * lineHeight - 2;
            int backgroundWidth = maxLineLength + 8;
            int backgroundHeight = textHeight + 8;
            Vec2i startPos = getScreenClampedHoverTextStartPosition(x, y, backgroundWidth, backgroundHeight);
            int textStartX = startPos.x + 4;
            int textStartY = startPos.y + 4;

            GlStateManager.disableRescaleNormal();
            RenderUtils.disableItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();

            backgroundRenderer.render(startPos.x, startPos.y, z, backgroundWidth, backgroundHeight);
            textRenderer.startBuffers();

            for (StyledTextLine line : textLines)
            {
                textRenderer.renderLineToBuffer(textStartX, textStartY, z, textColor, true, line);
                textStartY += lineHeight;
            }

            textRenderer.renderBuffers();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    public static void renderDefaultHoverTextBackground(int x, int y, float z, int width, int height)
    {
        int fillColor = 0xF0180018;
        int borderColor1 = 0xD02060FF;
        int borderColor2 = 0xC01030A0;

        renderHoverTextBackground(x, y, z, width, height, fillColor, borderColor1, borderColor2);
    }

    public static void renderHoverTextBackground(int x, int y, float z, int width, int height,
                                                 int fillColor, int borderColor1, int borderColor2)
    {
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        RenderUtils.setupBlend();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        int xl1 = x;
        int xl2 = xl1 + 1;
        int xl3 = xl2 + 1;
        int xr1 = x + width - 2;
        int xr2 = xr1 + 1;
        int xr3 = xr2 + 1;
        int yt1 = y;
        int yt2 = yt1 + 1;
        int yt3 = yt2 + 1;
        int yb1 = y + height - 2;
        int yb2 = yb1 + 1;
        int yb3 = yb2 + 1;

        ShapeRenderUtils.renderGradientRectangle(xl2, yt1, xr2, yt2, z, fillColor, fillColor, buffer);
        ShapeRenderUtils.renderGradientRectangle(xl2, yb2, xr2, yb3, z, fillColor, fillColor, buffer);
        ShapeRenderUtils.renderGradientRectangle(xl2, yt2, xr2, yb2, z, fillColor, fillColor, buffer);
        ShapeRenderUtils.renderGradientRectangle(xl1, yt2, xl2, yb2, z, fillColor, fillColor, buffer);
        ShapeRenderUtils.renderGradientRectangle(xr2, yt2, xr3, yb2, z, fillColor, fillColor, buffer);

        ShapeRenderUtils.renderGradientRectangle(xl2, yt3, xl3, yb1, z, borderColor1, borderColor2, buffer);
        ShapeRenderUtils.renderGradientRectangle(xr1, yt3, xr2, yb1, z, borderColor1, borderColor2, buffer);
        ShapeRenderUtils.renderGradientRectangle(xl2, yt2, xr2, yt3, z, borderColor1, borderColor1, buffer);
        ShapeRenderUtils.renderGradientRectangle(xl2, yb1, xr2, yb2, z, borderColor2, borderColor2, buffer);

        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    /**
     * Renders a text plate/billboard, similar to the player name plate.<br>
     * The plate will face towards the camera entity.
     */
    public static void renderTextPlate(List<String> text, double x, double y, double z, float scale)
    {
        Entity entity = GameUtils.getCameraEntity();

        if (entity != null)
        {
            renderTextPlate(text, x, y, z, EntityWrap.getYaw(entity), EntityWrap.getPitch(entity), scale, 0xFFFFFFFF, 0x40000000, true);
        }
    }

    /**
     * Renders a text plate/billboard, similar to the player name plate.<br>
     * The plate will face towards the given angle.
     */
    public static void renderTextPlate(List<String> text, double x, double y, double z, float yaw, float pitch,
                                       float scale, int textColor, int bgColor, boolean disableDepth)
    {
        FontRenderer textRenderer = GameUtils.getClient().fontRenderer;

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);

        GlStateManager.rotate(-yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);

        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.setupBlend();
        GlStateManager.disableTexture2D();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        int maxLineLen = 0;

        for (String line : text)
        {
            maxLineLen = Math.max(maxLineLen, textRenderer.getStringWidth(line));
        }

        int strLenHalf = maxLineLen / 2;
        int textHeight = textRenderer.FONT_HEIGHT * text.size() - 1;
        float bga = ((bgColor >>> 24) & 0xFF) * 255f;
        float bgr = ((bgColor >>> 16) & 0xFF) * 255f;
        float bgg = ((bgColor >>>  8) & 0xFF) * 255f;
        float bgb = (bgColor          & 0xFF) * 255f;

        if (disableDepth)
        {
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
        }

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(-strLenHalf - 1,          -1, 0.0D).color(bgr, bgg, bgb, bga).endVertex();
        buffer.pos(-strLenHalf - 1,  textHeight, 0.0D).color(bgr, bgg, bgb, bga).endVertex();
        buffer.pos( strLenHalf    ,  textHeight, 0.0D).color(bgr, bgg, bgb, bga).endVertex();
        buffer.pos( strLenHalf    ,          -1, 0.0D).color(bgr, bgg, bgb, bga).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();
        int textY = 0;

        // translate the text a bit infront of the background
        if (disableDepth == false)
        {
            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(-0.6f, -1.2f);
            //GlStateManager.translate(0, 0, -0.02);

            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
        }

        for (String line : text)
        {
            if (disableDepth)
            {
                GlStateManager.depthMask(false);
                GlStateManager.disableDepth();

                // Render the faint version that will also show through blocks
                textRenderer.drawString(line, -strLenHalf, textY, 0x20000000 | (textColor & 0xFFFFFF));

                GlStateManager.enableDepth();
                GlStateManager.depthMask(true);
            }

            // Render the actual fully opaque text, that will not show through blocks
            textRenderer.drawString(line, -strLenHalf, textY, textColor);
            textY += textRenderer.FONT_HEIGHT;
        }

        if (disableDepth == false)
        {
            GlStateManager.doPolygonOffset(0f, 0f);
            GlStateManager.disablePolygonOffset();
        }

        RenderUtils.color(1f, 1f, 1f, 1f);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
