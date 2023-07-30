package malilib.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import malilib.render.buffer.VanillaWrappingVertexBuilder;
import malilib.render.buffer.VertexBuilder;
import malilib.util.data.Color4f;
import malilib.util.data.EdgeInt;
import malilib.util.position.IntBoundingBox;

public class ShapeRenderUtils
{
    public static void renderHorizontalLine(double x, double y, double z, double width, int color, RenderContext ctx)
    {
        renderRectangle(x, y, z, width, 1, color, ctx);
    }

    public static void renderVerticalLine(double x, double y, double z, double height, int color, RenderContext ctx)
    {
        renderRectangle(x, y, z, 1, height, color, ctx);
    }

    public static void renderGrid(double x, double y, double z,
                                  double width, double height,
                                  double gridInterval, double lineWidth,
                                  int color, RenderContext ctx)
    {
        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();
        renderGrid(x, y, z, width, height, gridInterval, lineWidth, color, builder);
        builder.draw();
    }

    public static void renderGrid(double x, double y, double z,
                                  double width, double height,
                                  double gridInterval, double lineWidth,
                                  int color, VertexBuilder builder)
    {
        double endX = x + width;
        double endY = y + height;

        for (double tmpX = x; tmpX <= endX; tmpX += gridInterval)
        {
            renderRectangle(tmpX, y, z + 0.000125, lineWidth, height, color, builder);
        }

        for (double tmpY = y; tmpY <= endY; tmpY += gridInterval)
        {
            renderRectangle(x, tmpY, z, width, lineWidth, color, builder);
        }
    }

    public static void renderOutlinedRectangle(double x, double y, double z,
                                               double width, double height,
                                               int colorBg, int colorBorder, RenderContext ctx)
    {
        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();
        renderOutlinedRectangle(x, y, z, width, height, colorBg, colorBorder, builder);
        builder.draw();
    }

    public static void renderOutlinedRectangle(double x, double y, double z,
                                               double width, double height,
                                               int colorBg, int colorBorder, VertexBuilder builder)
    {
        // Draw the background
        renderRectangle(x + 1, y + 1, z, width - 2, height - 2, colorBg, builder);

        // Draw the border
        renderOutline(x, y, z, width, height, 1, colorBorder, builder);
    }

    public static void renderOutlinedRectangle(double x, double y, double z,
                                               double width, double height,
                                               int bgColor, EdgeInt borderColor, RenderContext ctx)
    {
        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();
        renderOutlinedRectangle(x, y, z, width, height, bgColor, borderColor, builder);
        builder.draw();
    }

    public static void renderOutlinedRectangle(double x, double y, double z,
                                               double width, double height,
                                               int bgColor, EdgeInt borderColor, VertexBuilder builder)
    {
        // Draw the background
        renderRectangle(x + 1, y + 1, z, width - 2, height - 2, bgColor, builder);

        // Draw the border
        renderOutline(x, y, z, width, height, 1, borderColor, builder);
    }

    public static void renderOutline(double x, double y, double z,
                                     double width, double height,
                                     double borderWidth, int color, RenderContext ctx)
    {
        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();
        renderOutline(x, y, z, width, height, borderWidth, color, builder);
        builder.draw();
    }

    public static void renderOutline(double x, double y, double z,
                                     double width, double height,
                                     double borderWidth, EdgeInt color, RenderContext ctx)
    {
        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();
        renderOutline(x, y, z, width, height, borderWidth, color, builder);
        builder.draw();
    }

