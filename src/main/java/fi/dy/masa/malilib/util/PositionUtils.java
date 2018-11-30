package fi.dy.masa.malilib.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PositionUtils
{
    /**
     * Returns the closest direction the given entity is looking towards,
     * with a vertical/pitch threshold of 60 degrees.
     * @param entity
     * @return
     */
    public static EnumFacing getClosestLookingDirection(Entity entity)
    {
        return getClosestLookingDirection(entity, 60);
    }

    /**
     * Returns the closest direction the given entity is looking towards.
     * @param entity
     * @param verticalThreshold the pitch threshold to return the up or down facing instead of horizontals
     * @return
     */
    public static EnumFacing getClosestLookingDirection(Entity entity, float verticalThreshold)
    {
        if (entity.rotationPitch >= verticalThreshold)
        {
            return EnumFacing.DOWN;
        }
        else if (entity.rotationPitch <= -verticalThreshold)
        {
            return EnumFacing.UP;
        }

        return entity.getHorizontalFacing();
    }

    /**
     * Returns the closest block position directly infront of the
     * given entity that is not colliding with it.
     * @param entity
     * @return
     */
    public static BlockPos getPositionInfrontOfEntity(Entity entity)
    {
        return getPositionInfrontOfEntity(entity, 60);
    }

    /**
     * Returns the closest block position directly infront of the
     * given entity that is not colliding with it.
     * @param entity
     * @param verticalThreshold
     * @return
     */
    public static BlockPos getPositionInfrontOfEntity(Entity entity, float verticalThreshold)
    {
        BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);

        if (entity.rotationPitch >= verticalThreshold)
        {
            return pos.down();
        }
        else if (entity.rotationPitch <= -verticalThreshold)
        {
            return new BlockPos(entity.posX, Math.ceil(entity.getBoundingBox().maxY), entity.posZ);
        }

        double y = Math.floor(entity.posY + entity.getEyeHeight());

        switch (entity.getHorizontalFacing())
        {
            case EAST:
                return new BlockPos((int) Math.ceil( entity.posX + entity.width / 2),     (int) y, (int) Math.floor(entity.posZ));
            case WEST:
                return new BlockPos((int) Math.floor(entity.posX - entity.width / 2) - 1, (int) y, (int) Math.floor(entity.posZ));
            case SOUTH:
                return new BlockPos((int) Math.floor(entity.posX), (int) y, (int) Math.ceil( entity.posZ + entity.width / 2)    );
            case NORTH:
                return new BlockPos((int) Math.floor(entity.posX), (int) y, (int) Math.floor(entity.posZ - entity.width / 2) - 1);
            default:
        }

        return pos;
    }

    /**
     * Returns the hit vector at the center point of the given side/face of the given block position.
     * @param basePos
     * @param facing
     * @return
     */
    public static Vec3d getHitVecCenter(BlockPos basePos, EnumFacing facing)
    {
        int x = basePos.getX();
        int y = basePos.getY();
        int z = basePos.getZ();

        switch (facing)
        {
            case UP:    return new Vec3d(x + 0.5, y + 1  , z + 0.5);
            case DOWN:  return new Vec3d(x + 0.5, y      , z + 0.5);
            case NORTH: return new Vec3d(x + 0.5, y + 0.5, z      );
            case SOUTH: return new Vec3d(x + 0.5, y + 0.5, z + 1  );
            case WEST:  return new Vec3d(x      , y + 0.5, z      );
            case EAST:  return new Vec3d(x + 1  , y + 0.5, z + 1);
            default:    return new Vec3d(x, y, z);
        }
    }
}
