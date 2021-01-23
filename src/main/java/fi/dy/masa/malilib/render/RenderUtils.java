package fi.dy.masa.malilib.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.opengl.GL11;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.MapData;
import fi.dy.masa.malilib.config.value.HudAlignment;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.render.overlay.InventoryOverlay;
import fi.dy.masa.malilib.util.PositionUtils;
import fi.dy.masa.malilib.util.PositionUtils.HitPart;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.data.IntBoundingBox;
import fi.dy.masa.malilib.util.inventory.InventoryUtils;

public class RenderUtils
{
    public static final ResourceLocation TEXTURE_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    //private static final Vec3d LIGHT0_POS = (new Vec3d( 0.2D, 1.0D, -0.7D)).normalize();
    //private static final Vec3d LIGHT1_POS = (new Vec3d(-0.2D, 1.0D,  0.7D)).normalize();

    public static void setupBlend()
    {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }

    public static void setupBlendSimple()
    {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    public static void bindTexture(ResourceLocation texture)
    {
        mc().getTextureManager().bindTexture(texture);
    }

    public static void color(float r, float g, float b, float a)
    {
        GlStateManager.color(r, g, b, a);
    }

    public static void disableItemLighting()
    {
        RenderHelper.disableStandardItemLighting();
    }

    public static void enableItemLighting()
    {
        RenderHelper.enableStandardItemLighting();
    }

    public static void enableGuiItemLighting()
    {
        RenderHelper.enableGUIStandardItemLighting();
    }

    public static void renderOutlinedBox(int x, int y, int width, int height, int colorBg, int colorBorder, float zLevel)
    {
        // Draw the background
        renderRectangle(x + 1, y + 1, width - 2, height - 2, colorBg, zLevel);

        // Draw the border
        renderOutline(x, y, width, height, 1, colorBorder, zLevel);
    }

    public static void renderOutline(int x, int y, int width, int height, int borderWidth, int colorBorder, float zLevel)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.disableTexture2D();
        setupBlend();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        renderRectangleBatched(x                      , y, borderWidth            , height     , colorBorder, zLevel, buffer); // left edge
        renderRectangleBatched(x + width - borderWidth, y, borderWidth            , height     , colorBorder, zLevel, buffer); // right edge
        renderRectangleBatched(x + borderWidth        , y, width - 2 * borderWidth, borderWidth, colorBorder, zLevel, buffer); // top edge
        renderRectangleBatched(x + borderWidth        , y + height - borderWidth, width - 2 * borderWidth, borderWidth, colorBorder, zLevel, buffer); // bottom edge

        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

        color(1f, 1f, 1f, 1f);
    }

    public static void renderRectangle(int x, int y, int width, int height, int color, float zLevel)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.disableTexture2D();
        setupBlend();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        renderRectangleBatched(x, y, width, height, color, zLevel, buffer);

        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

