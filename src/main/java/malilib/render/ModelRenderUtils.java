package malilib.render;

import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;

import malilib.render.buffer.VanillaWrappingVertexBuilder;
import malilib.render.buffer.VertexBuilder;
import malilib.render.buffer.VertexFormats;
import malilib.util.data.Identifier;
import malilib.util.game.wrap.GameWrap;
import malilib.util.game.wrap.RenderWrap;
import malilib.util.position.Direction;
import malilib.util.position.PositionUtils;
import malilib.util.position.Vec3d;

public class ModelRenderUtils
{
    private static final Identifier BLOCK_TEXTURE = new Identifier(TextureMap.LOCATION_BLOCKS_TEXTURE);

    public static void renderModelInGui(int x, int y, float zLevel,
                                        IBakedModel model, IBlockState state, RenderContext ctx)
    {
        if (state.getBlock() == Blocks.AIR)
        {
            return;
        }

        RenderWrap.pushMatrix(ctx);

        RenderWrap.bindTexture(BLOCK_TEXTURE);
        RenderWrap.enableRescaleNormal();
        RenderWrap.enableAlpha();
        RenderWrap.alphaFunc(GL11.GL_GREATER, 0.01F);
        RenderWrap.setupBlendSimple();
        RenderWrap.color(1f, 1f, 1f, 1f);

        setupGuiTransform(x, y, model.isGui3d(), zLevel, ctx);
        //model.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GUI);
        RenderWrap.rotate( 30, 1, 0, 0, ctx);
        RenderWrap.rotate(225, 0, 1, 0, ctx);
        RenderWrap.scale(0.625, 0.625, 0.625, ctx);

        renderModel(model, state, zLevel, ctx);

        RenderWrap.disableAlpha();
        RenderWrap.disableRescaleNormal();
        RenderWrap.disableLighting();

        RenderWrap.popMatrix(ctx);
    }

    public static void setupGuiTransform(int x, int y, boolean isGui3d, float zLevel, RenderContext ctx)
    {
        RenderWrap.translate(x + 8.0F, y + 8.0F, 100.0F + zLevel, ctx);
        //RenderWrap.translate(8.0F, 8.0F, 0.0F, ctx);
        RenderWrap.scale(1.0F, -1.0F, 1.0F, ctx);
        RenderWrap.scale(16.0F, 16.0F, 16.0F, ctx);

        if (isGui3d)
        {
            RenderWrap.enableLighting();
        }
        else
        {
            RenderWrap.disableLighting();
        }
    }

    public static void renderModel(IBakedModel model, IBlockState state, float zLevel, RenderContext ctx)
    {
        RenderWrap.pushMatrix(ctx);
        RenderWrap.translate(-0.5F, -0.5F, zLevel, ctx);
        int colorARGB = 0xFFFFFFFF;

        if (model.isBuiltInRenderer() == false)
        {
            VertexBuilder builder = VanillaWrappingVertexBuilder.create(GL11.GL_QUADS, VertexFormats.ITEM);

            for (Direction side : PositionUtils.ALL_DIRECTIONS)
            {
                renderQuads(model.getQuads(state, side.getVanillaDirection(), 0L), state, colorARGB, builder);
            }

            renderQuads(model.getQuads(state, null, 0L), state, colorARGB, builder);

            builder.draw();
        }

        RenderWrap.popMatrix(ctx);
    }

    public static void renderQuads(List<BakedQuad> quads, IBlockState state, int colorARGB, VertexBuilder builder)
    {
        BlockColors blockColors = GameWrap.getClient().getBlockColors();

        for (BakedQuad quad : quads)
        {
            renderQuad(quad, state, colorARGB, blockColors, builder);
        }
    }

    public static void renderQuad(BakedQuad quad, IBlockState state, int colorARGB,
                                  BlockColors blockColors, VertexBuilder builder)
    {
        if (quad.hasTintIndex())
        {
            int colorMultiplier = blockColors.colorMultiplier(state, null, null, quad.getTintIndex());
            builder.putBakedQuad(quad, colorARGB, colorMultiplier);
        }
        else
        {
            builder.putBakedQuad(quad, colorARGB);
        }
    }

    /**
     * Renders the given model to the given vertex consumer.
     * Needs a vertex consumer initialized with mode GL11.GL_QUADS and VertexFormats.ITEM
     */
    public static void renderModelBrightnessColor(IBakedModel model, Vec3d pos, VertexBuilder builder)
    {
        renderModelBrightnessColor(model, pos, null, 0xFFFFFFFF, builder);
    }

    /**
     * Renders the given model to the given vertex consumer.
     * Needs a vertex consumer initialized with mode GL11.GL_QUADS and VertexFormats.ITEM
     */
    public static void renderModelBrightnessColor(IBakedModel model, Vec3d pos, @Nullable IBlockState state,
                                                  int colorARGB, VertexBuilder builder)
    {
        for (Direction side : PositionUtils.ALL_DIRECTIONS)
        {
            renderQuads(model.getQuads(state, side.getVanillaDirection(), 0L), pos, colorARGB, builder);
        }

        renderQuads(model.getQuads(state, null, 0L), pos, colorARGB, builder);
    }

    /**
     * Renders the given quads to the given vertex consumer.
     * Needs a vertex consumer initialized with mode GL11.GL_QUADS and VertexFormats.ITEM
     */
    public static void renderQuads(List<BakedQuad> quads, Vec3d pos, int colorARGB, VertexBuilder builder)
    {
        for (BakedQuad quad : quads)
        {
            builder.putBakedQuad(pos.x, pos.y, pos.z, quad, colorARGB);
        }
    }
}
