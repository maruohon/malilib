package malilib.util.position;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public enum Quadrant
{
    NORTH_WEST,
    NORTH_EAST,
    SOUTH_WEST,
    SOUTH_EAST;

    public static Quadrant getQuadrant(BlockPos pos, Vec3 center)
    {
        return getQuadrant(pos.getX(), pos.getZ(), center);
    }

    public static Quadrant getQuadrant(int x, int z, Vec3 center)
    {
        return getQuadrant((double) x, (double) z, center);
    }

    public static Quadrant getQuadrant(double x, double z, Vec3 center)
    {
        // West
        if (x <= center.x)
        {
            // North
            if (z <= center.z)
            {
                return NORTH_WEST;
            }
            // South
            else
            {
                return SOUTH_WEST;
            }
        }
        // East
        else
        {
            // North
            if (z <= center.z)
            {
                return NORTH_EAST;
            }
            // South
            else
            {
                return SOUTH_EAST;
            }
        }
    }
}