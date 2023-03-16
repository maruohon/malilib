package malilib.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

import malilib.gui.util.GuiUtils;
import malilib.render.text.StyledTextLine;
import malilib.render.text.TextRenderer;
import malilib.util.game.wrap.EntityWrap;
import malilib.util.game.wrap.GameUtils;
import malilib.util.position.Vec2i;

public class TextRenderUtils
{
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

    public static void renderHoverText(int x, int y, float z, String text, RenderContext ctx)
    {
        renderHoverText(x, y, z, Collections.singletonList(text), ctx);
    }

    public static void renderHoverText(int x, int y, float z, List<String> textLines, RenderContext ctx)
    {
        renderHoverText(x, y, z, textLines, 0xFFC0C0C0 , TextRenderUtils::renderDefaultHoverTextBackground, ctx);
    }

    public static void renderHoverText(int x, int y, float z, List<String> textLines,
                                       int textColor, RectangleRenderer backgroundRenderer, RenderContext ctx)
    {
        if (textLines.isEmpty() == false && GuiUtils.getCurrentScreen() != null)
        {
            List<String> linesNew = new ArrayList<>();
            net.minecraft.client.font.TextRenderer textRenderer = GameUtils.getClient().textRenderer;
            int maxLineLength = 0;

            for (String lineOrig : textLines)
            {
                String[] lines = lineOrig.split("\\\\n");

                for (String line : lines)
                {
                    int length = textRenderer.getWidth(line);

                    if (length > maxLineLength)
                    {
                        maxLineLength = length;
                    }

                    linesNew.add(line);
                }
            }

            textLines = linesNew;

            int lineHeight = textRenderer.fontHeight + 1;
            int textHeight = textLines.size() * lineHeight - 2;
            int backgroundWidth = maxLineLength + 8;
            int backgroundHeight = textHeight + 8;
            Vec2i startPos = getScreenClampedHoverTextStartPosition(x, y, backgroundWidth, backgroundHeight);
            int textStartX = startPos.x + 4;
            int textStartY = startPos.y + 4;

            MatrixStack matrixStack = ctx.matrixStack;
            matrixStack.push();
            matrixStack.translate(0, 0, z + 1);

            RenderUtils.disableItemLighting();

            backgroundRenderer.render(startPos.x, startPos.y, z, backgroundWidth, backgroundHeight, ctx);

            for (String str : textLines)
            {
                textRenderer.drawWithShadow(matrixStack, str, textStartX, textStartY, textColor);
                textStartY += lineHeight;
            }

            matrixStack.pop();

            RenderSystem.enableDepthTest();
        }
    }

    public static void renderStyledHoverText(int x, int y, float z, List<StyledTextLine> textLines, RenderContext ctx)
    {
        renderStyledHoverText(x, y, z, textLines, 0xFFB0B0B0 , TextRenderUtils::renderDefaultHoverTextBackground, ctx);
    }

    public static void renderStyledHoverText(int x, int y, float z, List<StyledTextLine> textLines,
                                             int textColor, RectangleRenderer backgroundRenderer, RenderContext ctx)
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

            //GlStateManager.disableRescaleNormal();
            RenderUtils.disableItemLighting();
            //GlStateManager.disableLighting();
            RenderSystem.disableDepthTest();

            backgroundRenderer.render(startPos.x, startPos.y, z, backgroundWidth, backgroundHeight, ctx);
            textRenderer.startBuffers();

            for (StyledTextLine line : textLines)
            {
                textRenderer.renderLineToBuffer(textStartX, textStartY, z, textColor, true, line);
                textStartY += lineHeight;
            }

            textRenderer.renderBuffers();
            //GlStateManager.enableLighting();
            RenderSystem.enableDepthTest();
            //RenderHelper.enableStandardItemLighting();
            //RenderSystem.enableRescaleNormal();
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
        BufferBuilder buffer = RenderUtils.startBuffer(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR, false);

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

