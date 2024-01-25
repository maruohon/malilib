package malilib.render;

import net.minecraft.entity.Entity;

import malilib.render.buffer.VanillaWrappingVertexBuilder;
import malilib.render.buffer.VertexBuilder;
import malilib.util.data.Color4f;
import malilib.util.game.wrap.EntityWrap;
import malilib.util.game.wrap.RenderWrap;
import malilib.util.position.BlockPos;
import malilib.util.position.Direction;
import malilib.util.position.PositionUtils;
import malilib.util.position.PositionUtils.HitPart;
import malilib.util.position.Vec3d;

public class BlockTargetingRenderUtils
{
    public static final Color4f TRANSLUCENT_WHITE = Color4f.fromColor(0xFFFFFF, 0.18f);

    public static void render5WayBlockTargetingOverlay(Entity entity, BlockPos pos, Direction side, Vec3d hitVec,
                                                       Color4f color, float partialTicks, RenderContext ctx)
    {
        Direction playerFacing = EntityWrap.getClosestHorizontalLookingDirection(entity);
        HitPart part = PositionUtils.getHitPart(side, playerFacing, pos, hitVec);

        double x = pos.getX() + 0.5d - EntityWrap.lerpX(entity, partialTicks);
        double y = pos.getY() + 0.5d - EntityWrap.lerpY(entity, partialTicks);
        double z = pos.getZ() + 0.5d - EntityWrap.lerpZ(entity, partialTicks);

        RenderWrap.pushMatrix();

        blockTargetingOverlayTranslations(x, y, z, side, playerFacing);

        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();

        // White full block background
        ShapeRenderUtils.renderRectangle(x - 0.5, y - 0.5, z, 1.0, 1.0, TRANSLUCENT_WHITE, builder);

        switch (part)
        {
            case CENTER:
                ShapeRenderUtils.renderRectangle(x - 0.25, y - 0.25, z, 0.5, 0.5, color, builder);
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

        RenderWrap.lineWidth(1.6f);

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

        RenderWrap.popMatrix();
    }

    public static void renderSimpleSquareBlockTargetingOverlay(Entity entity, BlockPos pos, Direction side,
                                                               Color4f color, float partialTicks, RenderContext ctx)
    {
        Direction playerFacing = EntityWrap.getClosestHorizontalLookingDirection(entity);

        double x = pos.getX() + 0.5d - EntityWrap.lerpX(entity, partialTicks);
        double y = pos.getY() + 0.5d - EntityWrap.lerpY(entity, partialTicks);
        double z = pos.getZ() + 0.5d - EntityWrap.lerpZ(entity, partialTicks);

        RenderWrap.pushMatrix();

        blockTargetingOverlayTranslations(x, y, z, side, playerFacing);

        VertexBuilder builder = VanillaWrappingVertexBuilder.coloredQuads();

        // Simple colored quad
        builder.posColor(x - 0.5, y - 0.5, z, color);
        builder.posColor(x + 0.5, y - 0.5, z, color);
        builder.posColor(x + 0.5, y + 0.5, z, color);
        builder.posColor(x - 0.5, y + 0.5, z, color);

        builder.draw();

        RenderWrap.lineWidth(1.6f);

        builder = VanillaWrappingVertexBuilder.coloredLineLoop();

        // Middle rectangle
        builder.posColor(x - 0.375, y - 0.375, z, Color4f.WHITE);
        builder.posColor(x + 0.375, y - 0.375, z, Color4f.WHITE);
        builder.posColor(x + 0.375, y + 0.375, z, Color4f.WHITE);
        builder.posColor(x - 0.375, y + 0.375, z, Color4f.WHITE);

        builder.draw();

        RenderWrap.popMatrix();
    }

    protected static void blockTargetingOverlayTranslations(double x, double y, double z,
                                                            Direction side, Direction playerFacing)
    {
        RenderWrap.translate(x, y, z);

        switch (side)
        {
            case DOWN:
                RenderWrap.rotate(180f - playerFacing.getHorizontalAngle(), 0, 1f, 0);
                RenderWrap.rotate( 90f, 1f, 0, 0);
                break;
            case UP:
                RenderWrap.rotate(180f - playerFacing.getHorizontalAngle(), 0, 1f, 0);
                RenderWrap.rotate(-90f, 1f, 0, 0);
                break;
            case NORTH:
                RenderWrap.rotate(180f, 0, 1f, 0);
                break;
            case SOUTH:
                RenderWrap.rotate(   0, 0, 1f, 0);
                break;
            case WEST:
                RenderWrap.rotate(-90f, 0, 1f, 0);
                break;
            case EAST:
                RenderWrap.rotate( 90f, 0, 1f, 0);
                break;
        }

        RenderWrap.translate(-x, -y, -z + 0.501);
    }
}
