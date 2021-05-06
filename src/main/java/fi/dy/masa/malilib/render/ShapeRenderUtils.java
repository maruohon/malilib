package fi.dy.masa.malilib.render;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.data.IntBoundingBox;

public class ShapeRenderUtils
{
    public static void renderHorizontalLine(float x, float y, float z, int width, int color)
    {
        renderRectangle(x, y, z, width, 1, color);
    }

    public static void renderVerticalLine(float x, float y, float z, int height, int color)
    {
        renderRectangle(x, y, z, 1, height, color);
    }

    public static void renderGrid(float x, float y, float z, int width, int height,
                                  int gridInterval, int lineWidth, int color)
    {
        BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR, false);

        renderGrid(x, y, z, width, height, gridInterval, lineWidth, color, buffer);

        RenderUtils.drawBuffer();
    }

    public static void renderGrid(float x, float y, float z, int width, int height,
                                  int gridInterval, int lineWidth, int color, BufferBuilder buffer)
    {
        float endX = x + width;
        float endY = y + height;

        for (float tmpX = x; tmpX <= endX; tmpX += gridInterval)
        {
            renderRectangle(tmpX, y, z + 0.000125f, lineWidth, height, color, buffer);
        }

        for (float tmpY = y; tmpY <= endY; tmpY += gridInterval)
        {
            renderRectangle(x, tmpY, z, width, lineWidth, color, buffer);
        }
    }

    public static void renderOutlinedRectangle(float x, float y, float z, int width, int height, int colorBg, int colorBorder)
    {
        BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR, false);

        // Draw the background
        renderRectangle(x + 1, y + 1, z, width - 2, height - 2, colorBg, buffer);

        // Draw the border
        renderOutline(x, y, z, width, height, 1, colorBorder, buffer);

        RenderUtils.drawBuffer();
    }

    public static void renderOutline(float x, float y, float z, int width, int height, int borderWidth, int colorBorder)
    {
        BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR, false);

        renderOutline(x, y, z, width, height, borderWidth, colorBorder, buffer);

        RenderUtils.drawBuffer();
    }

    /**
     * Takes in a BufferBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderOutline(float x, float y, float z, int width, int height,
                                     int borderWidth, int colorBorder, BufferBuilder buffer)
    {
        renderRectangle(x                      , y                       , z, borderWidth            , height     , colorBorder, buffer); // left edge
        renderRectangle(x + width - borderWidth, y                       , z, borderWidth            , height     , colorBorder, buffer); // right edge
        renderRectangle(x + borderWidth        , y                       , z, width - 2 * borderWidth, borderWidth, colorBorder, buffer); // top edge
        renderRectangle(x + borderWidth        , y + height - borderWidth, z, width - 2 * borderWidth, borderWidth, colorBorder, buffer); // bottom edge
    }

    public static void renderRectangle(float x, float y, float z, int width, int height, int color)
    {
        BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR, false);

        renderRectangle(x, y, z, width, height, color, buffer);

        RenderUtils.drawBuffer();
    }

    /**
     * Takes in a BufferBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderRectangle(float x, float y, float z, float width, float height,
                                       int color, BufferBuilder buffer)
    {
        float a = (float) ((color >> 24) & 0xFF) / 255.0F;
        float r = (float) ((color >> 16) & 0xFF) / 255.0F;
        float g = (float) ((color >>  8) & 0xFF) / 255.0F;
        float b = (float) (color         & 0xFF) / 255.0F;

        buffer.pos(x        , y         , z).color(r, g, b, a).endVertex();
        buffer.pos(x        , y + height, z).color(r, g, b, a).endVertex();
        buffer.pos(x + width, y + height, z).color(r, g, b, a).endVertex();
        buffer.pos(x + width, y         , z).color(r, g, b, a).endVertex();
    }

    /**
     * Takes in a BufferBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderRectangle(float x, float y, float z, float width, float height,
                                       Color4f color, BufferBuilder buffer)
    {
        buffer.pos(x        , y         , z).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(x        , y + height, z).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(x + width, y + height, z).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(x + width, y         , z).color(color.r, color.g, color.b, color.a).endVertex();
    }

    /**
     * Renders a textured rectangle.<br>
     * Assumes the bound texture sheet dimensions to be 256 x 256 pixels.
     */
    public static void renderTexturedRectangle256(float x, float y, float z, int u, int v, int width, int height)
    {
        BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX, true);

        renderTexturedRectangle256(x, y, z, u, v, width, height, buffer);

        RenderUtils.drawBuffer();
    }

    /**
     * Renders a textured rectangle.<br>
     * Assumes the bound texture sheet dimensions to be 256 x 256 pixels.<br>
     * Takes in a BufferBuilder initialized in GL_QUADS, POSITION_TEX mode
     */
    public static void renderTexturedRectangle256(float x, float y, float z, int u, int v,
                                                  int width, int height, BufferBuilder buffer)
    {
        float pixelSize = 0.00390625F; // 1 / 256
        renderTexturedRectangle(x, y, z, u, v, width, height, pixelSize, pixelSize, buffer);
    }

    /**
     * Renders a textured rectangle with a custom sized texture sheet. The sheet size
     * is indicated by the pixelWidth and pixelHeight arguments, which are the
     * relative width and height of one pixel on the sheet.
     */
    public static void renderTexturedRectangle(float x, float y, float z, int u, int v,
                                               int width, int height, float pixelWidth, float pixelHeight)
    {
        renderScaledTexturedRectangle(x, y, z, u, v, width, height, width, height, pixelWidth, pixelHeight);
    }

    /**
     * Renders a textured rectangle with a custom sized texture sheet. The sheet size
     * is indicated by the pixelWidth and pixelHeight arguments, which are the
     * relative width and height of one pixel on the sheet.
     * Takes in a BufferBuilder initialized in GL_QUADS, POSITION_TEX mode
     */
    public static void renderTexturedRectangle(float x, float y, float z, int u, int v,
                                               int width, int height,
                                               float pixelWidth, float pixelHeight, BufferBuilder buffer)
    {
        renderScaledTexturedRectangle(x, y, z, u, v, width, height, width, height, pixelWidth, pixelHeight, buffer);
    }

    /**
     * Renders a possibly scaled/stretched textured rectangle with a custom sized texture sheet.
     * The sheet size is indicated by the pixelWidth and pixelHeight arguments, which are the
     * relative width and height of one pixel on the sheet.
     * The width and height parameters are the rendered size of the rectangle/texture, whereas the
     * textureWidth and textureHeight parameters define which region of the texture sheet is used.
     */
    public static void renderScaledTexturedRectangle(float x, float y, float z, int u, int v,
                                                     int width, int height, int textureWidth, int textureHeight,
                                                     float pixelWidth, float pixelHeight)
    {
        BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX, true);

        renderScaledTexturedRectangle(x, y, z, u, v, width, height, textureWidth, textureHeight,
                                      pixelWidth, pixelHeight, buffer);

        RenderUtils.drawBuffer();
    }

    /**
     * Renders a possibly scaled/stretched textured rectangle with a custom sized texture sheet.
     * The sheet size is indicated by the pixelWidth and pixelHeight arguments, which are the
     * relative width and height of one pixel on the sheet.
     * The width and height parameters are the rendered size of the rectangle/texture, whereas the
     * textureWidth and textureHeight parameters define which region of the texture sheet is used.
     * Takes in a BufferBuilder initialized in GL_QUADS, POSITION_TEX mode
     */
    public static void renderScaledTexturedRectangle(float x, float y, float z, int u, int v,
                                                     int width, int height, int textureWidth, int textureHeight,
                                                     float pixelWidth, float pixelHeight, BufferBuilder buffer)
    {
        buffer.pos(x        , y + height, z).tex(u                  * pixelWidth, (v + textureHeight) * pixelHeight).endVertex();
        buffer.pos(x + width, y + height, z).tex((u + textureWidth) * pixelWidth, (v + textureHeight) * pixelHeight).endVertex();
        buffer.pos(x + width, y         , z).tex((u + textureWidth) * pixelWidth, v                   * pixelHeight).endVertex();
        buffer.pos(x        , y         , z).tex(u                  * pixelWidth, v                   * pixelHeight).endVertex();
    }

    public static void renderGradientRectangle(float left, float top, float right, float bottom, float z,
                                               int startColor, int endColor)
    {
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR, false);

        renderGradientRectangle(left, top, right, bottom, z, startColor, endColor, buffer);

        RenderUtils.drawBuffer();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableAlpha();
    }

    /**
     * Takes in a BufferBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderGradientRectangle(float left, float top, float right, float bottom, float z,
                                               int startColor, int endColor, BufferBuilder buffer)
    {
        float sa = (float)(startColor >> 24 & 0xFF) / 255.0F;
        float sr = (float)(startColor >> 16 & 0xFF) / 255.0F;
        float sg = (float)(startColor >>  8 & 0xFF) / 255.0F;
        float sb = (float)(startColor & 0xFF) / 255.0F;

        float ea = (float)(endColor >> 24 & 0xFF) / 255.0F;
        float er = (float)(endColor >> 16 & 0xFF) / 255.0F;
        float eg = (float)(endColor >>  8 & 0xFF) / 255.0F;
        float eb = (float)(endColor & 0xFF) / 255.0F;

        buffer.pos(right, top,    z).color(sr, sg, sb, sa).endVertex();
        buffer.pos(left,  top,    z).color(sr, sg, sb, sa).endVertex();
        buffer.pos(left,  bottom, z).color(er, eg, eb, ea).endVertex();
        buffer.pos(right, bottom, z).color(er, eg, eb, ea).endVertex();
    }

    public static void renderArc(double centerX, double centerY, double z, double radius,
                                 double startAngle, double endAngle, float lineWidth, int color)
    {
        if (radius < 1)
        {
            return;
        }

        float a = (float)(color >> 24 & 0xFF) / 255.0F;
        float r = (float)(color >> 16 & 0xFF) / 255.0F;
        float g = (float)(color >>  8 & 0xFF) / 255.0F;
        float b = (float)(color & 0xFF) / 255.0F;

        double twoPi = 2 * Math.PI;
        double arcAngle = (endAngle - startAngle) % twoPi;

        if (arcAngle < 0)
        {
            arcAngle += twoPi;
        }

        double arcLength = arcAngle * radius;
        int steps = (int) Math.ceil(arcLength / 5.0);
        double angleIncrement = arcAngle / (double) steps;
        double lastAngle = startAngle;

        BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR, false);
        GlStateManager.glLineWidth(lineWidth);

        for (int i = 0; i <= steps; ++i)
        {
            double x = centerX + radius * Math.cos(lastAngle);
            double y = centerY + radius * Math.sin(lastAngle);

            buffer.pos(x, y, z).color(r, g, b, a).endVertex();

            lastAngle += angleIncrement;
        }

        RenderUtils.drawBuffer();
    }

    /**
     * Renders the outline for a circle sector/segment.
     */
    public static void renderSectorOutline(double centerX, double centerY, double z,
                                           double innerRadius, double outerRadius,
                                           double startAngle, double endAngle, float lineWidth, int color)
    {
        if (innerRadius < 1 || outerRadius < 1)
        {
            return;
        }

        float a = (float)(color >> 24 & 0xFF) / 255.0F;
        float r = (float)(color >> 16 & 0xFF) / 255.0F;
        float g = (float)(color >>  8 & 0xFF) / 255.0F;
        float b = (float)(color & 0xFF) / 255.0F;

        double twoPi = 2 * Math.PI;
        double arcAngle = (endAngle - startAngle) % twoPi;

        if (arcAngle < 0)
        {
            arcAngle += twoPi;
        }

        double arcLength = arcAngle * innerRadius;
        int steps = (int) Math.ceil(arcLength / 5.0);
        double angleIncrement = arcAngle / (double) steps;
        double lastAngle = startAngle;

        BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR, false);
        GlStateManager.glLineWidth(lineWidth);

        // First render the inner arc in the positive direction
        for (int i = 0; i <= steps; ++i)
        {
            double x = centerX + innerRadius * Math.cos(lastAngle);
            double y = centerY + innerRadius * Math.sin(lastAngle);

            buffer.pos(x, y, z).color(r, g, b, a).endVertex();

            lastAngle += angleIncrement;
        }

        arcLength = arcAngle * outerRadius;
        steps = (int) Math.ceil(arcLength / 5.0);
        angleIncrement = arcAngle / (double) steps;

        lastAngle = endAngle;

        // Second render the outer arc in the negative direction.
        // The end of the inner arc will connect to the start of the outer arc, and vice versa
        for (int i = 0; i <= steps; ++i)
        {
            double x = centerX + outerRadius * Math.cos(lastAngle);
            double y = centerY + outerRadius * Math.sin(lastAngle);

            buffer.pos(x, y, z).color(r, g, b, a).endVertex();

            lastAngle -= angleIncrement;
        }

        RenderUtils.drawBuffer();
    }

    public static void renderSectorFill(double centerX, double centerY, double z,
                                        double innerRadius, double outerRadius,
                                        double startAngle, double endAngle, int color)
    {
        if (innerRadius < 1 || outerRadius < 1)
        {
            return;
        }

        float a = (float)(color >> 24 & 0xFF) / 255.0F;
        float r = (float)(color >> 16 & 0xFF) / 255.0F;
        float g = (float)(color >>  8 & 0xFF) / 255.0F;
        float b = (float)(color & 0xFF) / 255.0F;

        double twoPi = 2 * Math.PI;
        double arcAngle = (endAngle - startAngle) % twoPi;

        if (arcAngle < 0)
        {
            arcAngle += twoPi;
        }

        BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR, false);

        double arcLength = arcAngle * outerRadius;
        int steps = Math.max((int) Math.ceil(arcLength / 5.0), 2);
        double angleIncrement = arcAngle / (double) steps;

        double lastAngle = endAngle;
        double x, y;

        for (int i = 0; i <= steps; ++i)
        {
            x = centerX + innerRadius * Math.cos(lastAngle);
            y = centerY + innerRadius * Math.sin(lastAngle);

            buffer.pos(x, y, z).color(r, g, b, a).endVertex();

            x = centerX + outerRadius * Math.cos(lastAngle);
            y = centerY + outerRadius * Math.sin(lastAngle);

            buffer.pos(x, y, z).color(r, g, b, a).endVertex();

            lastAngle -= angleIncrement;
        }

        RenderUtils.drawBuffer();
    }

    /**
     * Takes in a BufferBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderBlockPosSideQuads(BlockPos pos, double expand, Color4f color, BufferBuilder buffer)
    {
        renderBlockPosSideQuads(pos, expand, color, buffer, Vec3d.ZERO);
    }

    /**
     * Takes in a BufferBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderBlockPosSideQuads(BlockPos pos, double expand, Color4f color,
                                               BufferBuilder buffer, Vec3d cameraPos)
    {
        double minX = pos.getX() - expand - cameraPos.x;
        double minY = pos.getY() - expand - cameraPos.y;
        double minZ = pos.getZ() - expand - cameraPos.z;
        double maxX = pos.getX() + expand - cameraPos.x + 1;
        double maxY = pos.getY() + expand - cameraPos.y + 1;
        double maxZ = pos.getZ() + expand - cameraPos.z + 1;

        renderBoxSideQuads(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
    }

    /**
     * Takes in a BufferBuilder initialized in GL_LINES, POSITION_COLOR mode
     */
    public static void renderBlockPosEdgeLines(BlockPos pos, double expand, Color4f color, BufferBuilder buffer)
    {
        renderBlockPosEdgeLines(pos, expand, color, buffer, Vec3d.ZERO);
    }

    /**
     * Takes in a BufferBuilder initialized in GL_LINES, POSITION_COLOR mode
     * The cameraPos value will be subtracted from the absolute coordinate values of the passed in BlockPos.
     */
    public static void renderBlockPosEdgeLines(BlockPos pos, double expand, Color4f color,
                                               BufferBuilder buffer, Vec3d cameraPos)
    {
        double minX = pos.getX() - expand - cameraPos.x;
        double minY = pos.getY() - expand - cameraPos.y;
        double minZ = pos.getZ() - expand - cameraPos.z;
        double maxX = pos.getX() + expand - cameraPos.x + 1;
        double maxY = pos.getY() + expand - cameraPos.y + 1;
        double maxZ = pos.getZ() + expand - cameraPos.z + 1;

        renderBoxEdgeLines(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
    }

    /**
     * Takes in a BufferBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderBoxSideQuads(double minX, double minY, double minZ,
                                          double maxX, double maxY, double maxZ,
                                          Color4f color, BufferBuilder buffer)
    {
        renderBoxHorizontalSideQuads(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
        renderBoxTopQuad(minX, minZ, maxX, maxY, maxZ, color, buffer);
        renderBoxBottomQuad(minX, minY, minZ, maxX, maxZ, color, buffer);
    }

    /**
     * Draws a box with outlines around the given corner positions.
     * Takes in buffers initialized for GL_QUADS and GL_LINES, POSITION_COLOR modes.
     * The cameraPos value will be subtracted from the absolute coordinate values of the passed in block positions.
     */
    public static void renderBoxSidesAndEdges(BlockPos posMin, BlockPos posMax,
                                              Color4f colorLines, Color4f colorSides,
                                              BufferBuilder bufferQuads, BufferBuilder bufferLines, Vec3d cameraPos)
    {
        final double x1 = posMin.getX() - cameraPos.x;
        final double y1 = posMin.getY() - cameraPos.y;
        final double z1 = posMin.getZ() - cameraPos.z;
        final double x2 = posMax.getX() + 1 - cameraPos.x;
        final double y2 = posMax.getY() + 1 - cameraPos.y;
        final double z2 = posMax.getZ() + 1 - cameraPos.z;

        renderBoxSideQuads(x1, y1, z1, x2, y2, z2, colorSides, bufferQuads);
        renderBoxEdgeLines(x1, y1, z1, x2, y2, z2, colorLines, bufferLines);
    }

    /**
     * Takes in a BufferBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderBoxHorizontalSideQuads(double minX, double minY, double minZ,
                                                    double maxX, double maxY, double maxZ,
                                                    Color4f color, BufferBuilder buffer)
    {
        // West side
        buffer.pos(minX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();

        // East side
        buffer.pos(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();

        // North side
        buffer.pos(maxX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();

        // South side
        buffer.pos(minX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
    }

    /**
     * Takes in a BufferBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderBoxTopQuad(double minX, double minZ,
                                        double maxX, double maxY, double maxZ,
                                        Color4f color, BufferBuilder buffer)
    {
        // Top side
        buffer.pos(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
    }

    /**
     * Takes in a BufferBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderBoxBottomQuad(double minX, double minY, double minZ,
                                           double maxX, double maxZ,
                                           Color4f color, BufferBuilder buffer)
    {
        // Bottom side
        buffer.pos(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
    }

    /**
     * Takes in a BufferBuilder initialized in GL_LINES, POSITION_COLOR mode
     */
    public static void renderBoxEdgeLines(double minX, double minY, double minZ,
                                          double maxX, double maxY, double maxZ,
                                          Color4f color, BufferBuilder buffer)
    {
        // West side
        buffer.pos(minX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();

        buffer.pos(minX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();

        buffer.pos(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();

        buffer.pos(minX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();

        // East side
        buffer.pos(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();

        buffer.pos(maxX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();

        buffer.pos(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();

        buffer.pos(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();

        // North side (don't repeat the vertical lines that are done by the east/west sides)
        buffer.pos(maxX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();

        buffer.pos(minX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();

        // South side (don't repeat the vertical lines that are done by the east/west sides)
        buffer.pos(minX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();

        buffer.pos(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
    }

    /**
     * Takes in a BufferBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderBlockPosSideQuad(BlockPos pos, EnumFacing side, double expand,
                                              Color4f color, BufferBuilder buffer)
    {
        renderBlockPosSideQuad(pos, side, expand, color, buffer, Vec3d.ZERO);
    }

    /**
     * Takes in a BufferBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderBlockPosSideQuad(BlockPos pos, EnumFacing side, double expand,
                                              Color4f color, BufferBuilder buffer, Vec3d cameraPos)
    {
        double minX = pos.getX() - expand - cameraPos.x;
        double minY = pos.getY() - expand - cameraPos.y;
        double minZ = pos.getZ() - expand - cameraPos.z;
        double maxX = pos.getX() + expand - cameraPos.x + 1;
        double maxY = pos.getY() + expand - cameraPos.y + 1;
        double maxZ = pos.getZ() + expand - cameraPos.z + 1;

        switch (side)
        {
            case DOWN:
                buffer.pos(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
                buffer.pos(minX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
                buffer.pos(minX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
                buffer.pos(maxX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
                break;

            case UP:
                buffer.pos(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
                buffer.pos(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
                buffer.pos(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
                buffer.pos(minX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
                break;

            case NORTH:
                buffer.pos(maxX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
                buffer.pos(minX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
                buffer.pos(minX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
                buffer.pos(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
                break;

            case SOUTH:
                buffer.pos(minX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
                buffer.pos(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
                buffer.pos(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
                buffer.pos(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
                break;

            case WEST:
                buffer.pos(minX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
                buffer.pos(minX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
                buffer.pos(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
                buffer.pos(minX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
                break;

            case EAST:
                buffer.pos(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
                buffer.pos(maxX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
                buffer.pos(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
                buffer.pos(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
                break;
        }
    }

    /**
     * Takes in buffers initialized for GL_QUADS and GL_LINES, POSITION_COLOR modes.
     */
    public static void renderBoxSidesAndEdges(IntBoundingBox bb, Color4f color,
                                              BufferBuilder bufferQuads, BufferBuilder bufferLines, Vec3d cameraPos)
    {
        double minX = bb.minX - cameraPos.x;
        double minY = bb.minY - cameraPos.y;
        double minZ = bb.minZ - cameraPos.z;
        double maxX = bb.maxX - cameraPos.x + 1;
        double maxY = bb.maxY - cameraPos.y + 1;
        double maxZ = bb.maxZ - cameraPos.z + 1;

        renderBoxSideQuads(minX, minY, minZ, maxX, maxY, maxZ, color, bufferQuads);
        renderBoxEdgeLines(minX, minY, minZ, maxX, maxY, maxZ, color, bufferLines);
    }
}