        RenderUtils.setupBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        ShapeRenderUtils.renderGradientRectangle(xl2, yt1, xr2, yt2, z, fillColor, fillColor, buffer);
        ShapeRenderUtils.renderGradientRectangle(xl2, yb2, xr2, yb3, z, fillColor, fillColor, buffer);
        ShapeRenderUtils.renderGradientRectangle(xl2, yt2, xr2, yb2, z, fillColor, fillColor, buffer);
        ShapeRenderUtils.renderGradientRectangle(xl1, yt2, xl2, yb2, z, fillColor, fillColor, buffer);
        ShapeRenderUtils.renderGradientRectangle(xr2, yt2, xr3, yb2, z, fillColor, fillColor, buffer);

        ShapeRenderUtils.renderGradientRectangle(xl2, yt3, xl3, yb1, z, borderColor1, borderColor2, buffer);
        ShapeRenderUtils.renderGradientRectangle(xr1, yt3, xr2, yb1, z, borderColor1, borderColor2, buffer);
        ShapeRenderUtils.renderGradientRectangle(xl2, yt2, xr2, yt3, z, borderColor1, borderColor1, buffer);
        ShapeRenderUtils.renderGradientRectangle(xl2, yb1, xr2, yb2, z, borderColor2, borderColor2, buffer);

        RenderUtils.drawBuffer();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
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
        net.minecraft.client.font.TextRenderer textRenderer = GameUtils.getClient().textRenderer;

        //GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        MatrixStack matrixStack = ctx.matrixStack;
        matrixStack.push();
        matrixStack.translate(x, y, z);
        //GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);

        Quaternion rot = Vec3f.POSITIVE_Y.getDegreesQuaternion(-yaw);
        rot.hamiltonProduct(Vec3f.POSITIVE_X.getDegreesQuaternion(pitch));
        matrixStack.multiply(rot);

        matrixStack.scale(-scale, -scale, scale);
        //GlStateManager.disableLighting();
        RenderSystem.disableCull();

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.setupBlend();
        RenderSystem.disableTexture();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        int maxLineLen = 0;

        for (String line : text)
        {
            maxLineLen = Math.max(maxLineLen, textRenderer.getWidth(line));
        }

        int strLenHalf = maxLineLen / 2;
        int textHeight = textRenderer.fontHeight * text.size() - 1;
        float bga = ((bgColor >>> 24) & 0xFF) * 255f;
        float bgr = ((bgColor >>> 16) & 0xFF) * 255f;
        float bgg = ((bgColor >>>  8) & 0xFF) * 255f;
        float bgb = (bgColor          & 0xFF) * 255f;

        if (disableDepth)
        {
            RenderSystem.depthMask(false);
            RenderSystem.disableDepthTest();
        }

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(-strLenHalf - 1,          -1, 0.0D).color(bgr, bgg, bgb, bga).next();
        buffer.vertex(-strLenHalf - 1,  textHeight, 0.0D).color(bgr, bgg, bgb, bga).next();
        buffer.vertex( strLenHalf    ,  textHeight, 0.0D).color(bgr, bgg, bgb, bga).next();
        buffer.vertex( strLenHalf    ,          -1, 0.0D).color(bgr, bgg, bgb, bga).next();
        tessellator.draw();

        RenderSystem.enableTexture();
        int textY = 0;

        // translate the text a bit infront of the background
        if (disableDepth == false)
        {
            RenderSystem.enablePolygonOffset();
            RenderSystem.polygonOffset(-0.6f, -1.2f);
            //GlStateManager.translate(0, 0, -0.02);

            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
        }

        for (String line : text)
        {
            if (disableDepth)
            {
                RenderSystem.depthMask(false);
                RenderSystem.disableDepthTest();

                // Render the faint version that will also show through blocks
                textRenderer.draw(matrixStack, line, -strLenHalf, textY, 0x20000000 | (textColor & 0xFFFFFF));

                RenderSystem.enableDepthTest();
                RenderSystem.depthMask(true);
            }

            // Render the actual fully opaque text, that will not show through blocks
            textRenderer.draw(matrixStack, line, -strLenHalf, textY, textColor);
            textY += textRenderer.fontHeight;
        }

        if (disableDepth == false)
        {
            RenderSystem.polygonOffset(0f, 0f);
            RenderSystem.disablePolygonOffset();
        }

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        matrixStack.pop();
    }
}
