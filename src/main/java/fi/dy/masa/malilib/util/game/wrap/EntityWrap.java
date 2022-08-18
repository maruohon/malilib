package fi.dy.masa.malilib.util.game.wrap;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityWrap
{
    public static BlockPos getCameraEntityBlockPos()
    {
        Entity entity = GameUtils.getCameraEntity();
        return entity != null ? getEntityBlockPos(entity) : BlockPos.ORIGIN;
    }

    public static Vec3d getCameraEntityPosition()
    {
        Entity entity = GameUtils.getCameraEntity();
        return entity != null ? getEntityPos(entity) : Vec3d.ZERO;
    }

    public static BlockPos getPlayerBlockPos()
    {
        Entity entity = GameUtils.getClientPlayer();
        return entity != null ? getEntityBlockPos(entity) : BlockPos.ORIGIN;
    }

    public static Vec3d getEntityPos(Entity entity)
    {
        return entity.getPos();
    }

    public static BlockPos getEntityBlockPos(Entity entity)
    {
        return new BlockPos(entity);
    }

    public static double getX(Entity entity)
    {
        return entity.posX;
    }

    public static double getY(Entity entity)
    {
        return entity.posY;
    }

    public static double getZ(Entity entity)
    {
        return entity.posZ;
    }

    public static float getYaw(Entity entity)
    {
        return entity.rotationYaw;
    }

    public static float getPitch(Entity entity)
    {
        return entity.rotationPitch;
    }

    public static double lerpX(Entity entity, float tickDelta)
    {
        double lastTickPos = entity.lastRenderX;
        return lastTickPos + (getX(entity) - lastTickPos) * tickDelta;
    }

    public static double lerpY(Entity entity, float tickDelta)
    {
        double lastTickPos = entity.lastRenderY;
        return lastTickPos + (getY(entity) - lastTickPos) * tickDelta;
    }

    public static double lerpZ(Entity entity, float tickDelta)
    {
        double lastTickPos = entity.lastRenderZ;
        return lastTickPos + (getZ(entity) - lastTickPos) * tickDelta;
    }

    public static int getChunkX(Entity entity)
    {
        return MathHelper.floor(getX(entity) / 16.0);
    }

    public static int getChunkY(Entity entity)
    {
        return MathHelper.floor(getY(entity) / 16.0);
    }

    public static int getChunkZ(Entity entity)
    {
        return MathHelper.floor(getZ(entity) / 16.0);
    }

    public static void setYaw(Entity entity, float yaw)
    {
        entity.rotationYaw = yaw;
    }

    public static void setPitch(Entity entity, float pitch)
    {
        entity.rotationPitch = pitch;
    }
}
