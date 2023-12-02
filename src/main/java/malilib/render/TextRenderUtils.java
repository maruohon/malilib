package malilib.render;

import java.util.List;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;

import malilib.gui.util.GuiUtils;
import malilib.render.buffer.VanillaWrappingVertexBuilder;
import malilib.render.buffer.VertexBuilder;
import malilib.render.text.StyledText;
import malilib.render.text.StyledTextLine;
import malilib.render.text.TextRenderer;
import malilib.util.game.wrap.EntityWrap;
import malilib.util.game.wrap.GameUtils;
import malilib.util.position.Vec2i;

public class TextRenderUtils
{
    public static Vec2i getScreenClampedHoverTextStartPosition(int x, int y, int renderWidth, int renderHeight)
    {
        GuiScreen screen = GuiUtils.getCurrentScreen();
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

    public static void renderStyledHoverText(int x, int y, float z, StyledText text, RenderContext ctx)
    {
        renderStyledHoverText(x, y, z, text.lines, 0xFFB0B0B0 , TextRenderUtils::renderDefaultHoverTextBackground, ctx);
    }

    public static void renderStyledHoverText(int x, int y, float z, List<StyledTextLine> textLines, RenderContext ctx)
    {
        renderStyledHoverText(x, y, z, textLines, 0xFFB0B0B0 , TextRenderUtils::renderDefaultHoverTextBackground, ctx);
    }

    public static void renderStyledHoverText(int x, int y, float z, List<StyledTextLine> textLines,
                                             int textColor, RectangleRenderer backgroundRenderer, RenderContext ctx)
    {
        // FIXME Why does this check for the screen?
        if (textLines.isEmpty() == false && GuiUtils.isScreenOpen())
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

            backgroundRenderer.render(startPos.x, startPos.y, z, backgroundWidth, backgroundHeight, ctx);
            textRenderer.startBuffers();

            for (StyledTextLine line : textLines)
            {
                textRenderer.renderLineToBuffer(textStartX, textStartY, z, textColor, true, line, ctx);
                textStartY += lineHeight;
            }

            textRenderer.renderBuffers();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    public static void renderDefaultHoverTextBackground(int x, int y, float z, int width, int height, RenderContext ctx)
    {
        int fillColor = 0xF0180018;
        int borderColor1 = 0xD02060FF;
        int borderColor2 = 0xC01030A0;

        renderHoverTextBackground(x, y, z, width, height, fillColor, borderColor1, borderColor2, ctx);
    }

    public static void renderHoverTextBackground(int x, int y, float z, int width, int height,
                                                 int fillColor, int borderColor1, int borderColor2,
                                                 RenderContext ctx)
    {
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

        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();

        ShapeRenderUtils.renderGradientRectangle(xl2, yt1, xr2, yt2, z, fillColor, fillColor, builder);
        ShapeRenderUtils.renderGradientRectangle(xl2, yb2, xr2, yb3, z, fillColor, fillColor, builder);
        ShapeRenderUtils.renderGradientRectangle(xl2, yt2, xr2, yb2, z, fillColor, fillColor, builder);
        ShapeRenderUtils.renderGradientRectangle(xl1, yt2, xl2, yb2, z, fillColor, fillColor, builder);
        ShapeRenderUtils.renderGradientRectangle(xr2, yt2, xr3, yb2, z, fillColor, fillColor, builder);

        ShapeRenderUtils.renderGradientRectangle(xl2, yt3, xl3, yb1, z, borderColor1, borderColor2, builder);
        ShapeRenderUtils.renderGradientRectangle(xr1, yt3, xr2, yb1, z, borderColor1, borderColor2, builder);
        ShapeRenderUtils.renderGradientRectangle(xl2, yt2, xr2, yt3, z, borderColor1, borderColor1, builder);
        ShapeRenderUtils.renderGradientRectangle(xl2, yb1, xr2, yb2, z, borderColor2, borderColor2, builder);

        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        builder.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableAlpha();
    }

    /**
     * Renders a text plate/billboard, similar to the player name plate.<br>
     * The plate will face towards the camera entity.
     */
    public static void renderTextPlate(List<String> text, double x, double y, double z, float scale, RenderContext ctx)
    {
        Entity entity = GameUtils.getCameraEntity();

        if (entity != null)
        {
            renderTextPlate(text, x, y, z, EntityWrap.getYaw(entity), EntityWrap.getPitch(entity),
                            scale, 0xFFFFFFFF, 0x40000000, true, ctx);
        }
    }

    /**
     * Renders a text plate/billboard, similar to the player name plate.<br>
     * The plate will face towards the given angle.
     */
    public static void renderTextPlate(List<String> text, double x, double y, double z, float yaw, float pitch,
                                       float scale, int textColor, int bgColor, boolean disableDepth, RenderContext ctx)
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

        int maxLineLen = 0;

        for (String line : text)
        {
            maxLineLen = Math.max(maxLineLen, textRenderer.getStringWidth(line));
        }

        int strLenHalf = maxLineLen / 2;
        int textHeight = textRenderer.FONT_HEIGHT * text.size() - 1;
        int bga = (bgColor >> 24) & 0xFF;
        int bgr = (bgColor >> 16) & 0xFF;
        int bgg = (bgColor >>  8) & 0xFF;
        int bgb =  bgColor        & 0xFF;

        if (disableDepth)
        {
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
        }

        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();
        builder.posColor(-strLenHalf - 1,          -1, 0.0, bgr, bgg, bgb, bga);
        builder.posColor(-strLenHalf - 1,  textHeight, 0.0, bgr, bgg, bgb, bga);
        builder.posColor( strLenHalf    ,  textHeight, 0.0, bgr, bgg, bgb, bga);
        builder.posColor( strLenHalf    ,          -1, 0.0, bgr, bgg, bgb, bga);
        builder.draw();

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