    /**
     * Takes in a VertexBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderOutline(double x, double y, double z,
                                     double width, double height,
                                     double borderWidth, int color, VertexBuilder builder)
    {
        renderRectangle(x                      , y                       , z, borderWidth            , height     , color, builder); // left edge
        renderRectangle(x + width - borderWidth, y                       , z, borderWidth            , height     , color, builder); // right edge
        renderRectangle(x + borderWidth        , y                       , z, width - 2 * borderWidth, borderWidth, color, builder); // top edge
        renderRectangle(x + borderWidth        , y + height - borderWidth, z, width - 2 * borderWidth, borderWidth, color, builder); // bottom edge
    }

    /**
     * Takes in a VertexBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderOutline(double x, double y, double z, double width, double height,
                                     double borderWidth, EdgeInt color, VertexBuilder builder)
    {
        renderRectangle(x                      , y                       , z, borderWidth            , height     , color.getLeft(),   builder); // left edge
        renderRectangle(x + width - borderWidth, y                       , z, borderWidth            , height     , color.getRight(),  builder); // right edge
        renderRectangle(x + borderWidth        , y                       , z, width - 2 * borderWidth, borderWidth, color.getTop(),    builder); // top edge
        renderRectangle(x + borderWidth        , y + height - borderWidth, z, width - 2 * borderWidth, borderWidth, color.getBottom(), builder); // bottom edge
    }

    /**
     * Takes in a VertexBuilder initialized in GL_TRIANGLES, POSITION_COLOR mode
     */
    public static void renderTriangle(double x1, double y1, double z1,
                                      double x2, double y2, double z2,
                                      double x3, double y3, double z3,
                                      int color, VertexBuilder builder)
    {
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >>  8) & 0xFF;
        int b =  color        & 0xFF;

