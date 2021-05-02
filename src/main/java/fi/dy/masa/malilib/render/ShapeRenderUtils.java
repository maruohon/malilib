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
    public static void renderHorizontalLine(int x, int y, float z, int width, int color)
    {
        renderRectangle(x, y, z, width, 1, color);
    }

    public static void renderVerticalLine(int x, int y, float z, int height, int color)
    {
        renderRectangle(x, y, z, 1, height, color);
    }

    public static void renderGrid(int x, int y, float z, int width, int height,
                                  int gridInterval, int lineWidth, int color)
    {
        BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR, false);

        renderGrid(x, y, z, width, height, gridInterval, lineWidth, color, buffer);

        RenderUtils.drawBuffer();
    }

    public static void renderGrid(int x, int y, float z, int width, int height,
                                  int gridInterval, int lineWidth, int color, BufferBuilder buffer)
    {
        int endX = x + width;
        int endY = y + height;

        for (int tmpX = x; tmpX <= endX; tmpX += gridInterval)
        {
            renderRectangle(tmpX, y, z, lineWidth, height, color, buffer);
        }

        for (int tmpY = y; tmpY <= endY; tmpY += gridInterval)
        {
            renderRectangle(x, tmpY, z - 0.001f, width, lineWidth, color, buffer);
        }
    }

    public static void renderOutlinedRectangle(int x, int y, float z, int width, int height, int colorBg, int colorBorder)
    {
        BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR, false);

        // Draw the background
        renderRectangle(x + 1, y + 1, z, width - 2, height - 2, colorBg, buffer);

        // Draw the border
        renderOutline(x, y, z, width, height, 1, colorBorder, buffer);

        RenderUtils.drawBuffer();
    }

    public static void renderOutline(int x, int y, float z, int width, int height, int borderWidth, int colorBorder)
    {
        BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR, false);

        renderOutline(x, y, z, width, height, borderWidth, colorBorder, buffer);

        RenderUtils.drawBuffer();
    }

    /**
     * Takes in a BufferBuilder initialized in GL_QUADS, POSITION_COLOR mode
     */
    public static void renderOutline(int x, int y, float z, int width, int height,
                                     int borderWidth, int colorBorder, BufferBuilder buffer)
    {
        renderRectangle(x                      , y                       , z, borderWidth            , height     , colorBorder, buffer); // left edge
        renderRectangle(x + width - borderWidth, y                       , z, borderWidth            , height     , colorBorder, buffer); // right edge
        renderRectangle(x + borderWidth        , y                       , z, width - 2 * borderWidth, borderWidth, colorBorder, buffer); // top edge
        renderRectangle(x + borderWidth        , y + height - borderWidth, z, width - 2 * borderWidth, borderWidth, colorBorder, buffer); // bottom edge
    }

    public static void renderRectangle(int x, int y, float z, int width, int height, int color)
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

    public static void renderTexturedRectangle(int x, int y, float z, int u, int v, int width, int height)
    {
        BufferBuilder buffer = RenderUtils.startBuffer(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX, true);

        renderTexturedRectangle(x, y, z, u, v, width, height, buffer);

        RenderUtils.drawBuffer();
    }

    /**
     * Renders a textured rectangle.<br>
     * Assumes the bound texture sheet dimensions to be 256 x 256 pixels.<br>
     * Takes in a BufferBuilder initialized in GL_QUADS, POSITION_TEX mode
     */
    public static void renderTexturedRectangle(int x, int y, float z, int u, int v,
                                               int width, int height, BufferBuilder buffer)
    {
        float pixelWidth = 0.00390625F;

        buffer.pos(x        , y + height, z).tex(u           * pixelWidth, (v + height) * pixelWidth).endVertex();
        buffer.pos(x + width, y + height, z).tex((u + width) * pixelWidth, (v + height) * pixelWidth).endVertex();
        buffer.pos(x + width, y         , z).tex((u + width) * pixelWidth, v            * pixelWidth).endVertex();
        buffer.pos(x        , y         , z).tex(u           * pixelWidth, v            * pixelWidth).endVertex();
    }

    public static void renderGradientRectangle(int left, int top, int right, int bottom, double z,
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
    public static void renderGradientRectangle(int left, int top, int right, int bottom, double z,
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
