package fi.dy.masa.malilib.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import fi.dy.masa.malilib.config.value.HudAlignment;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextRenderer;
import fi.dy.masa.malilib.util.EntityUtils;
import fi.dy.masa.malilib.util.data.Vec2i;

public class TextRenderUtils
{
    public static void renderText(int x, int y, int color, String text)
    {
        String[] parts = text.split("\\\\n");
        FontRenderer textRenderer = Minecraft.getMinecraft().fontRenderer;

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
            FontRenderer textRenderer = Minecraft.getMinecraft().fontRenderer;

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
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontRenderer = mc.fontRenderer;
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
        posY += GuiUtils.getHudOffsetForPotions(alignment, scale, mc.player);

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
        int maxWidth = GuiUtils.getCurrentScreen().width;
        int textStartX = x + 4;
        int textStartY = Math.max(8, y - renderHeight - 6);

        // The text can't fit from the cursor to the right edge of the screen
        if (textStartX + renderWidth + 6 > maxWidth)
        {
            int leftX = x - renderWidth - 8;

            // If the text fits from the cursor to the left edge of the screen...
            if (leftX >= 4)
            {
                textStartX = leftX;
            }
            // otherwise move it to touching the edge of the screen that the cursor is closest to
            else
            {
                textStartX = x < (maxWidth / 2) ? 4 : Math.max(4, maxWidth - renderWidth - 6);
            }
        }

        // The hover info would overlap the cursor vertically
        // (because the hover info was clamped to the top of the screen),
        // move it below the cursor instead
        if (textStartY < y && y < textStartY + renderHeight)
        {
            textStartY = y + 16;
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
        Minecraft mc = Minecraft.getMinecraft();

        if (textLines.isEmpty() == false && GuiUtils.getCurrentScreen() != null)
        {
            List<String> linesNew = new ArrayList<>();
            FontRenderer font = mc.fontRenderer;
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
            Vec2i startPos = getScreenClampedHoverTextStartPosition(x, y, maxLineLength, textHeight);
            int textStartX = startPos.x;
            int textStartY = startPos.y;

            GlStateManager.disableRescaleNormal();
            RenderUtils.disableItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();

            backgroundRenderer.render(textStartX, textStartY, z, maxLineLength, textHeight);

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
            int maxLineLength = 0;

            for (StyledTextLine line : textLines)
            {
                if (line.renderWidth > maxLineLength)
                {
                    maxLineLength = line.renderWidth;
                }
            }

            final int lineHeight = textRenderer.getFontHeight() + 1;
            int textHeight = textLines.size() * lineHeight - 2;
            Vec2i startPos = getScreenClampedHoverTextStartPosition(x, y, maxLineLength, textHeight);
            int textStartX = startPos.x;
            int textStartY = startPos.y;

            GlStateManager.disableRescaleNormal();
            RenderUtils.disableItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();

            backgroundRenderer.render(textStartX, textStartY, z, maxLineLength, textHeight);
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
        int borderColor = 0xF0100010;
        int fillColor1 = 0x505000FF;
        int fillColor2 = 0x5028007F;

        renderHoverTextBackground(x, y, z, width, height, borderColor, fillColor1, fillColor2);
    }

    public static void renderHoverTextBackground(int x, int y, float z, int width, int height,
                                                 int borderColor, int fillColor1, int fillColor2)
    {
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        RenderUtils.setupBlend();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        ShapeRenderUtils.renderGradientRectangle(x - 3        , y - 4         , x + width + 3, y - 3         , z, borderColor, borderColor, buffer);
        ShapeRenderUtils.renderGradientRectangle(x - 3        , y + height + 3, x + width + 3, y + height + 4, z, borderColor, borderColor, buffer);
        ShapeRenderUtils.renderGradientRectangle(x - 3        , y - 3         , x + width + 3, y + height + 3, z, borderColor, borderColor, buffer);
        ShapeRenderUtils.renderGradientRectangle(x - 4        , y - 3         , x - 3        , y + height + 3, z, borderColor, borderColor, buffer);
        ShapeRenderUtils.renderGradientRectangle(x + width + 3, y - 3         , x + width + 4, y + height + 3, z, borderColor, borderColor, buffer);

        ShapeRenderUtils.renderGradientRectangle(x - 3        , y - 3 + 1     , x - 3 + 1    , y + height + 3 - 1, z, fillColor1, fillColor2, buffer);
        ShapeRenderUtils.renderGradientRectangle(x + width + 2, y - 3 + 1     , x + width + 3, y + height + 3 - 1, z, fillColor1, fillColor2, buffer);
        ShapeRenderUtils.renderGradientRectangle(x - 3        , y - 3         , x + width + 3, y - 3 + 1         , z, fillColor1, fillColor1, buffer);
        ShapeRenderUtils.renderGradientRectangle(x - 3        , y + height + 2, x + width + 3, y + height + 3    , z, fillColor2, fillColor2, buffer);

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
        Entity entity = EntityUtils.getCameraEntity();

        if (entity != null)
        {
            renderTextPlate(text, x, y, z, entity.rotationYaw, entity.rotationPitch, scale, 0xFFFFFFFF, 0x40000000, true);
        }
    }

    /**
     * Renders a text plate/billboard, similar to the player name plate.<br>
     * The plate will face towards the given angle.
     */
    public static void renderTextPlate(List<String> text, double x, double y, double z, float yaw, float pitch,
                                       float scale, int textColor, int bgColor, boolean disableDepth)
    {
        FontRenderer textRenderer = Minecraft.getMinecraft().fontRenderer;

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);

        GlStateManager.rotate(-yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);

        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();

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