        builder.posColor(x1, y1, z1, r, g, b, a);
        builder.posColor(x2, y2, z2, r, g, b, a);
        builder.posColor(x3, y3, z3, r, g, b, a);
    }

    public static void renderRectangle(double x, double y, double z,
                                       double width, double height, int color, RenderContext ctx)
    {
        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();
        renderRectangle(x, y, z, width, height, color, builder);
        builder.draw();
    }

    /**
     * Takes in a VertexBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderRectangle(double x, double y, double z, double width, double height,
                                       int color, VertexBuilder builder)
    {
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >>  8) & 0xFF;
        int b = color & 0xFF;

        builder.posColor(x        , y         , z, r, g, b, a);
        builder.posColor(x        , y + height, z, r, g, b, a);
        builder.posColor(x + width, y + height, z, r, g, b, a);
        builder.posColor(x + width, y         , z, r, g, b, a);
    }

    /**
     * Takes in a VertexBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderRectangle(double x, double y, double z, double width, double height,
                                       Color4f color, VertexBuilder builder)
    {
        builder.posColor(x        , y         , z, color);
        builder.posColor(x        , y + height, z, color);
        builder.posColor(x + width, y + height, z, color);
        builder.posColor(x + width, y         , z, color);
    }

    /**
     * Renders a textured rectangle.<br>
     * Assumes the bound texture sheet dimensions to be 256 x 256 pixels.
     */
    public static void renderTexturedRectangle256(double x, double y, double z,
                                                  int u, int v,
                                                  int width, int height, RenderContext ctx)
    {
        VertexBuilder builder = VanillaWrappingVertexBuilder.texturedQuad();
        renderTexturedRectangle256(x, y, z, u, v, width, height, builder);
        builder.draw();
    }

    /**
     * Renders a textured rectangle.<br>
     * Assumes the bound texture sheet dimensions to be 256 x 256 pixels.<br>
     * Takes in a BufferBuilder initialized in GL_QUADS, POSITION_TEX mode
     */
    public static void renderTexturedRectangle256(double x, double y, double z,
                                                  int u, int v,
                                                  int width, int height,
                                                  VertexBuilder builder)
    {
        float pixelSize = 0.00390625F; // 1 / 256
        renderTexturedRectangle(x, y, z, u, v, width, height, pixelSize, pixelSize, builder);
    }

    /**
     * Renders a textured rectangle with a custom sized texture sheet. The sheet size
     * is indicated by the pixelWidth and pixelHeight arguments, which are the
     * relative width and height of one pixel on the sheet.
     */
    public static void renderTexturedRectangle(double x, double y, double z,
                                               int u, int v,
                                               int renderWidth, int renderHeight,
                                               float pixelWidth, float pixelHeight, RenderContext ctx)
    {
        renderScaledTexturedRectangle(x, y, z, u, v,
                                      renderWidth, renderHeight,
                                      renderWidth, renderHeight,
                                      pixelWidth, pixelHeight, ctx);
    }

    /**
     * Renders a textured rectangle with a custom sized texture sheet. The sheet size
     * is indicated by the pixelWidth and pixelHeight arguments, which are the
     * relative width and height of one pixel on the sheet.
     * Takes in a VertexBuilder initialized in GL_QUADS, POSITION_TEX mode
     */
    public static void renderTexturedRectangle(double x, double y, double z,
                                               int u, int v,
                                               int renderWidth, int renderHeight,
                                               float pixelWidth, float pixelHeight,
                                               VertexBuilder builder)
    {
        renderScaledTexturedRectangle(x, y, z, u, v,
                                      renderWidth, renderHeight,
                                      renderWidth, renderHeight,
                                      pixelWidth, pixelHeight, builder);
    }

    /**
     * Renders a possibly scaled/stretched textured rectangle with a custom sized texture sheet.
     * The sheet size is indicated by the pixelWidth and pixelHeight arguments, which are the
     * relative width and height of one pixel on the sheet.
     * The renderWidth and renderHeight parameters are the rendered size of the rectangle/texture, whereas the
     * textureWidth and textureHeight parameters define which region of the texture sheet is used.
     */
    public static void renderScaledTexturedRectangle(double x, double y, double z,
                                                     int u, int v,
                                                     int renderWidth, int renderHeight,
                                                     int textureWidth, int textureHeight,
                                                     float pixelWidth, float pixelHeight, RenderContext ctx)
    {
        VertexBuilder builder = VanillaWrappingVertexBuilder.texturedQuad();

        renderScaledTexturedRectangle(x, y, z, u, v,
                                      renderWidth, renderHeight,
                                      textureWidth, textureHeight,
                                      pixelWidth, pixelHeight, builder);

        builder.draw();
    }

    /**
     * Renders a possibly scaled/stretched textured rectangle with a custom sized texture sheet.
     * The sheet size is indicated by the pixelWidth and pixelHeight arguments, which are the
     * relative width and height of one pixel on the sheet.
     * The width and height parameters are the rendered size of the rectangle/texture, whereas the
     * textureWidth and textureHeight parameters define which region of the texture sheet is used.
     * Takes in a VertexBuilder initialized in GL_QUADS, POSITION_TEX mode
     */
    public static void renderScaledTexturedRectangle(double x, double y, double z,
                                                     int u, int v,
                                                     int renderWidth, int renderHeight,
                                                     int textureWidth, int textureHeight,
                                                     float pixelWidth, float pixelHeight, VertexBuilder builder)
    {
        double x2 = x + renderWidth;
        double y2 = y + renderHeight;
        float u1 =  u                  * pixelWidth;
        float u2 = (u + textureWidth)  * pixelWidth;
        float v1 =  v                  * pixelHeight;
        float v2 = (v + textureHeight) * pixelHeight;

        builder.posUv(x , y2, z, u1, v2);
        builder.posUv(x2, y2, z, u2, v2);
        builder.posUv(x2, y , z, u2, v1);
        builder.posUv(x , y , z, u1, v1);
    }

    /**
     * Renders a possibly scaled/stretched textured rectangle with a tint color, from a custom sized texture sheet.
     * The sheet size is indicated by the pixelWidth and pixelHeight arguments, which are the
     * relative width and height of one pixel on the sheet.
     * The width and height parameters are the rendered size of the rectangle/texture, whereas the
     * textureWidth and textureHeight parameters define which region of the texture sheet is used.
     */
    public static void renderScaledTintedTexturedRectangle(float x, float y, float z, int u, int v,
                                                           int renderWidth, int renderHeight,
                                                           int textureWidth, int textureHeight,
                                                           float pixelWidth, float pixelHeight,
                                                           int backgroundTintColor, RenderContext ctx)
    {
        VertexBuilder builder = VanillaWrappingVertexBuilder.tintedTexturedQuad();

        renderScaledTintedTexturedRectangle(x, y, z, u, v, renderWidth, renderHeight, textureWidth, textureHeight,
                                            pixelWidth, pixelHeight, backgroundTintColor, builder);

        builder.draw();
    }

    /**
     * Renders a possibly scaled/stretched textured rectangle with a tint color, from a custom sized texture sheet.
     * The sheet size is indicated by the pixelWidth and pixelHeight arguments, which are the
     * relative width and height of one pixel on the sheet.
     * The width and height parameters are the rendered size of the rectangle/texture, whereas the
     * textureWidth and textureHeight parameters define which region of the texture sheet is used.
     * Takes in a VertexBuilder initialized in GL_QUADS, POSITION_TEX_COLOR mode
     */
    public static void renderScaledTintedTexturedRectangle(float x, float y, float z, int u, int v,
                                                           int renderWidth, int renderHeight,
                                                           int textureWidth, int textureHeight,
                                                           float pixelWidth, float pixelHeight,
                                                           int backgroundTintColor, VertexBuilder builder)
    {
        int a = (backgroundTintColor >>> 24) & 0xFF;
        int r = (backgroundTintColor >>> 16) & 0xFF;
        int g = (backgroundTintColor >>>  8) & 0xFF;
        int b = backgroundTintColor & 0xFF;

        float x2 = x + renderWidth;
        float y2 = y + renderHeight;
        float u1 = u                  * pixelWidth;
        float u2 = (u + textureWidth) * pixelWidth;
        float v1 = v                   * pixelHeight;
        float v2 = (v + textureHeight) * pixelHeight;

        builder.posUvColor(x , y2, z, u1, v2, r, g, b, a);
        builder.posUvColor(x2, y2, z, u2, v2, r, g, b, a);
        builder.posUvColor(x2, y , z, u2, v1, r, g, b, a);
        builder.posUvColor(x , y , z, u1, v1, r, g, b, a);
    }

    public static void renderGradientRectangle(float left, float top, float right, float bottom, float z,
                                               int startColor, int endColor, RenderContext ctx)
    {
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();
        renderGradientRectangle(left, top, right, bottom, z, startColor, endColor, builder);
        builder.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableAlpha();
    }

    /**
     * Takes in a VertexBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderGradientRectangle(float left, float top, float right, float bottom, float z,
                                               int startColor, int endColor, VertexBuilder builder)
    {
        int sa = (startColor >> 24) & 0xFF;
        int sr = (startColor >> 16) & 0xFF;
        int sg = (startColor >>  8) & 0xFF;
        int sb = startColor & 0xFF;

        int ea = (endColor >> 24) & 0xFF;
        int er = (endColor >> 16) & 0xFF;
        int eg = (endColor >>  8) & 0xFF;
        int eb = endColor & 0xFF;

        builder.posColor(right, top,    z, sr, sg, sb, sa);
        builder.posColor(left,  top,    z, sr, sg, sb, sa);
        builder.posColor(left,  bottom, z, er, eg, eb, ea);
        builder.posColor(right, bottom, z, er, eg, eb, ea);
    }

    public static void renderArc(double centerX, double centerY, double z, double radius,
                                 double startAngle, double endAngle, float lineWidth, int color, RenderContext ctx)
    {
        if (radius < 1)
        {
            return;
        }

        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >>  8) & 0xFF;
        int b = color & 0xFF;

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

        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredLineStrip();
        GlStateManager.glLineWidth(lineWidth);

        for (int i = 0; i <= steps; ++i)
        {
            double x = centerX + radius * Math.cos(lastAngle);
            double y = centerY + radius * Math.sin(lastAngle);

            builder.posColor(x, y, z, r, g, b, a);

            lastAngle += angleIncrement;
        }

        builder.draw();
    }

    /**
     * Renders the outline for a circle sector/segment.
     */
    public static void renderSectorOutline(double centerX, double centerY, double z,
                                           double innerRadius, double outerRadius,
                                           double startAngle, double endAngle,
                                           float lineWidth, int color, RenderContext ctx)
    {
        if (innerRadius < 1 || outerRadius < 1)
        {
            return;
        }

        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >>  8) & 0xFF;
        int b = color & 0xFF;

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

        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredLineLoop();
        GlStateManager.glLineWidth(lineWidth);

        // First render the inner arc in the positive direction
        for (int i = 0; i <= steps; ++i)
        {
            double x = centerX + innerRadius * Math.cos(lastAngle);
            double y = centerY + innerRadius * Math.sin(lastAngle);

            builder.posColor(x, y, z, r, g, b, a);

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

            builder.posColor(x, y, z, r, g, b, a);

            lastAngle -= angleIncrement;
        }

        builder.draw();
    }

    public static void renderSectorFill(double centerX, double centerY, double z,
                                        double innerRadius, double outerRadius,
                                        double startAngle, double endAngle, int color, RenderContext ctx)
    {
        if (innerRadius < 1 || outerRadius < 1)
        {
            return;
        }

        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >>  8) & 0xFF;
        int b = color & 0xFF;

        double twoPi = 2 * Math.PI;
        double arcAngle = (endAngle - startAngle) % twoPi;

        if (arcAngle < 0)
        {
            arcAngle += twoPi;
        }

        double arcLength = arcAngle * outerRadius;
        int steps = Math.max((int) Math.ceil(arcLength / 5.0), 2);
        double angleIncrement = arcAngle / (double) steps;

        double lastAngle = endAngle;
        double x, y;

        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredTriangleStrip();

        for (int i = 0; i <= steps; ++i)
        {
            x = centerX + innerRadius * Math.cos(lastAngle);
            y = centerY + innerRadius * Math.sin(lastAngle);

            builder.posColor(x, y, z, r, g, b, a);

            x = centerX + outerRadius * Math.cos(lastAngle);
            y = centerY + outerRadius * Math.sin(lastAngle);

            builder.posColor(x, y, z, r, g, b, a);

            lastAngle -= angleIncrement;
        }

        builder.draw();
    }

    /**
     * Takes in a VertexBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderBlockPosSideQuads(BlockPos pos, double expand, Color4f color, VertexBuilder builder)
    {
        renderBlockPosSideQuads(pos, expand, color, Vec3d.ZERO, builder);
    }

    /**
     * Takes in a VertexBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderBlockPosSideQuads(BlockPos pos, double expand, Color4f color,
                                               Vec3d cameraPos, VertexBuilder builder)
    {
        double minX = pos.getX() - expand - cameraPos.x;
        double minY = pos.getY() - expand - cameraPos.y;
        double minZ = pos.getZ() - expand - cameraPos.z;
        double maxX = pos.getX() + expand - cameraPos.x + 1;
        double maxY = pos.getY() + expand - cameraPos.y + 1;
        double maxZ = pos.getZ() + expand - cameraPos.z + 1;

        renderBoxSideQuads(minX, minY, minZ, maxX, maxY, maxZ, color, builder);
    }

    /**
     * Takes in a VertexBuilder initialized in GL_LINES, POSITION_COLOR mode
     */
    public static void renderBlockPosEdgeLines(BlockPos pos, double expand, Color4f color, VertexBuilder builder)
    {
        renderBlockPosEdgeLines(pos, expand, color, Vec3d.ZERO, builder);
    }

    /**
     * Takes in a VertexBuilder initialized in GL_LINES, POSITION_COLOR mode
     * The cameraPos value will be subtracted from the absolute coordinate values of the passed in BlockPos.
     */
    public static void renderBlockPosEdgeLines(BlockPos pos, double expand, Color4f color,
                                               Vec3d cameraPos, VertexBuilder builder)
    {
        double minX = pos.getX() - expand - cameraPos.x;
        double minY = pos.getY() - expand - cameraPos.y;
        double minZ = pos.getZ() - expand - cameraPos.z;
        double maxX = pos.getX() + expand - cameraPos.x + 1;
        double maxY = pos.getY() + expand - cameraPos.y + 1;
        double maxZ = pos.getZ() + expand - cameraPos.z + 1;

        renderBoxEdgeLines(minX, minY, minZ, maxX, maxY, maxZ, color, builder);
    }

    /**
     * Takes in a VertexBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderBoxSideQuads(double minX, double minY, double minZ,
                                          double maxX, double maxY, double maxZ,
                                          Color4f color, VertexBuilder builder)
    {
        renderBoxHorizontalSideQuads(minX, minY, minZ, maxX, maxY, maxZ, color, builder);
        renderBoxTopQuad(minX, minZ, maxX, maxY, maxZ, color, builder);
        renderBoxBottomQuad(minX, minY, minZ, maxX, maxZ, color, builder);
    }

    /**
     * Draws a box with outlines around the given corner positions.
     * Takes in VertexBuilders initialized for GL_QUADS and GL_LINES, POSITION_COLOR modes.
     * The cameraPos value will be subtracted from the absolute coordinate values of the passed in block positions.
     */
    public static void renderBoxSidesAndEdges(BlockPos posMin, BlockPos posMax,
                                              Color4f colorLines, Color4f colorSides, Vec3d cameraPos,
                                              VertexBuilder quadBuilder, VertexBuilder lineBuilder)
    {
        final double x1 = posMin.getX() - cameraPos.x;
        final double y1 = posMin.getY() - cameraPos.y;
        final double z1 = posMin.getZ() - cameraPos.z;
        final double x2 = posMax.getX() + 1 - cameraPos.x;
        final double y2 = posMax.getY() + 1 - cameraPos.y;
        final double z2 = posMax.getZ() + 1 - cameraPos.z;

        renderBoxSideQuads(x1, y1, z1, x2, y2, z2, colorSides, quadBuilder);
        renderBoxEdgeLines(x1, y1, z1, x2, y2, z2, colorLines, lineBuilder);
    }

    /**
     * Takes in a VertexBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderBoxHorizontalSideQuads(double minX, double minY, double minZ,
                                                    double maxX, double maxY, double maxZ,
                                                    Color4f color, VertexBuilder builder)
    {
        // West side
        builder.posColor(minX, minY, minZ, color);
        builder.posColor(minX, minY, maxZ, color);
        builder.posColor(minX, maxY, maxZ, color);
        builder.posColor(minX, maxY, minZ, color);

        // East side
        builder.posColor(maxX, minY, maxZ, color);
        builder.posColor(maxX, minY, minZ, color);
        builder.posColor(maxX, maxY, minZ, color);
        builder.posColor(maxX, maxY, maxZ, color);

        // North side
        builder.posColor(maxX, minY, minZ, color);
        builder.posColor(minX, minY, minZ, color);
        builder.posColor(minX, maxY, minZ, color);
        builder.posColor(maxX, maxY, minZ, color);

        // South side
        builder.posColor(minX, minY, maxZ, color);
        builder.posColor(maxX, minY, maxZ, color);
        builder.posColor(maxX, maxY, maxZ, color);
        builder.posColor(minX, maxY, maxZ, color);
    }

    /**
     * Takes in a VertexBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderBoxTopQuad(double minX, double minZ,
                                        double maxX, double maxY, double maxZ,
                                        Color4f color, VertexBuilder builder)
    {
        // Top side
        builder.posColor(minX, maxY, maxZ, color);
        builder.posColor(maxX, maxY, maxZ, color);
        builder.posColor(maxX, maxY, minZ, color);
        builder.posColor(minX, maxY, minZ, color);
    }

    /**
     * Takes in a VertexBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderBoxBottomQuad(double minX, double minY, double minZ,
                                           double maxX, double maxZ,
                                           Color4f color, VertexBuilder builder)
    {
        // Bottom side
        builder.posColor(maxX, minY, maxZ, color);
        builder.posColor(minX, minY, maxZ, color);
        builder.posColor(minX, minY, minZ, color);
        builder.posColor(maxX, minY, minZ, color);
    }

    /**
     * Takes in a VertexBuilder initialized in GL_LINES, POSITION_COLOR mode
     */
    public static void renderBoxEdgeLines(double minX, double minY, double minZ,
                                          double maxX, double maxY, double maxZ,
                                          Color4f color, VertexBuilder builder)
    {
        // West side
        builder.posColor(minX, minY, minZ, color);
        builder.posColor(minX, minY, maxZ, color);

        builder.posColor(minX, minY, maxZ, color);
        builder.posColor(minX, maxY, maxZ, color);

        builder.posColor(minX, maxY, maxZ, color);
        builder.posColor(minX, maxY, minZ, color);

        builder.posColor(minX, maxY, minZ, color);
        builder.posColor(minX, minY, minZ, color);

        // East side
        builder.posColor(maxX, minY, maxZ, color);
        builder.posColor(maxX, minY, minZ, color);

        builder.posColor(maxX, minY, minZ, color);
        builder.posColor(maxX, maxY, minZ, color);

        builder.posColor(maxX, maxY, minZ, color);
        builder.posColor(maxX, maxY, maxZ, color);

        builder.posColor(maxX, maxY, maxZ, color);
        builder.posColor(maxX, minY, maxZ, color);

        // North side (don't repeat the vertical lines that are done by the east/west sides)
        builder.posColor(maxX, minY, minZ, color);
        builder.posColor(minX, minY, minZ, color);

        builder.posColor(minX, maxY, minZ, color);
        builder.posColor(maxX, maxY, minZ, color);

        // South side (don't repeat the vertical lines that are done by the east/west sides)
        builder.posColor(minX, minY, maxZ, color);
        builder.posColor(maxX, minY, maxZ, color);

        builder.posColor(maxX, maxY, maxZ, color);
        builder.posColor(minX, maxY, maxZ, color);
    }

    /**
     * Takes in a VertexBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderBlockPosSideQuad(BlockPos pos, EnumFacing side, double expand,
                                              Color4f color, VertexBuilder builder)
    {
        renderBlockPosSideQuad(pos, side, expand, color, Vec3d.ZERO, builder);
    }

    /**
     * Takes in a VertexBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderBlockPosSideQuad(BlockPos pos, EnumFacing side, double expand,
                                              Color4f color, Vec3d cameraPos, VertexBuilder builder)
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
                builder.posColor(maxX, minY, maxZ, color);
                builder.posColor(minX, minY, maxZ, color);
                builder.posColor(minX, minY, minZ, color);
                builder.posColor(maxX, minY, minZ, color);
                break;

            case UP:
                builder.posColor(minX, maxY, maxZ, color);
                builder.posColor(maxX, maxY, maxZ, color);
                builder.posColor(maxX, maxY, minZ, color);
                builder.posColor(minX, maxY, minZ, color);
                break;

            case NORTH:
                builder.posColor(maxX, minY, minZ, color);
                builder.posColor(minX, minY, minZ, color);
                builder.posColor(minX, maxY, minZ, color);
                builder.posColor(maxX, maxY, minZ, color);
                break;

            case SOUTH:
                builder.posColor(minX, minY, maxZ, color);
                builder.posColor(maxX, minY, maxZ, color);
                builder.posColor(maxX, maxY, maxZ, color);
                builder.posColor(minX, maxY, maxZ, color);
                break;

            case WEST:
                builder.posColor(minX, minY, minZ, color);
                builder.posColor(minX, minY, maxZ, color);
                builder.posColor(minX, maxY, maxZ, color);
                builder.posColor(minX, maxY, minZ, color);
                break;

            case EAST:
                builder.posColor(maxX, minY, maxZ, color);
                builder.posColor(maxX, minY, minZ, color);
                builder.posColor(maxX, maxY, minZ, color);
                builder.posColor(maxX, maxY, maxZ, color);
                break;
        }
    }

    /**
     * Takes in VertexBuilders initialized for GL_QUADS and GL_LINES, POSITION_COLOR modes.
     */
    public static void renderBoxSidesAndEdges(IntBoundingBox bb, Color4f color, Vec3d cameraPos,
                                              VertexBuilder quadBuilder, VertexBuilder lineBuilder)
    {
        double minX = bb.minX - cameraPos.x;
        double minY = bb.minY - cameraPos.y;
        double minZ = bb.minZ - cameraPos.z;
        double maxX = bb.maxX - cameraPos.x + 1;
        double maxY = bb.maxY - cameraPos.y + 1;
        double maxZ = bb.maxZ - cameraPos.z + 1;

        renderBoxSideQuads(minX, minY, minZ, maxX, maxY, maxZ, color, quadBuilder);
        renderBoxEdgeLines(minX, minY, minZ, maxX, maxY, maxZ, color, lineBuilder);
    }
}