        color(1f, 1f, 1f, 1f);
    }

    public static void renderRectangleBatched(int x, int y, int width, int height, int color, float zLevel, BufferBuilder buffer)
    {
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >>  8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        buffer.pos(x        , y         , zLevel).color(r, g, b, a).endVertex();
        buffer.pos(x        , y + height, zLevel).color(r, g, b, a).endVertex();
        buffer.pos(x + width, y + height, zLevel).color(r, g, b, a).endVertex();
        buffer.pos(x + width, y         , zLevel).color(r, g, b, a).endVertex();
    }

    public static void renderTexturedRectangle(int x, int y, int u, int v, int width, int height, float zLevel)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        renderTexturedRectangleBatched(x, y, u, v, width, height, zLevel, buffer);

        tessellator.draw();
    }

    public static void renderTexturedRectangleBatched(int x, int y, int u, int v, int width, int height, float zLevel, BufferBuilder buffer)
    {
        float pixelWidth = 0.00390625F;

        buffer.pos(x        , y + height, zLevel).tex( u          * pixelWidth, (v + height) * pixelWidth).endVertex();
        buffer.pos(x + width, y + height, zLevel).tex((u + width) * pixelWidth, (v + height) * pixelWidth).endVertex();
        buffer.pos(x + width, y         , zLevel).tex((u + width) * pixelWidth,  v           * pixelWidth).endVertex();
        buffer.pos(x        , y         , zLevel).tex( u          * pixelWidth,  v           * pixelWidth).endVertex();
    }

    public static void renderGradientRectangle(int left, int top, int right, int bottom, double zLevel, int startColor, int endColor)
    {
        float sa = (float)(startColor >> 24 & 0xFF) / 255.0F;
        float sr = (float)(startColor >> 16 & 0xFF) / 255.0F;
        float sg = (float)(startColor >>  8 & 0xFF) / 255.0F;
        float sb = (float)(startColor & 0xFF) / 255.0F;

        float ea = (float)(endColor >> 24 & 0xFF) / 255.0F;
        float er = (float)(endColor >> 16 & 0xFF) / 255.0F;
        float eg = (float)(endColor >>  8 & 0xFF) / 255.0F;
        float eb = (float)(endColor & 0xFF) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        setupBlend();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        buffer.pos(right, top,    zLevel).color(sr, sg, sb, sa).endVertex();
        buffer.pos(left,  top,    zLevel).color(sr, sg, sb, sa).endVertex();
        buffer.pos(left,  bottom, zLevel).color(er, eg, eb, ea).endVertex();
        buffer.pos(right, bottom, zLevel).color(er, eg, eb, ea).endVertex();

        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void renderHorizontalLine(int x, int y, int width, int color, float zLevel)
    {
        renderRectangle(x, y, width, 1, color, zLevel);
    }

    public static void renderVerticalLine(int x, int y, int height, int color, float zLevel)
    {
        renderRectangle(x, y, 1, height, color, zLevel);
    }

    public static void renderSprite(int x, int y, int width, int height, int zLevel, String texture)
    {
        if (texture != null)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0f, 0f, zLevel);

            GlStateManager.disableLighting();
            TextureAtlasSprite sprite = mc().getTextureMapBlocks().getAtlasSprite(texture);
            mc().ingameGUI.drawTexturedModalRect(x, y, sprite, width, height);

            GlStateManager.popMatrix();
        }
    }

    public static void renderNineSplicedTexture(int x, int y, int u, int v, int width, int height,
                                                int texWidth, int texHeight, int edgeThickness, float zLevel)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        int e = edgeThickness;
        
        RenderUtils.renderTexturedRectangleBatched(x, y             , u, v                , e, e, zLevel, buffer); // top left
        RenderUtils.renderTexturedRectangleBatched(x, y + height - e, u, v + texHeight - e, e, e, zLevel, buffer); // bottom left

        RenderUtils.renderTexturedRectangleBatched(x + width - e, y             , u + texWidth - e, v                , e, e, zLevel, buffer); // top right
        RenderUtils.renderTexturedRectangleBatched(x + width - e, y + height - e, u + texWidth - e, v + texHeight - e, e, e, zLevel, buffer); // bottom right

        // Texture is smaller than the requested width, repeat stuff horizontally
        if (texWidth < width)
        {
            final int repeatableWidth = texWidth - 2 * e;
            final int requiredWidth = width - 2 * e;

            for (int doneWidth = 0, tmpX = x + e, tmpW; doneWidth < requiredWidth; )
            {
                tmpW = Math.min(repeatableWidth, requiredWidth - doneWidth);

                RenderUtils.renderTexturedRectangleBatched(tmpX, y             , u + e, v                , tmpW, e, zLevel, buffer); // top center
                RenderUtils.renderTexturedRectangleBatched(tmpX, y + height - e, u + e, v + texHeight - e, tmpW, e, zLevel, buffer); // bottom center

                tmpX += tmpW;
                doneWidth += tmpW;
            }
        }
        // Texture is wide enough, no need to repeat horizontally
        else
        {
            RenderUtils.renderTexturedRectangleBatched(x + e, y             , u + e, v                , width - 2 * e, e, zLevel, buffer); // top center
            RenderUtils.renderTexturedRectangleBatched(x + e, y + height - e, u + e, v + texHeight - e, width - 2 * e, e, zLevel, buffer); // bottom center
        }

        // Texture is smaller than the requested height, repeat stuff vertically
        if (texHeight < height)
        {
            final int repeatableHeight = texHeight - 2 * e;
            final int requiredHeight = height - 2 * e;

            for (int doneHeight = 0, tmpY = y + e, tmpH; doneHeight < requiredHeight; )
            {
                tmpH = Math.min(repeatableHeight, requiredHeight - doneHeight);

                RenderUtils.renderTexturedRectangleBatched(x            , tmpY, u               , v + e, e, tmpH, zLevel, buffer); // left center
                RenderUtils.renderTexturedRectangleBatched(x + width - e, tmpY, u + texWidth - e, v + e, e, tmpH, zLevel, buffer); // right center

                tmpY += tmpH;
                doneHeight += tmpH;
            }
        }
        // Texture is tall enough, no need to repeat vertically
        else
        {
            RenderUtils.renderTexturedRectangleBatched(x            , y + e, u               , v + e, e, height - 2 * e, zLevel, buffer); // left center
            RenderUtils.renderTexturedRectangleBatched(x + width - e, y + e, u + texWidth - e, v + e, e, height - 2 * e, zLevel, buffer); // right center
        }

        // The center part needs to be repeated
        if (texWidth < width || texHeight < height)
        {
            final int repeatableWidth = texWidth - 2 * e;
            final int requiredWidth = width - 2 * e;

            for (int doneWidth = 0, tmpX = x + e, tmpW; doneWidth < requiredWidth; )
            {
                final int repeatableHeight = texHeight - 2 * e;
                final int requiredHeight = height - 2 * e;
                tmpW = Math.min(repeatableWidth, requiredWidth - doneWidth);

                for (int doneHeight = 0, tmpY = y + e, tmpH; doneHeight < requiredHeight; )
                {
                    tmpH = Math.min(repeatableHeight, requiredHeight - doneHeight);

                    RenderUtils.renderTexturedRectangleBatched(tmpX, tmpY, u + e, v + e, tmpW, tmpH, zLevel, buffer); // center

                    tmpY += tmpH;
                    doneHeight += tmpH;
                }

                tmpX += tmpW;
                doneWidth += tmpW;
            }
        }
        else
        {
            RenderUtils.renderTexturedRectangleBatched(x + e, y + e, u + e, v + e, width - 2 * e, height - 2 * e, zLevel, buffer); // center
        }

        tessellator.draw();
    }

    public static void renderText(int x, int y, int color, String text)
    {
        String[] parts = text.split("\\\\n");
        FontRenderer textRenderer = mc().fontRenderer;

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
            FontRenderer textRenderer = mc().fontRenderer;

            for (String line : lines)
            {
                textRenderer.drawString(line, x, y, color);
                y += textRenderer.FONT_HEIGHT + 2;
            }
        }
    }

    public static int renderText(int xOff, int yOff, int zLevel, double scale, int textColor, int bgColor,
                                 HudAlignment alignment, boolean useBackground, boolean useShadow, List<String> lines)
    {
        FontRenderer fontRenderer = mc().fontRenderer;
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
        posY += GuiUtils.getHudOffsetForPotions(alignment, scale, mc().player);

        for (String line : lines)
        {
            final int width = fontRenderer.getStringWidth(line);

            switch (alignment)
            {
                case TOP_RIGHT:
                case BOTTOM_RIGHT:
                    posX = (scaledWidth / scale) - width - xOff - bgMargin;
                    break;
                case CENTER:
                    posX = (scaledWidth / scale / 2) - (width / 2) - xOff;
                    break;
                default:
            }

            final int x = (int) posX;
            final int y = (int) posY;
            posY += lineHeight;

            if (useBackground)
            {
                renderRectangle(x - bgMargin, y - bgMargin, width + bgMargin, bgMargin + fontRenderer.FONT_HEIGHT, bgColor, zLevel);
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

    public static void renderHoverText(int x, int y, float zLevel, String text)
    {
        renderHoverText(x, y, zLevel, Collections.singletonList(text));
    }

    public static void renderHoverText(int x, int y, float zLevel, List<String> textLines)
    {
        renderHoverText(x, y, zLevel, textLines, 0xFFC0C0C0 , RenderUtils::renderHoverTextBackground);
    }

    public static void renderHoverText(int x, int y, float zLevel, List<String> textLines,
                                       int textColor, RectangleRenderer backgroundRenderer)
    {
        Minecraft mc = mc();

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

            final int lineHeight = font.FONT_HEIGHT + 1;
            int maxWidth = GuiUtils.getCurrentScreen().width;
            int textHeight = textLines.size() * lineHeight - 2;
            int textStartX = x + 4;
            int textStartY = Math.max(8, y - textHeight - 6);

            // The text can't fit from the cursor to the right edge of the screen
            if (textStartX + maxLineLength + 6 > maxWidth)
            {
                int leftX = x - maxLineLength - 8;

                // If the text fits from the cursor to the left edge of the screen...
                if (leftX >= 4)
                {
                    textStartX = leftX;
                }
                // otherwise move it to touching the edge of the screen that the cursor is closest to
                else
                {
                    textStartX = x < (maxWidth / 2) ? 4 : Math.max(4, maxWidth - maxLineLength - 6);
                }
            }

            // The hover info would overlap the cursor vertically
            // (because the hover info was clamped to the top of the screen),
            // move it below the cursor instead
            if (textStartY < y && y < textStartY + textHeight)
            {
                textStartY = y + 16;
            }

            GlStateManager.disableRescaleNormal();
            disableItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();

            backgroundRenderer.render(textStartX, textStartY, maxLineLength, textHeight, zLevel);

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

    public static void renderHoverTextBackground(int x, int y, int width, int height, float zLevel)
    {
        int borderColor = 0xF0100010;
        renderGradientRectangle(x - 3        , y - 4         , x + width + 3, y - 3         , zLevel, borderColor, borderColor);
        renderGradientRectangle(x - 3        , y + height + 3, x + width + 3, y + height + 4, zLevel, borderColor, borderColor);
        renderGradientRectangle(x - 3        , y - 3         , x + width + 3, y + height + 3, zLevel, borderColor, borderColor);
        renderGradientRectangle(x - 4        , y - 3         , x - 3        , y + height + 3, zLevel, borderColor, borderColor);
        renderGradientRectangle(x + width + 3, y - 3         , x + width + 4, y + height + 3, zLevel, borderColor, borderColor);

        int fillColor1 = 0x505000FF;
        int fillColor2 = 0x5028007F;
        renderGradientRectangle(x - 3        , y - 3 + 1     , x - 3 + 1    , y + height + 3 - 1, zLevel, fillColor1, fillColor2);
        renderGradientRectangle(x + width + 2, y - 3 + 1     , x + width + 3, y + height + 3 - 1, zLevel, fillColor1, fillColor2);
        renderGradientRectangle(x - 3        , y - 3         , x + width + 3, y - 3 + 1         , zLevel, fillColor1, fillColor1);
        renderGradientRectangle(x - 3        , y + height + 2, x + width + 3, y + height + 3    , zLevel, fillColor2, fillColor2);
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void renderBlockSpaceAllSidesBatchedQuads(BlockPos pos, Color4f color,
                                                            double expand, BufferBuilder buffer)
    {
        renderBlockSpaceAllSidesBatchedQuads(pos, Vec3d.ZERO, color, expand, buffer);
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     * @param pos
     * @param cameraPos
     * @param color
     * @param expand
     * @param buffer
     */
    public static void renderBlockSpaceAllSidesBatchedQuads(BlockPos pos, Vec3d cameraPos, Color4f color,
                                                            double expand, BufferBuilder buffer)
    {
        double minX = pos.getX() - expand - cameraPos.x;
        double minY = pos.getY() - expand - cameraPos.y;
        double minZ = pos.getZ() - expand - cameraPos.z;
        double maxX = pos.getX() + expand - cameraPos.x + 1;
        double maxY = pos.getY() + expand - cameraPos.y + 1;
        double maxZ = pos.getZ() + expand - cameraPos.z + 1;

        renderBoxAllSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
    }

    /**
     * Assumes a BufferBuilder in GL_LINES mode has been initialized
     */
    public static void renderBlockSpaceAllOutlinesBatchedLines(BlockPos pos, Color4f color,
                                                               double expand, BufferBuilder buffer)
    {
        renderBlockSpaceAllOutlinesBatchedLines(pos, Vec3d.ZERO, color, expand, buffer);
    }

    /**
     * Assumes a BufferBuilder in GL_LINES mode has been initialized.
     * The cameraPos value will be subtracted from the absolute coordinate values of the passed in BlockPos.
     * @param pos
     * @param cameraPos
     * @param color
     * @param expand
     * @param buffer
     */
    public static void renderBlockSpaceAllOutlinesBatchedLines(BlockPos pos, Vec3d cameraPos, Color4f color,
                                                               double expand, BufferBuilder buffer)
    {
        double minX = pos.getX() - expand - cameraPos.x;
        double minY = pos.getY() - expand - cameraPos.y;
        double minZ = pos.getZ() - expand - cameraPos.z;
        double maxX = pos.getX() + expand - cameraPos.x + 1;
        double maxY = pos.getY() + expand - cameraPos.y + 1;
        double maxZ = pos.getZ() + expand - cameraPos.z + 1;

        renderBoxAllEdgesBatchedLines(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void renderBoxAllSidesBatchedQuads(double minX, double minY, double minZ,
                                                     double maxX, double maxY, double maxZ,
                                                     Color4f color, BufferBuilder buffer)
    {
        renderBoxHorizontalSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
        renderBoxTopBatchedQuads(minX, minZ, maxX, maxY, maxZ, color, buffer);
        renderBoxBottomBatchedQuads(minX, minY, minZ, maxX, maxZ, color, buffer);
    }

    /**
     * Draws a box with outlines around the given corner positions.
     * Takes in buffers initialized for GL_QUADS and GL_LINES modes.
     * @param posMin
     * @param posMax
     * @param colorLines
     * @param colorSides
     * @param bufferQuads
     * @param bufferLines
     */
    public static void renderBoxWithEdgesBatched(BlockPos posMin, BlockPos posMax,
                                                 Color4f colorLines, Color4f colorSides,
                                                 BufferBuilder bufferQuads, BufferBuilder bufferLines)
    {
        renderBoxWithEdgesBatched(posMin, posMax, Vec3d.ZERO, colorLines, colorSides, bufferQuads, bufferLines);
    }

    /**
     * Draws a box with outlines around the given corner positions.
     * Takes in buffers initialized for GL_QUADS and GL_LINES modes.
     * The cameraPos value will be subtracted from the absolute coordinate values of the passed in block positions.
     * @param posMin
     * @param posMax
     * @param cameraPos
     * @param colorLines
     * @param colorSides
     * @param bufferQuads
     * @param bufferLines
     */
    public static void renderBoxWithEdgesBatched(BlockPos posMin, BlockPos posMax, Vec3d cameraPos,
                                                 Color4f colorLines, Color4f colorSides,
                                                 BufferBuilder bufferQuads, BufferBuilder bufferLines)
    {
        final double x1 = posMin.getX() - cameraPos.x;
        final double y1 = posMin.getY() - cameraPos.y;
        final double z1 = posMin.getZ() - cameraPos.z;
        final double x2 = posMax.getX() + 1 - cameraPos.x;
        final double y2 = posMax.getY() + 1 - cameraPos.y;
        final double z2 = posMax.getZ() + 1 - cameraPos.z;

        renderBoxAllSidesBatchedQuads(x1, y1, z1, x2, y2, z2, colorSides, bufferQuads);
        renderBoxAllEdgesBatchedLines(x1, y1, z1, x2, y2, z2, colorLines, bufferLines);
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void renderBoxHorizontalSidesBatchedQuads(double minX, double minY, double minZ,
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
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void renderBoxTopBatchedQuads(double minX, double minZ,
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
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void renderBoxBottomBatchedQuads(double minX, double minY, double minZ,
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
     * Assumes a BufferBuilder in GL_LINES mode has been initialized
     */
    public static void renderBoxAllEdgesBatchedLines(double minX, double minY, double minZ,
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
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void renderBlockSpaceSideBatchedQuads(BlockPos pos, EnumFacing side,
                                                        Color4f color, double expand, BufferBuilder buffer)
    {
        renderBlockSpaceSideBatchedQuads(pos, Vec3d.ZERO, side, color, expand, buffer);
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void renderBlockSpaceSideBatchedQuads(BlockPos pos, Vec3d cameraPos, EnumFacing side,
                                                        Color4f color, double expand, BufferBuilder buffer)
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

    public static void renderBox(IntBoundingBox bb, Vec3d cameraPos, Color4f color,
                                 BufferBuilder bufferQuads, BufferBuilder bufferLines)
    {
        double minX = bb.minX - cameraPos.x;
        double minY = bb.minY - cameraPos.y;
        double minZ = bb.minZ - cameraPos.z;
        double maxX = bb.maxX - cameraPos.x + 1;
        double maxY = bb.maxY - cameraPos.y + 1;
        double maxZ = bb.maxZ - cameraPos.z + 1;

        renderBoxAllSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, bufferQuads);
        renderBoxAllEdgesBatchedLines(minX, minY, minZ, maxX, maxY, maxZ, color, bufferLines);
    }

    public static void renderBox(StructureBoundingBox bb, Color4f color,
                                 BufferBuilder bufferQuads, BufferBuilder bufferLines)
    {
        double minX = bb.minX;
        double minY = bb.minY;
        double minZ = bb.minZ;
        double maxX = bb.maxX + 1;
        double maxY = bb.maxY + 1;
        double maxZ = bb.maxZ + 1;

        renderBoxAllSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, bufferQuads);
        renderBoxAllEdgesBatchedLines(minX, minY, minZ, maxX, maxY, maxZ, color, bufferLines);
    }

    /**
     * Renders a text plate/billboard, similar to the player name plate.<br>
     * The plate will always face towards the viewer.
     * @param text
     * @param x
     * @param y
     * @param z
     * @param scale
     */
    public static void renderTextPlate(List<String> text, double x, double y, double z, float scale)
    {
        Entity entity = mc().getRenderViewEntity();

        if (entity != null)
        {
            renderTextPlate(text, x, y, z, entity.rotationYaw, entity.rotationPitch, scale, 0xFFFFFFFF, 0x40000000, true);
        }
    }

    public static void renderTextPlate(List<String> text, double x, double y, double z, float yaw, float pitch,
                                       float scale, int textColor, int bgColor, boolean disableDepth)
    {
        FontRenderer textRenderer = mc().fontRenderer;

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);

        GlStateManager.rotate(-yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);

        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();

        setupBlend();
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

        color(1f, 1f, 1f, 1f);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void renderBlockTargetingOverlay(Entity entity, BlockPos pos, EnumFacing side, Vec3d hitVec,
                                                   Color4f color, float partialTicks)
    {
        EnumFacing playerFacing = entity.getHorizontalFacing();
        HitPart part = PositionUtils.getHitPart(side, playerFacing, pos, hitVec);

        double dx = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        double dy = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        double dz = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

        double x = pos.getX() + 0.5d - dx;
        double y = pos.getY() + 0.5d - dy;
        double z = pos.getZ() + 0.5d - dz;

        GlStateManager.pushMatrix();

        blockTargetingOverlayTranslations(x, y, z, side, playerFacing);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float quadAlpha = 0.18f;
        float ha = color.a;
        float hr = color.r;
        float hg = color.g;
        float hb = color.b;

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // White full block background
        buffer.pos(x - 0.5, y - 0.5, z).color(1f, 1f, 1f, quadAlpha).endVertex();
        buffer.pos(x + 0.5, y - 0.5, z).color(1f, 1f, 1f, quadAlpha).endVertex();
        buffer.pos(x + 0.5, y + 0.5, z).color(1f, 1f, 1f, quadAlpha).endVertex();
        buffer.pos(x - 0.5, y + 0.5, z).color(1f, 1f, 1f, quadAlpha).endVertex();

        switch (part)
        {
            case CENTER:
                buffer.pos(x - 0.25, y - 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.25, y - 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.25, y + 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x - 0.25, y + 0.25, z).color(hr, hg, hb, ha).endVertex();
                break;
            case LEFT:
                buffer.pos(x - 0.50, y - 0.50, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x - 0.25, y - 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x - 0.25, y + 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x - 0.50, y + 0.50, z).color(hr, hg, hb, ha).endVertex();
                break;
            case RIGHT:
                buffer.pos(x + 0.50, y - 0.50, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.25, y - 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.25, y + 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.50, y + 0.50, z).color(hr, hg, hb, ha).endVertex();
                break;
            case TOP:
                buffer.pos(x - 0.50, y + 0.50, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x - 0.25, y + 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.25, y + 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.50, y + 0.50, z).color(hr, hg, hb, ha).endVertex();
                break;
            case BOTTOM:
                buffer.pos(x - 0.50, y - 0.50, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x - 0.25, y - 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.25, y - 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.50, y - 0.50, z).color(hr, hg, hb, ha).endVertex();
                break;
            default:
        }

        tessellator.draw();

        GlStateManager.glLineWidth(1.6f);

        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);

        // Middle small rectangle
        buffer.pos(x - 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x + 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x + 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x - 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).endVertex();
        tessellator.draw();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        // Bottom left
        buffer.pos(x - 0.50, y - 0.50, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x - 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).endVertex();

        // Top left
        buffer.pos(x - 0.50, y + 0.50, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x - 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).endVertex();

        // Bottom right
        buffer.pos(x + 0.50, y - 0.50, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x + 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).endVertex();

        // Top right
        buffer.pos(x + 0.50, y + 0.50, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x + 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).endVertex();
        tessellator.draw();

        GlStateManager.popMatrix();
    }

    public static void renderBlockTargetingOverlaySimple(Entity entity, BlockPos pos, EnumFacing side,
                                                         Color4f color, float partialTicks)
    {
        EnumFacing playerFacing = entity.getHorizontalFacing();

        double dx = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        double dy = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        double dz = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

        double x = pos.getX() + 0.5d - dx;
        double y = pos.getY() + 0.5d - dy;
        double z = pos.getZ() + 0.5d - dz;

        GlStateManager.pushMatrix();

        blockTargetingOverlayTranslations(x, y, z, side, playerFacing);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        float a = color.a;
        float r = color.r;
        float g = color.g;
        float b = color.b;

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // Simple colored quad
        buffer.pos(x - 0.5, y - 0.5, z).color(r, g, b, a).endVertex();
        buffer.pos(x + 0.5, y - 0.5, z).color(r, g, b, a).endVertex();
        buffer.pos(x + 0.5, y + 0.5, z).color(r, g, b, a).endVertex();
        buffer.pos(x - 0.5, y + 0.5, z).color(r, g, b, a).endVertex();

        tessellator.draw();

        GlStateManager.glLineWidth(1.6f);

        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);

        // Middle rectangle
        buffer.pos(x - 0.375, y - 0.375, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x + 0.375, y - 0.375, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x + 0.375, y + 0.375, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x - 0.375, y + 0.375, z).color(1f, 1f, 1f, 1f).endVertex();

        tessellator.draw();

        GlStateManager.popMatrix();
    }

    private static void blockTargetingOverlayTranslations(double x, double y, double z,
                                                          EnumFacing side, EnumFacing playerFacing)
    {
        GlStateManager.translate(x, y, z);

        switch (side)
        {
            case DOWN:
                GlStateManager.rotate(180f - playerFacing.getHorizontalAngle(), 0, 1f, 0);
                GlStateManager.rotate( 90f, 1f, 0, 0);
                break;
            case UP:
                GlStateManager.rotate(180f - playerFacing.getHorizontalAngle(), 0, 1f, 0);
                GlStateManager.rotate(-90f, 1f, 0, 0);
                break;
            case NORTH:
                GlStateManager.rotate(180f, 0, 1f, 0);
                break;
            case SOUTH:
                GlStateManager.rotate(   0, 0, 1f, 0);
                break;
            case WEST:
                GlStateManager.rotate(-90f, 0, 1f, 0);
                break;
            case EAST:
                GlStateManager.rotate( 90f, 0, 1f, 0);
                break;
        }

        GlStateManager.translate(-x, -y, -z + 0.501);
    }

    public static void renderMapPreview(ItemStack stack, int x, int y, int dimensions)
    {
        if (stack.getItem() instanceof ItemMap && BaseScreen.isShiftDown())
        {
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            color(1f, 1f, 1f, 1f);

            int y1 = y - dimensions - 20;
            int y2 = y1 + dimensions;
            int x1 = x + 8;
            int x2 = x1 + dimensions;
            int z = 300;

            bindTexture(fi.dy.masa.malilib.render.RenderUtils.TEXTURE_MAP_BACKGROUND);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(x1, y2, z).tex(0.0D, 1.0D).endVertex();
            buffer.pos(x2, y2, z).tex(1.0D, 1.0D).endVertex();
            buffer.pos(x2, y1, z).tex(1.0D, 0.0D).endVertex();
            buffer.pos(x1, y1, z).tex(0.0D, 0.0D).endVertex();

            tessellator.draw();

            MapData mapdata = Items.FILLED_MAP.getMapData(stack, mc().world);

            if (mapdata != null)
            {
                x1 += 8;
                y1 += 8;
                z = 310;
                double scale = (double) (dimensions - 16) / 128.0D;
                GlStateManager.translate(x1, y1, z);
                GlStateManager.scale(scale, scale, 0);
                mc().entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
            }

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();

            color(1f, 1f, 1f, 1f);
        }
    }

    public static void renderShulkerBoxPreview(ItemStack stack, int x, int y, boolean useBgColors)
    {
        if (stack.hasTagCompound())
        {
            NonNullList<ItemStack> items = InventoryUtils.getStoredItems(stack, -1);

            if (items.size() == 0)
            {
                return;
            }

            GlStateManager.pushMatrix();
            disableItemLighting();
            GlStateManager.translate(0F, 0F, 300F);

            InventoryOverlay.InventoryRenderType type = InventoryOverlay.getInventoryType(stack);
            InventoryOverlay.InventoryProperties props = InventoryOverlay.getInventoryPropsTemp(type, items.size());

            int screenWidth = GuiUtils.getScaledWindowWidth();
            int screenHeight = GuiUtils.getScaledWindowHeight();
            int z = 0;
            x += 8;
            y = Math.max(y - (props.height + 18), 2);

            if (x + props.width + 2 > screenWidth)
            {
                x = Math.max(x - props.width - 16, 2);
            }

            if (y + props.height + 2 > screenHeight)
            {
                y = screenHeight - props.height - 2;
            }

            if (stack.getItem() instanceof ItemShulkerBox)
            {
                setShulkerBoxBackgroundTintColor((BlockShulkerBox) ((ItemBlock) stack.getItem()).getBlock(), useBgColors);
            }
            else
            {
                color(1f, 1f, 1f, 1f);
            }

            InventoryOverlay.renderInventoryBackground(type, x, y, z, props.slotsPerRow, items.size(), mc());

            enableGuiItemLighting();
            GlStateManager.enableDepth();
            GlStateManager.enableRescaleNormal();

            IInventory inv = InventoryUtils.getAsInventory(items);
            InventoryOverlay.renderInventoryStacks(type, inv, x + props.slotOffsetX, y + props.slotOffsetY, z + 1, props.slotsPerRow, 0, -1, mc());

            GlStateManager.disableDepth();
            GlStateManager.popMatrix();

            color(1f, 1f, 1f, 1f);
        }
    }

    /**
     * Calls RenderUtils.color() with the dye color of the provided shulker box block's color
     * @param block
     * @param useBgColors
     */
    public static void setShulkerBoxBackgroundTintColor(@Nullable BlockShulkerBox block, boolean useBgColors)
    {
        // In 1.13+ there is the separate uncolored Shulker Box variant,
        // which returns null from getColor().
        // In that case don't tint the background.
        if (useBgColors && block != null && block.getColor() != null)
        {
            final EnumDyeColor dye = block.getColor();
            final float[] colors = dye.getColorComponentValues();
            color(colors[0], colors[1], colors[2], 1f);
        }
        else
        {
            color(1f, 1f, 1f, 1f);
        }
    }

    public static void renderModelInGui(int x, int y, float zLevel, IBakedModel model, IBlockState state)
    {
        if (state.getBlock() == Blocks.AIR)
        {
            return;
        }

        GlStateManager.pushMatrix();

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01F);
        setupBlendSimple();
        color(1f, 1f, 1f, 1f);

        setupGuiTransform(x, y, model.isGui3d(), zLevel);
        //model.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GUI);
        GlStateManager.rotate( 30, 1, 0, 0);
        GlStateManager.rotate(225, 0, 1, 0);
        GlStateManager.scale(0.625, 0.625, 0.625);

        renderModel(model, state, zLevel);

        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();

        color(1f, 1f, 1f, 1f);
    }

    public static void setupGuiTransform(int xPosition, int yPosition, boolean isGui3d, float zLevel)
    {
        GlStateManager.translate(xPosition, yPosition, 100.0F + zLevel);
        GlStateManager.translate(8.0F, 8.0F, 0.0F);
        GlStateManager.scale(1.0F, -1.0F, 1.0F);
        GlStateManager.scale(16.0F, 16.0F, 16.0F);

        if (isGui3d)
        {
            GlStateManager.enableLighting();
        }
        else
        {
            GlStateManager.disableLighting();
        }
    }

    private static void renderModel(IBakedModel model, IBlockState state, float zLevel)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        int color = 0xFFFFFFFF;

        if (model.isBuiltInRenderer() == false)
        {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);

            for (EnumFacing enumfacing : EnumFacing.values())
            {
                renderQuads(bufferbuilder, model.getQuads(state, enumfacing, 0L), state, color);
            }

            renderQuads(bufferbuilder, model.getQuads(state, null, 0L), state, color);
            tessellator.draw();
        }

        GlStateManager.popMatrix();
    }

    public static void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, IBlockState state, int color)
    {
        for (BakedQuad quad : quads)
        {
            renderQuad(renderer, quad, state, 0xFFFFFFFF);
        }
    }

    public static void renderQuad(BufferBuilder buffer, BakedQuad quad, IBlockState state, int color)
    {
        buffer.addVertexData(quad.getVertexData());
        buffer.putColor4(color);

        if (quad.hasTintIndex())
        {
            BlockColors blockColors = mc().getBlockColors();
            int m = blockColors.colorMultiplier(state, null, null, quad.getTintIndex());

            float r = (float) (m >>> 16 & 0xFF) / 255F;
            float g = (float) (m >>>  8 & 0xFF) / 255F;
            float b = (float) (m        & 0xFF) / 255F;
            buffer.putColorMultiplier(r, g, b, 4);
            buffer.putColorMultiplier(r, g, b, 3);
            buffer.putColorMultiplier(r, g, b, 2);
            buffer.putColorMultiplier(r, g, b, 1);
        }

        putQuadNormal(buffer, quad);
    }

    public static void putQuadNormal(BufferBuilder buffer, BakedQuad quad)
    {
        Vec3i direction = quad.getFace().getDirectionVec();
        buffer.putNormal(direction.getX(), direction.getY(), direction.getZ());
    }

    /**
     * Renders the given model to the given vertex consumer.
     * Needs a vertex consumer initialized with mode GL11.GL_QUADS and DefaultVertexFormats.ITEM
     */
    public static void renderModelBrightnessColor(IBakedModel model, Vec3d pos, BufferBuilder buffer)
    {
        renderModelBrightnessColor(model, pos, null, 1f, 1f, 1f, 1f, buffer);
    }

    /**
     * Renders the given model to the given vertex consumer.
     * Needs a vertex consumer initialized with mode GL11.GL_QUADS and DefaultVertexFormats.ITEM
     */
    public static void renderModelBrightnessColor(IBakedModel model, Vec3d pos, @Nullable IBlockState state,
                                                  float brightness, float r, float g, float b, BufferBuilder buffer)
    {
        for (EnumFacing side : PositionUtils.ALL_DIRECTIONS)
        {
            renderQuads(model.getQuads(state, side, 0L), pos, brightness, r, g, b, buffer);
        }

        renderQuads(model.getQuads(state, null, 0L), pos, brightness, r, g, b, buffer);
    }

    /**
     * Renders the given quads to the given vertex consumer.
     * Needs a vertex consumer initialized with mode GL11.GL_QUADS and DefaultVertexFormats.ITEM
     */
    public static void renderQuads(List<BakedQuad> quads, Vec3d pos, float brightness,
                                   float red, float green, float blue, BufferBuilder buffer)
    {
        for (BakedQuad quad : quads)
        {
            buffer.addVertexData(quad.getVertexData());

            if (quad.hasTintIndex())
            {
                buffer.putColorRGB_F4(red * brightness, green * brightness, blue * brightness);
            }
            else
            {
                buffer.putColorRGB_F4(brightness, brightness, brightness);
            }

            buffer.putPosition(pos.x, pos.y, pos.z);
            putQuadNormal(buffer, quad);
        }
    }

    private static Minecraft mc()
    {
        return Minecraft.getMinecraft();
    }

    /*
    public static void enableGUIStandardItemLighting(float scale)
    {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(165.0F, 1.0F, 0.0F, 0.0F);

        enableStandardItemLighting(scale);

        GlStateManager.popMatrix();
    }

    public static void enableStandardItemLighting(float scale)
    {
        GlStateManager.enableLighting();
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);
        GlStateManager.enableColorMaterial();
        RenderUtils.colorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
        GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, RenderHelper.setColorBuffer((float) LIGHT0_POS.x, (float) LIGHT0_POS.y, (float) LIGHT0_POS.z, 0.0f));

        float lightStrength = 0.3F * scale;
        GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(lightStrength, lightStrength, lightStrength, 1.0F));
        GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, RenderHelper.setColorBuffer((float) LIGHT1_POS.x, (float) LIGHT1_POS.y, (float) LIGHT1_POS.z, 0.0f));
        GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(lightStrength, lightStrength, lightStrength, 1.0F));
        GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));

        GlStateManager.shadeModel(GL11.GL_FLAT);

        float ambientLightStrength = 0.4F;
        GlStateManager.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, RenderHelper.setColorBuffer(ambientLightStrength, ambientLightStrength, ambientLightStrength, 1.0F));
    }
    */
}
