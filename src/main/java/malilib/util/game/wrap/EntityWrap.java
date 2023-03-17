package malilib.util.game.wrap;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import malilib.util.inventory.InventoryUtils;

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

    public static Direction getClosestHorizontalLookingDirection(Entity entity)
    {
        return Direction.fromRotation(EntityWrap.getYaw(entity));
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

    public static ItemStack getMainHandItem(LivingEntity entity)
    {
        return getHeldItem(entity, Hand.MAIN_HAND);
    }

    public static ItemStack getOffHandItem(LivingEntity entity)
    {
        return getHeldItem(entity, Hand.OFF_HAND);
    }

    public static ItemStack getHeldItem(LivingEntity entity, Hand hand)
    {
        return entity.getStackInHand(hand);
    }

    /**
     * Checks if the requested item is currently in the entity's hand such that it would be used for using/placing.
     * This means, that it must either be in the main hand, or the main hand must be empty and the item is in the offhand.
     * @param lenient if true, then NBT tags and also damage of damageable items are ignored
     */
    @Nullable
    public static Hand getUsedHandForItem(LivingEntity entity, ItemStack stack, boolean lenient)
    {
        Hand hand = null;
        Hand tmpHand = ItemWrap.isEmpty(getMainHandItem(entity)) ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack handStack = getHeldItem(entity, tmpHand);

        if ((lenient          && InventoryUtils.areItemsEqualIgnoreDurability(handStack, stack)) ||
            (lenient == false && InventoryUtils.areStacksEqual(handStack, stack)))
        {
            hand = tmpHand;
        }

        return hand;
    }
}
