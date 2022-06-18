package fi.dy.masa.malilib.render;

import java.util.List;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import fi.dy.masa.malilib.gui.icon.PositionedIcon;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.game.wrap.EntityWrap;
import fi.dy.masa.malilib.util.game.wrap.GameUtils;
import fi.dy.masa.malilib.util.position.PositionUtils;
import fi.dy.masa.malilib.util.position.PositionUtils.HitPart;

public class RenderUtils
{
    public static final Identifier TEXTURE_MAP_BACKGROUND = new Identifier("textures/map/map_background.png");
    public static final Identifier TEXTURE_MAP_BACKGROUND_CHECKERBOARD = new Identifier("textures/map/map_background_checkerboard.png");

    private static final Random RAND = new LocalRandom(0);

    public static void setupBlend()
    {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA,
                                       GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
                                       GlStateManager.SrcFactor.ONE,
                                       GlStateManager.DstFactor.ZERO);
    }

    public static void setupBlendSimple()
    {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA,
                               GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    }

    public static void bindTexture(Identifier texture)
    {
        RenderSystem.setShaderTexture(0, texture);
    }

    public static void color(float r, float g, float b, float a)
    {
        RenderSystem.setShaderColor(r, g, b, a);
    }

    public static void disableItemLighting()
    {
        // TODO 1.13+ port
        //RenderHelper.disableStandardItemLighting();
    }

    public static void enableItemLighting()
    {
        // TODO 1.13+ port
        //RenderHelper.enableStandardItemLighting();
    }

    public static void enableGuiItemLighting()
    {
        // TODO 1.13+ port
        //RenderHelper.enableGUIStandardItemLighting();
    }

    /**
     * Gets the BufferBuilder from the vanilla Tessellator and initializes
     * it in the given mode/format.<br>
     * <b>Note:</b> This method also enables blending
     * @param useTexture determines if texture2D mode is enabled or disabled
     * @return the initialized BufferBuilder
     */
    public static BufferBuilder startBuffer(VertexFormat.DrawMode drawMode,
                                            VertexFormat format,
                                            boolean useTexture)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        if (useTexture)
        {
            RenderSystem.enableTexture();
        }
        else
        {
            RenderSystem.disableTexture();
        }

        RenderUtils.setupBlend();

        buffer.begin(drawMode, format);

        return buffer;
    }

    /**
     * Draws the buffer in the vanilla Tessellator, and then enables Texture2D mode
     */
    public static void drawBuffer()
    {
        Tessellator.getInstance().draw();
        RenderSystem.enableTexture();
    }

    public static void setupScaledScreenRendering(double scaleFactor)
    {
        double width = GuiUtils.getDisplayWidth() / scaleFactor;
        double height = GuiUtils.getDisplayHeight() / scaleFactor;

        setupScaledScreenRendering(width, height);
    }

    public static int getVanillaScreenScale()
    {
        MinecraftClient mc = GameUtils.getClient();
        int displayWidth = GuiUtils.getDisplayWidth();
        int displayHeight = GuiUtils.getDisplayHeight();
        int scale = Math.min(displayWidth / 320, displayHeight / 240);
        scale = Math.min(scale, mc.options.getGuiScale().getValue());
        scale = Math.max(scale, 1);

        if (mc.forcesUnicodeFont() && (scale & 0x1) != 0 && scale > 1)
        {
            --scale;
        }

        return scale;
    }

    public static void setupScaledScreenRendering(double width, double height)
    {
        Matrix4f matrix = Matrix4f.projectionMatrix(
                0.0F,
                (float) width,
                0.0F,
                (float) height,
                1000.0F,
                3000.0F
        );
        RenderSystem.setProjectionMatrix(matrix);
        /*
        GlStateManager.clear(256);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, width, height, 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        */
    }

    /* TODO 1.13+ port
    public static void renderAtlasSprite(float x, float y, float z, int width, int height, String texture)
    {
        if (texture != null)
        {
            TextureAtlasSprite sprite = GameUtils.getClient().getTextureMapBlocks().getAtlasSprite(texture);
            BufferBuilder buffer = startBuffer(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE, true);

            float u1 = sprite.getMinU();
            float u2 = sprite.getMaxU();
            float v1 = sprite.getMinV();
            float v2 = sprite.getMaxV();

            buffer.vertex(x        , y + height, z).texture(u1, v2).next();
            buffer.vertex(x + width, y + height, z).texture(u2, v2).next();
            buffer.vertex(x + width, y         , z).texture(u2, v1).next();
            buffer.vertex(x        , y         , z).texture(u1, v1).next();

            drawBuffer();
        }
    }
    */

    /**
     * Renders the given list of icons at their relative positions.
     * If the tintColor is not 0xFFFFFFFF, then the icons will be tinted/colored.
     */
    public static void renderPositionedIcons(int x, int y, float z,
                                             int tintColor, List<PositionedIcon> icons)
    {
        for (PositionedIcon posIcon : icons)
        {
            int posX = x + posIcon.pos.x;
            int posY = y + posIcon.pos.y;

            if (tintColor == 0xFFFFFFFF)
            {
                posIcon.icon.renderAt(posX, posY, z);
            }
            else
            {
                posIcon.icon.renderTintedAt(posX, posY, z, tintColor);
            }
        }
    }

    public static void renderNineSplicedTexture(int x, int y, float z,
                                                int u, int v,
                                                int width, int height,
                                                int texWidth, int texHeight, int edgeThickness)
    {
        BufferBuilder buffer = startBuffer(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE, true);

        int e = edgeThickness;
        
        ShapeRenderUtils.renderTexturedRectangle256(x, y             , z, u, v                , e, e, buffer); // top left
        ShapeRenderUtils.renderTexturedRectangle256(x, y + height - e, z, u, v + texHeight - e, e, e, buffer); // bottom left

        ShapeRenderUtils.renderTexturedRectangle256(x + width - e, y             , z, u + texWidth - e, v                , e, e, buffer); // top right
        ShapeRenderUtils.renderTexturedRectangle256(x + width - e, y + height - e, z, u + texWidth - e, v + texHeight - e, e, e, buffer); // bottom right

        // Texture is smaller than the requested width, repeat stuff horizontally
        if (texWidth < width)
        {
            final int repeatableWidth = texWidth - 2 * e;
            final int requiredWidth = width - 2 * e;

            for (int doneWidth = 0, tmpX = x + e, tmpW; doneWidth < requiredWidth; )
            {
                tmpW = Math.min(repeatableWidth, requiredWidth - doneWidth);

                ShapeRenderUtils.renderTexturedRectangle256(tmpX, y             , z, u + e, v                , tmpW, e, buffer); // top center
                ShapeRenderUtils.renderTexturedRectangle256(tmpX, y + height - e, z, u + e, v + texHeight - e, tmpW, e, buffer); // bottom center

                tmpX += tmpW;
                doneWidth += tmpW;
            }
        }
        // Texture is wide enough, no need to repeat horizontally
        else
        {
            ShapeRenderUtils.renderTexturedRectangle256(x + e, y             , z, u + e, v                , width - 2 * e, e, buffer); // top center
            ShapeRenderUtils.renderTexturedRectangle256(x + e, y + height - e, z, u + e, v + texHeight - e, width - 2 * e, e, buffer); // bottom center
        }

        // Texture is smaller than the requested height, repeat stuff vertically
        if (texHeight < height)
        {
            final int repeatableHeight = texHeight - 2 * e;
            final int requiredHeight = height - 2 * e;

            for (int doneHeight = 0, tmpY = y + e, tmpH; doneHeight < requiredHeight; )
            {
                tmpH = Math.min(repeatableHeight, requiredHeight - doneHeight);

                ShapeRenderUtils.renderTexturedRectangle256(x            , tmpY, z, u               , v + e, e, tmpH, buffer); // left center
                ShapeRenderUtils.renderTexturedRectangle256(x + width - e, tmpY, z, u + texWidth - e, v + e, e, tmpH, buffer); // right center

                tmpY += tmpH;
                doneHeight += tmpH;
            }
        }
        // Texture is tall enough, no need to repeat vertically
        else
        {
            ShapeRenderUtils.renderTexturedRectangle256(x            , y + e, z, u               , v + e, e, height - 2 * e, buffer); // left center
            ShapeRenderUtils.renderTexturedRectangle256(x + width - e, y + e, z, u + texWidth - e, v + e, e, height - 2 * e, buffer); // right center
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

                    ShapeRenderUtils.renderTexturedRectangle256(tmpX, tmpY, z, u + e, v + e, tmpW, tmpH, buffer); // center

                    tmpY += tmpH;
                    doneHeight += tmpH;
                }

                tmpX += tmpW;
                doneWidth += tmpW;
            }
        }
        else
        {
            ShapeRenderUtils.renderTexturedRectangle256(x + e, y + e, z, u + e, v + e, width - 2 * e, height - 2 * e, buffer); // center
        }

        drawBuffer();
    }

    public static void renderBlockTargetingOverlay(Entity entity,
                                                   BlockPos pos,
                                                   Direction side,
                                                   Vec3d hitVec,
                                                   Color4f color,
                                                   float partialTicks,
                                                   RenderContext ctx)
    {
        Direction playerFacing = entity.getHorizontalFacing();
        HitPart part = PositionUtils.getHitPart(side, playerFacing, pos, hitVec);

        double dx = EntityWrap.lerpX(entity, partialTicks);
        double dy = EntityWrap.lerpY(entity, partialTicks);
        double dz = EntityWrap.lerpZ(entity, partialTicks);

        double x = pos.getX() + 0.5d - dx;
        double y = pos.getY() + 0.5d - dy;
        double z = pos.getZ() + 0.5d - dz;

        MatrixStack matrixStack = ctx.matrixStack;
        matrixStack.push();

        blockTargetingOverlayTranslations(x, y, z, side, playerFacing, matrixStack);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float quadAlpha = 0.18f;
        float ha = color.a;
        float hr = color.r;
        float hg = color.g;
        float hb = color.b;

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        // White full block background
        buffer.vertex(x - 0.5, y - 0.5, z).color(1f, 1f, 1f, quadAlpha).next();
        buffer.vertex(x + 0.5, y - 0.5, z).color(1f, 1f, 1f, quadAlpha).next();
        buffer.vertex(x + 0.5, y + 0.5, z).color(1f, 1f, 1f, quadAlpha).next();
        buffer.vertex(x - 0.5, y + 0.5, z).color(1f, 1f, 1f, quadAlpha).next();

        switch (part)
        {
            case CENTER:
                buffer.vertex(x - 0.25, y - 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x + 0.25, y - 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x + 0.25, y + 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x - 0.25, y + 0.25, z).color(hr, hg, hb, ha).next();
                break;
            case LEFT:
                buffer.vertex(x - 0.50, y - 0.50, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x - 0.25, y - 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x - 0.25, y + 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x - 0.50, y + 0.50, z).color(hr, hg, hb, ha).next();
                break;
            case RIGHT:
                buffer.vertex(x + 0.50, y - 0.50, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x + 0.25, y - 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x + 0.25, y + 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x + 0.50, y + 0.50, z).color(hr, hg, hb, ha).next();
                break;
            case TOP:
                buffer.vertex(x - 0.50, y + 0.50, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x - 0.25, y + 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x + 0.25, y + 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x + 0.50, y + 0.50, z).color(hr, hg, hb, ha).next();
                break;
            case BOTTOM:
                buffer.vertex(x - 0.50, y - 0.50, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x - 0.25, y - 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x + 0.25, y - 0.25, z).color(hr, hg, hb, ha).next();
                buffer.vertex(x + 0.50, y - 0.50, z).color(hr, hg, hb, ha).next();
                break;
            default:
        }

        tessellator.draw();

        RenderSystem.lineWidth(1.6f);

        buffer.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

        // Middle small rectangle
        buffer.vertex(x - 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).next();
        buffer.vertex(x + 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).next();

        //buffer.vertex(x + 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).next();
        buffer.vertex(x + 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).next();

        //buffer.vertex(x + 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).next();
        buffer.vertex(x - 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).next();

        //buffer.vertex(x - 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).next();
        buffer.vertex(x - 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).next();
        tessellator.draw();

        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        // Bottom left
        buffer.vertex(x - 0.50, y - 0.50, z).color(1f, 1f, 1f, 1f).next();
        buffer.vertex(x - 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).next();

        // Top left
        buffer.vertex(x - 0.50, y + 0.50, z).color(1f, 1f, 1f, 1f).next();
        buffer.vertex(x - 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).next();

        // Bottom right
        buffer.vertex(x + 0.50, y - 0.50, z).color(1f, 1f, 1f, 1f).next();
        buffer.vertex(x + 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).next();

        // Top right
        buffer.vertex(x + 0.50, y + 0.50, z).color(1f, 1f, 1f, 1f).next();
        buffer.vertex(x + 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).next();
        tessellator.draw();

        matrixStack.pop();
    }

    public static void renderBlockTargetingOverlaySimple(Entity entity,
                                                         BlockPos pos,
                                                         Direction side,
                                                         Color4f color,
                                                         float partialTicks,
                                                         RenderContext ctx)
    {
        Direction playerFacing = entity.getHorizontalFacing();

        double dx = EntityWrap.lerpX(entity, partialTicks);
        double dy = EntityWrap.lerpY(entity, partialTicks);
        double dz = EntityWrap.lerpZ(entity, partialTicks);

        double x = pos.getX() + 0.5d - dx;
        double y = pos.getY() + 0.5d - dy;
        double z = pos.getZ() + 0.5d - dz;

        MatrixStack matrixStack = ctx.matrixStack;
        matrixStack.push();

        blockTargetingOverlayTranslations(x, y, z, side, playerFacing, matrixStack);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        float a = color.a;
        float r = color.r;
        float g = color.g;
        float b = color.b;

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        // Simple colored quad
        buffer.vertex(x - 0.5, y - 0.5, z).color(r, g, b, a).next();
        buffer.vertex(x + 0.5, y - 0.5, z).color(r, g, b, a).next();
        buffer.vertex(x + 0.5, y + 0.5, z).color(r, g, b, a).next();
        buffer.vertex(x - 0.5, y + 0.5, z).color(r, g, b, a).next();

        tessellator.draw();

        RenderSystem.lineWidth(1.6f);

        buffer.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

        // Middle rectangle
        buffer.vertex(x - 0.375, y - 0.375, z).color(1f, 1f, 1f, 1f).next();
        buffer.vertex(x + 0.375, y - 0.375, z).color(1f, 1f, 1f, 1f).next();
        buffer.vertex(x + 0.375, y + 0.375, z).color(1f, 1f, 1f, 1f).next();
        buffer.vertex(x - 0.375, y + 0.375, z).color(1f, 1f, 1f, 1f).next();
        buffer.vertex(x - 0.375, y - 0.375, z).color(1f, 1f, 1f, 1f).next();

        tessellator.draw();

        matrixStack.pop();
    }

    private static void blockTargetingOverlayTranslations(double x, double y, double z,
                                                          Direction side,
                                                          Direction playerFacing,
                                                          MatrixStack matrixStack)
    {
        matrixStack.translate(x, y, z);

        switch (side)
        {
            case DOWN:
                matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180f - playerFacing.asRotation()));
                matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90f));
                break;
            case UP:
                matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180f - playerFacing.asRotation()));
                matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90f));
                break;
            case NORTH:
                matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180f));
                break;
            case SOUTH:
                break;
            case WEST:
                matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90f));
                break;
            case EAST:
                matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90f));
                break;
        }

        matrixStack.translate(-x, -y, -z + 0.510);
    }

    public static void renderMapPreview(ItemStack stack,
                                        int x, int y, float z,
                                        int dimensions,
                                        RenderContext ctx)
    {
        if (stack.getItem() instanceof FilledMapItem)
        {
            MatrixStack matrixStack = ctx.matrixStack;
            matrixStack.push();
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

            Integer mapId = FilledMapItem.getMapId(stack);
            MapState mapState = FilledMapItem.getMapState(mapId, GameUtils.getClientWorld());

            Identifier bgTexture = mapState == null ? TEXTURE_MAP_BACKGROUND : TEXTURE_MAP_BACKGROUND_CHECKERBOARD;
            bindTexture(bgTexture);
            setupBlend();

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            //RenderSystem.applyModelViewMatrix();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            buffer.vertex(x1, y2, z).texture(0.0f, 1.0f).next();
            buffer.vertex(x2, y2, z).texture(1.0f, 1.0f).next();
            buffer.vertex(x2, y1, z).texture(1.0f, 0.0f).next();
            buffer.vertex(x1, y1, z).texture(0.0f, 0.0f).next();

            tessellator.draw();
            RenderSystem.disableBlend();

            if (mapState != null)
            {
                x1 += 8;
                y1 += 8;
                double scale = (double) (dimensions - 16) / 128.0;
                VertexConsumerProvider.Immediate consumer = VertexConsumerProvider.immediate(buffer);
                MatrixStack emptyStack = new MatrixStack();
                emptyStack.translate(x1, y1, z);
                emptyStack.scale((float) scale, (float) scale, 1);
                GameUtils.getClient().gameRenderer.getMapRenderer()
                        .draw(emptyStack, consumer, mapId, mapState, false, 0xF000F0);
                consumer.draw();
            }

            color(1f, 1f, 1f, 1f);
            matrixStack.pop();
        }
    }

    public static void renderModelInGui(int x, int y, float zLevel,
                                        BakedModel model, BlockState state,
                                        RenderContext ctx)
    {
        if (state.getBlock() == Blocks.AIR)
        {
            return;
        }

        MatrixStack matrixStack = ctx.matrixStack;
        matrixStack.push();
        bindTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        GameUtils.getClient().getTextureManager().getTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).setFilter(false, false);

        RenderSystem.enableBlend();
        setupBlendSimple();
        color(1f, 1f, 1f, 1f);

        matrixStack.translate(x + 8.0, y + 8.0, zLevel + 100.0);
        matrixStack.scale(16, -16, 16);

        //model.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GUI);
        matrixStack.multiply(new Quaternion(Vec3f.POSITIVE_X, 30, true));
        matrixStack.multiply(new Quaternion(Vec3f.POSITIVE_Y, 225, true));
        matrixStack.scale(0.625f, 0.625f, 0.625f);

        renderModel(model, state, zLevel, matrixStack);

        matrixStack.pop();
    }

    private static void renderModel(BakedModel model, BlockState state,
                                    float zLevel, MatrixStack matrixStack)
    {
        matrixStack.push();
        matrixStack.translate(-0.5, -0.5, zLevel);
        int color = 0xFFFFFFFF;

        if (model.isBuiltin() == false)
        {
            RenderSystem.setShader(GameRenderer::getRenderTypeSolidShader);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);

            for (Direction face : PositionUtils.ALL_DIRECTIONS)
            {
                RAND.setSeed(0);
                renderQuads(bufferbuilder, model.getQuads(state, face, RAND), state, color);
            }

            RAND.setSeed(0);
            renderQuads(bufferbuilder, model.getQuads(state, null, RAND), state, color);
            tessellator.draw();
        }

        matrixStack.pop();
    }

    public static void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, BlockState state, int color)
    {
        for (BakedQuad quad : quads)
        {
            renderQuad(renderer, quad, state, color);
        }
    }

    public static void renderQuad(BufferBuilder buffer, BakedQuad quad, BlockState state, int color)
    {
        /* TODO 1.13+ port
        buffer.addVertexData(quad.getVertexData());
        buffer.putColor4(color);

        if (quad.hasTintIndex())
        {
            BlockColors blockColors = GameUtils.getClient().getBlockColors();
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
        */
    }

    /*
    public static void putQuadNormal(BufferBuilder buffer, BakedQuad quad)
    {
        Vec3i direction = quad.getFace().getDirectionVec();
        buffer.putNormal(direction.getX(), direction.getY(), direction.getZ());
    }
    */

    /*
     * Renders the given model to the given vertex consumer.
     * Needs a vertex consumer initialized with mode GL11.GL_QUADS and DefaultVertexFormats.ITEM
     */
    /*
    public static void renderModelBrightnessColor(BakedModel model, Vec3d pos, BufferBuilder buffer)
    {
        renderModelBrightnessColor(model, pos, null, 1f, 1f, 1f, 1f, buffer);
    }
    */

    /*
     * Renders the given model to the given vertex consumer.
     * Needs a vertex consumer initialized with mode GL11.GL_QUADS and DefaultVertexFormats.ITEM
     */
    /*
    public static void renderModelBrightnessColor(BakedModel model, Vec3d pos, @Nullable BlockState state,
                                                  float brightness, float r, float g, float b, BufferBuilder buffer)
    {
        for (Direction side : PositionUtils.ALL_DIRECTIONS)
        {
            renderQuads(model.getQuads(state, side, 0L), pos, brightness, r, g, b, buffer);
        }

        renderQuads(model.getQuads(state, null, 0L), pos, brightness, r, g, b, buffer);
    }
    */

    /*
     * Renders the given quads to the given vertex consumer.
     * Needs a vertex consumer initialized with mode GL11.GL_QUADS and DefaultVertexFormats.ITEM
     */
    /*
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
    */
}
