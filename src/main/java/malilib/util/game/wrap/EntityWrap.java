package malilib.util.game.wrap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import malilib.util.MathUtils;
import malilib.util.position.BlockPos;
import malilib.util.position.Direction;
import malilib.util.position.Vec3d;

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
        // TODO b1.7.3 eye pos or feet pos?
        return new Vec3d(entity.x, entity.y, entity.z);
    }

    public static BlockPos getEntityBlockPos(Entity entity)
    {
        // TODO b1.7.3 eye pos or feet pos?
        return BlockPos.ofFloored(entity.x, entity.y, entity.z);
    }

    public static double getX(Entity entity)
    {
        return entity.x;
    }

    public static double getY(Entity entity)
    {
        return entity.y;
    }

    public static double getZ(Entity entity)
    {
        return entity.z;
    }

    public static float getYaw(Entity entity)
    {
        return entity.yaw;
    }

    public static float getPitch(Entity entity)
    {
        return entity.pitch;
    }

    public static double lerpX(Entity entity, float tickDelta)
    {
        // TODO b1.7.3 is this the correct field?
        double lastTickPos = entity.prevTickX;
        return lastTickPos + (getX(entity) - lastTickPos) * tickDelta;
    }

    public static double lerpY(Entity entity, float tickDelta)
    {
        double lastTickPos = entity.prevTickY;
        return lastTickPos + (getY(entity) - lastTickPos) * tickDelta;
    }

    public static double lerpZ(Entity entity, float tickDelta)
    {
        double lastTickPos = entity.prevTickZ;
        return lastTickPos + (getZ(entity) - lastTickPos) * tickDelta;
    }

    public static int getChunkX(Entity entity)
    {
        return MathUtils.floor(getX(entity) / 16.0);
    }

    public static int getChunkY(Entity entity)
    {
        return MathUtils.floor(getY(entity) / 16.0);
    }

    public static int getChunkZ(Entity entity)
    {
        return MathUtils.floor(getZ(entity) / 16.0);
    }

    public static void setYaw(Entity entity, float yaw)
    {
        entity.yaw = yaw;
    }

    public static void setPitch(Entity entity, float pitch)
    {
        entity.pitch = pitch;
    }

    public static Direction getClosestHorizontalLookingDirection(Entity entity)
    {
        return Direction.fromAngle(EntityWrap.getYaw(entity));
    }

    /**
     * @param verticalThreshold The pitch rotation angle over which the up or down direction is preferred over the horizontal directions
     * @return the closest direction the entity is currently looking at.
     */
    public static Direction getClosestLookingDirection(Entity entity, float verticalThreshold)
    {
        float pitch = EntityWrap.getPitch(entity);

        if (pitch > verticalThreshold)
        {
            return Direction.DOWN;
        }
        else if (-pitch > verticalThreshold)
        {
            return Direction.UP;
        }

        return getClosestHorizontalLookingDirection(entity);
    }

    public static ItemStack getMainHandItem(PlayerEntity entity)
    {
        return entity.inventory.getMainHandStack();
    }

    /*
    public static ItemStack getOffHandItem(EntityLivingBase entity)
    {
        return getHeldItem(entity, EnumHand.OFF_HAND);
    }

    public static ItemStack getHeldItem(EntityLivingBase entity, EnumHand hand)
    {
        return entity.getHeldItem(hand);
    }
    */

    /**
     * Checks if the requested item is currently in the entity's hand such that it would be used for using/placing.
     * This means, that it must either be in the main hand, or the main hand must be empty and the item is in the offhand.
     * @param lenient if true, then NBT tags and also damage of damageable items are ignored
     */
    /*
    @Nullable
    public static EnumHand getUsedHandForItem(EntityLivingBase entity, ItemStack stack, boolean lenient)
    {
        EnumHand hand = null;
        EnumHand tmpHand = ItemWrap.isEmpty(getMainHandItem(entity)) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        ItemStack handStack = getHeldItem(entity, tmpHand);

        if ((lenient          && InventoryUtils.areItemsEqualIgnoreDurability(handStack, stack)) ||
            (lenient == false && InventoryUtils.areStacksEqual(handStack, stack)))
        {
            hand = tmpHand;
        }

        return hand;
    }
    */
}
