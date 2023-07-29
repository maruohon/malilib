package malilib.render;

import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.storage.MapData;

import malilib.gui.icon.Icon;
import malilib.gui.icon.PositionedIcon;
import malilib.gui.util.GuiUtils;
import malilib.render.buffer.VanillaWrappingVertexBuilder;
import malilib.render.buffer.VertexBuilder;
import malilib.util.data.Color4f;
import malilib.util.game.wrap.EntityWrap;
import malilib.util.game.wrap.GameUtils;
import malilib.util.position.PositionUtils;
import malilib.util.position.PositionUtils.HitPart;
import malilib.util.position.Vec2i;

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
        GameUtils.getClient().getTextureManager().bindTexture(texture);
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

    public static void setupScaledScreenRendering(double scaleFactor)
    {
        double width = GuiUtils.getDisplayWidth() / scaleFactor;
        double height = GuiUtils.getDisplayHeight() / scaleFactor;

        setupScaledScreenRendering(width, height);
    }

    public static void setupScaledScreenRendering(double width, double height)
    {
        GlStateManager.clear(256);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, width, height, 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
    }

    public static void renderAtlasSprite(float x, float y, float z, int width, int height, String texture)
    {
        if (texture != null)
        {
            TextureAtlasSprite sprite = GameUtils.getClient().getTextureMapBlocks().getAtlasSprite(texture);
            VertexBuilder builder = VanillaWrappingVertexBuilder.texturedQuad();

            float u1 = sprite.getMinU();
            float u2 = sprite.getMaxU();
            float v1 = sprite.getMinV();
            float v2 = sprite.getMaxV();

            builder.posUv(x        , y + height, z, u1, v2);
            builder.posUv(x + width, y + height, z, u2, v2);
            builder.posUv(x + width, y         , z, u2, v1);
            builder.posUv(x        , y         , z, u1, v1);

            builder.draw();
        }
    }

    /**
     * Renders the given list of icons at their relative positions.
     * If the tintColor is not 0xFFFFFFFF, then the icons will be tinted/colored.
     */
    public static void renderPositionedIcons(int x, int y, float z, int tintColor, List<PositionedIcon> icons)
    {
        for (PositionedIcon posIcon : icons)
        {
            Vec2i position = posIcon.pos;
            Icon icon = posIcon.icon;
            int posX = x + position.x;
            int posY = y + position.y;

            if (tintColor == 0xFFFFFFFF)
            {
                icon.renderAt(posX, posY, z);
            }
            else
            {
                icon.renderTintedAt(posX, posY, z, tintColor);
            }
        }
    }

    public static void renderNineSplicedTexture(int x, int y, float z, int u, int v, int width, int height,
                                                int texWidth, int texHeight, int edgeThickness)
    {
        VertexBuilder builder = VanillaWrappingVertexBuilder.texturedQuad();

        int e = edgeThickness;
        
        ShapeRenderUtils.renderTexturedRectangle256(x, y             , z, u, v                , e, e, builder); // top left
        ShapeRenderUtils.renderTexturedRectangle256(x, y + height - e, z, u, v + texHeight - e, e, e, builder); // bottom left

        ShapeRenderUtils.renderTexturedRectangle256(x + width - e, y             , z, u + texWidth - e, v                , e, e, builder); // top right
        ShapeRenderUtils.renderTexturedRectangle256(x + width - e, y + height - e, z, u + texWidth - e, v + texHeight - e, e, e, builder); // bottom right

        // Texture is smaller than the requested width, repeat stuff horizontally
        if (texWidth < width)
        {
            final int repeatableWidth = texWidth - 2 * e;
            final int requiredWidth = width - 2 * e;

            for (int doneWidth = 0, tmpX = x + e, tmpW; doneWidth < requiredWidth; )
            {
                tmpW = Math.min(repeatableWidth, requiredWidth - doneWidth);

                ShapeRenderUtils.renderTexturedRectangle256(tmpX, y             , z, u + e, v                , tmpW, e, builder); // top center
                ShapeRenderUtils.renderTexturedRectangle256(tmpX, y + height - e, z, u + e, v + texHeight - e, tmpW, e, builder); // bottom center

                tmpX += tmpW;
                doneWidth += tmpW;
            }
        }
        // Texture is wide enough, no need to repeat horizontally
        else
        {
            ShapeRenderUtils.renderTexturedRectangle256(x + e, y             , z, u + e, v                , width - 2 * e, e, builder); // top center
            ShapeRenderUtils.renderTexturedRectangle256(x + e, y + height - e, z, u + e, v + texHeight - e, width - 2 * e, e, builder); // bottom center
        }

        // Texture is smaller than the requested height, repeat stuff vertically
        if (texHeight < height)
        {
            final int repeatableHeight = texHeight - 2 * e;
            final int requiredHeight = height - 2 * e;

            for (int doneHeight = 0, tmpY = y + e, tmpH; doneHeight < requiredHeight; )
            {
                tmpH = Math.min(repeatableHeight, requiredHeight - doneHeight);

                ShapeRenderUtils.renderTexturedRectangle256(x            , tmpY, z, u               , v + e, e, tmpH, builder); // left center
                ShapeRenderUtils.renderTexturedRectangle256(x + width - e, tmpY, z, u + texWidth - e, v + e, e, tmpH, builder); // right center

                tmpY += tmpH;
                doneHeight += tmpH;
            }
        }
        // Texture is tall enough, no need to repeat vertically
        else
        {
            ShapeRenderUtils.renderTexturedRectangle256(x            , y + e, z, u               , v + e, e, height - 2 * e, builder); // left center
            ShapeRenderUtils.renderTexturedRectangle256(x + width - e, y + e, z, u + texWidth - e, v + e, e, height - 2 * e, builder); // right center
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

                    ShapeRenderUtils.renderTexturedRectangle256(tmpX, tmpY, z, u + e, v + e, tmpW, tmpH, builder); // center

                    tmpY += tmpH;
                    doneHeight += tmpH;
                }

                tmpX += tmpW;
                doneWidth += tmpW;
            }
        }
        else
        {
            ShapeRenderUtils.renderTexturedRectangle256(x + e, y + e, z, u + e, v + e, width - 2 * e, height - 2 * e, builder); // center
        }

        builder.draw();
    }

    public static void renderBlockTargetingOverlay(Entity entity, BlockPos pos, EnumFacing side, Vec3d hitVec,
                                                   Color4f color, float partialTicks)
    {
        EnumFacing playerFacing = entity.getHorizontalFacing();
        HitPart part = PositionUtils.getHitPart(side, playerFacing, pos, hitVec);

        double dx = EntityWrap.lerpX(entity, partialTicks);
        double dy = EntityWrap.lerpY(entity, partialTicks);
        double dz = EntityWrap.lerpZ(entity, partialTicks);

        double x = pos.getX() + 0.5d - dx;
        double y = pos.getY() + 0.5d - dy;
        double z = pos.getZ() + 0.5d - dz;

        GlStateManager.pushMatrix();

        blockTargetingOverlayTranslations(x, y, z, side, playerFacing);

        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();

        // White full block background
        int quadAlpha = 46;
        builder.posColor(x - 0.5, y - 0.5, z, 255, 255, 255, quadAlpha);
        builder.posColor(x + 0.5, y - 0.5, z, 255, 255, 255, quadAlpha);
        builder.posColor(x + 0.5, y + 0.5, z, 255, 255, 255, quadAlpha);
        builder.posColor(x - 0.5, y + 0.5, z, 255, 255, 255, quadAlpha);

        switch (part)
        {
            case CENTER:
                builder.posColor(x - 0.25, y - 0.25, z, color);
                builder.posColor(x + 0.25, y - 0.25, z, color);
                builder.posColor(x + 0.25, y + 0.25, z, color);
                builder.posColor(x - 0.25, y + 0.25, z, color);
                break;
            case LEFT:
                builder.posColor(x - 0.50, y - 0.50, z, color);
                builder.posColor(x - 0.25, y - 0.25, z, color);
                builder.posColor(x - 0.25, y + 0.25, z, color);
                builder.posColor(x - 0.50, y + 0.50, z, color);
                break;
            case RIGHT:
                builder.posColor(x + 0.50, y - 0.50, z, color);
                builder.posColor(x + 0.25, y - 0.25, z, color);
                builder.posColor(x + 0.25, y + 0.25, z, color);
                builder.posColor(x + 0.50, y + 0.50, z, color);
                break;
            case TOP:
                builder.posColor(x - 0.50, y + 0.50, z, color);
                builder.posColor(x - 0.25, y + 0.25, z, color);
                builder.posColor(x + 0.25, y + 0.25, z, color);
                builder.posColor(x + 0.50, y + 0.50, z, color);
                break;
            case BOTTOM:
                builder.posColor(x - 0.50, y - 0.50, z, color);
                builder.posColor(x - 0.25, y - 0.25, z, color);
                builder.posColor(x + 0.25, y - 0.25, z, color);
                builder.posColor(x + 0.50, y - 0.50, z, color);
                break;
            default:
        }

        builder.draw();

        GlStateManager.glLineWidth(1.6f);

        builder = VanillaWrappingVertexBuilder.coloredLines();

        // Middle small rectangle
        builder.posColor(x - 0.25, y - 0.25, z, Color4f.WHITE);
        builder.posColor(x + 0.25, y - 0.25, z, Color4f.WHITE);

        builder.posColor(x + 0.25, y - 0.25, z, Color4f.WHITE);
        builder.posColor(x + 0.25, y + 0.25, z, Color4f.WHITE);

        builder.posColor(x + 0.25, y + 0.25, z, Color4f.WHITE);
        builder.posColor(x - 0.25, y + 0.25, z, Color4f.WHITE);

        builder.posColor(x - 0.25, y + 0.25, z, Color4f.WHITE);
        builder.posColor(x - 0.25, y - 0.25, z, Color4f.WHITE);

        // Bottom left
        builder.posColor(x - 0.50, y - 0.50, z, Color4f.WHITE);
        builder.posColor(x - 0.25, y - 0.25, z, Color4f.WHITE);

        // Top left
        builder.posColor(x - 0.50, y + 0.50, z, Color4f.WHITE);
        builder.posColor(x - 0.25, y + 0.25, z, Color4f.WHITE);

        // Bottom right
        builder.posColor(x + 0.50, y - 0.50, z, Color4f.WHITE);
        builder.posColor(x + 0.25, y - 0.25, z, Color4f.WHITE);

        // Top right
        builder.posColor(x + 0.50, y + 0.50, z, Color4f.WHITE);
        builder.posColor(x + 0.25, y + 0.25, z, Color4f.WHITE);

        builder.draw();

        GlStateManager.popMatrix();
    }

    public static void renderBlockTargetingOverlaySimple(Entity entity, BlockPos pos, EnumFacing side,
                                                         Color4f color, float partialTicks)
    {
        EnumFacing playerFacing = entity.getHorizontalFacing();

        double dx = EntityWrap.lerpX(entity, partialTicks);
        double dy = EntityWrap.lerpY(entity, partialTicks);
        double dz = EntityWrap.lerpZ(entity, partialTicks);

        double x = pos.getX() + 0.5d - dx;
        double y = pos.getY() + 0.5d - dy;
        double z = pos.getZ() + 0.5d - dz;

        GlStateManager.pushMatrix();

        blockTargetingOverlayTranslations(x, y, z, side, playerFacing);

        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();

        // Simple colored quad
        builder.posColor(x - 0.5, y - 0.5, z, color);
        builder.posColor(x + 0.5, y - 0.5, z, color);
        builder.posColor(x + 0.5, y + 0.5, z, color);
        builder.posColor(x - 0.5, y + 0.5, z, color);

        builder.draw();

        GlStateManager.glLineWidth(1.6f);

        builder = VanillaWrappingVertexBuilder.coloredLineLoop();

        // Middle rectangle
        builder.posColor(x - 0.375, y - 0.375, z, Color4f.WHITE);
        builder.posColor(x + 0.375, y - 0.375, z, Color4f.WHITE);
        builder.posColor(x + 0.375, y + 0.375, z, Color4f.WHITE);
        builder.posColor(x - 0.375, y + 0.375, z, Color4f.WHITE);

        builder.draw();

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

    public static void renderMapPreview(ItemStack stack, int x, int y, float z, int dimensions)
    {
        if (stack.getItem() instanceof ItemMap)
        {
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            color(1f, 1f, 1f, 1f);

            int screenWidth = GuiUtils.getScaledWindowWidth();
            int screenHeight = GuiUtils.getScaledWindowHeight();
            int y1 = Math.max(y - dimensions - 20, 2);

            if (x + dimensions + 10 > screenWidth)
            {
                x = Math.max(x - dimensions - 10, 2);
            }

            if (y1 + dimensions + 2 > screenHeight)
            {
                y1 = screenHeight - dimensions - 2;
            }

            int x1 = x + 8;
            int x2 = x1 + dimensions;
            int y2 = y1 + dimensions;

            bindTexture(RenderUtils.TEXTURE_MAP_BACKGROUND);

            VertexBuilder builder = VanillaWrappingVertexBuilder.texturedQuad();

            builder.posUv(x1, y2, z, 0.0f, 1.0f);
            builder.posUv(x2, y2, z, 1.0f, 1.0f);
            builder.posUv(x2, y1, z, 1.0f, 0.0f);
            builder.posUv(x1, y1, z, 0.0f, 0.0f);

            builder.draw();

            MapData mapdata = Items.FILLED_MAP.getMapData(stack, GameUtils.getClientWorld());

            if (mapdata != null)
            {
                x1 += 8;
                y1 += 8;
                double scale = (double) (dimensions - 16) / 128.0;
                GlStateManager.translate(x1, y1, z + 1f);
                GlStateManager.scale(scale, scale, 0);
                GameUtils.getClient().entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
            }

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();

            color(1f, 1f, 1f, 1f);
        }
    }

    public static void renderModelInGui(int x, int y, float zLevel,
                                        IBakedModel model, IBlockState state, RenderContext ctx)
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
        GlStateManager.translate(-0.5F, -0.5F, zLevel);
        int color = 0xFFFFFFFF;

        if (model.isBuiltInRenderer() == false)
        {
            VertexBuilder builder = VanillaWrappingVertexBuilder.create(GL11.GL_QUADS, DefaultVertexFormats.ITEM);

            for (EnumFacing enumfacing : PositionUtils.ALL_DIRECTIONS)
            {
                renderQuads(model.getQuads(state, enumfacing, 0L), state, color, builder);
            }

            renderQuads(model.getQuads(state, null, 0L), state, color, builder);

            builder.draw();
        }

        GlStateManager.popMatrix();
    }

    public static void renderQuads(List<BakedQuad> quads, IBlockState state, int color, VertexBuilder builder)
    {
        for (BakedQuad quad : quads)
        {
            renderQuad(quad, state, color, builder);
        }
    }

    public static void renderQuad(BakedQuad quad, IBlockState state, int color, VertexBuilder builder)
    {
        builder.addVertexData(quad.getVertexData());
        builder.putQuadColor(color);

        if (quad.hasTintIndex())
        {
            BlockColors blockColors = GameUtils.getClient().getBlockColors();
            int m = blockColors.colorMultiplier(state, null, null, quad.getTintIndex());

            float r = (float) (m >>> 16 & 0xFF) / 255F;
            float g = (float) (m >>>  8 & 0xFF) / 255F;
            float b = (float) (m        & 0xFF) / 255F;
            builder.putColorMultiplier(r, g, b, 4);
            builder.putColorMultiplier(r, g, b, 3);
            builder.putColorMultiplier(r, g, b, 2);
            builder.putColorMultiplier(r, g, b, 1);
        }

        putQuadNormal(quad, builder);
    }

    public static void putQuadNormal(BakedQuad quad, VertexBuilder builder)
    {
        Vec3i direction = quad.getFace().getDirectionVec();
        builder.putNormal(direction.getX(), direction.getY(), direction.getZ());
    }

    /**
     * Renders the given model to the given vertex consumer.
     * Needs a vertex consumer initialized with mode GL11.GL_QUADS and DefaultVertexFormats.ITEM
     */
    public static void renderModelBrightnessColor(IBakedModel model, Vec3d pos, VertexBuilder builder)
    {
        renderModelBrightnessColor(model, pos, null, 1f, 1f, 1f, 1f, builder);
    }

    /**
     * Renders the given model to the given vertex consumer.
     * Needs a vertex consumer initialized with mode GL11.GL_QUADS and DefaultVertexFormats.ITEM
     */
    public static void renderModelBrightnessColor(IBakedModel model, Vec3d pos, @Nullable IBlockState state,
                                                  float brightness, float r, float g, float b, VertexBuilder builder)
    {
        for (EnumFacing side : PositionUtils.ALL_DIRECTIONS)
        {
            renderQuads(model.getQuads(state, side, 0L), pos, brightness, r, g, b, builder);
        }

        renderQuads(model.getQuads(state, null, 0L), pos, brightness, r, g, b, builder);
    }

    /**
     * Renders the given quads to the given vertex consumer.
     * Needs a vertex consumer initialized with mode GL11.GL_QUADS and DefaultVertexFormats.ITEM
     */
    public static void renderQuads(List<BakedQuad> quads, Vec3d pos, float brightness,
                                   float red, float green, float blue, VertexBuilder builder)
    {
        for (BakedQuad quad : quads)
        {
            builder.addVertexData(quad.getVertexData());

            if (quad.hasTintIndex())
            {
                builder.putQuadColor(red * brightness, green * brightness, blue * brightness);
            }
            else
            {
                builder.putQuadColor(brightness, brightness, brightness);
            }

            builder.putPosition(pos.x, pos.y, pos.z);
            putQuadNormal(quad, builder);
        }
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
