package malilib.util.game.wrap;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import malilib.util.inventory.InventoryUtils;

public class EntityWrap
{
    public static BlockPos getCameraEntityBlockPos()
    {
        Entity entity = GameUtils.getCameraEntity();
        return entity != null ? getEntityBlockPos(entity) : BlockPos.ZERO;
    }

    public static Vec3 getCameraEntityPosition()
    {
        Entity entity = GameUtils.getCameraEntity();
        return entity != null ? getEntityPos(entity) : Vec3.ZERO;
    }

    public static BlockPos getPlayerBlockPos()
    {
        Entity entity = GameUtils.getClientPlayer();
        return entity != null ? getEntityBlockPos(entity) : BlockPos.ZERO;
    }

    public static Vec3 getEntityPos(Entity entity)
    {
        return entity.position();
    }

    public static BlockPos getEntityBlockPos(Entity entity)
    {
        return new BlockPos(entity.position());
    }

    public static double getX(Entity entity)
    {
        return entity.getX();
    }

    public static double getY(Entity entity)
    {
        return entity.getY();
    }

    public static double getZ(Entity entity)
    {
        return entity.getZ();
    }

    public static float getYaw(Entity entity)
    {
        return entity.getYRot();
    }

    public static float getPitch(Entity entity)
    {
        return entity.getXRot();
    }

    public static double lerpX(Entity entity, float tickDelta)
    {
        double lastTickPos = entity.xOld;
        return lastTickPos + (getX(entity) - lastTickPos) * tickDelta;
    }

    public static double lerpY(Entity entity, float tickDelta)
    {
        double lastTickPos = entity.yOld;
        return lastTickPos + (getY(entity) - lastTickPos) * tickDelta;
    }

    public static double lerpZ(Entity entity, float tickDelta)
    {
        double lastTickPos = entity.zOld;
        return lastTickPos + (getZ(entity) - lastTickPos) * tickDelta;
    }

    public static int getChunkX(Entity entity)
    {
        return Mth.floor(getX(entity) / 16.0);
    }

    public static int getChunkY(Entity entity)
    {
        return Mth.floor(getY(entity) / 16.0);
    }

    public static int getChunkZ(Entity entity)
    {
        return Mth.floor(getZ(entity) / 16.0);
    }

    public static void setYaw(Entity entity, float yaw)
    {
        entity.setYRot(yaw);
    }

    public static void setPitch(Entity entity, float pitch)
    {
        entity.setXRot(pitch);
    }

    public static Direction getClosestHorizontalLookingDirection(Entity entity)
    {
        return Direction.fromYRot(EntityWrap.getYaw(entity));
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
        return getHeldItem(entity, InteractionHand.MAIN_HAND);
    }

    public static ItemStack getOffHandItem(LivingEntity entity)
    {
        return getHeldItem(entity, InteractionHand.OFF_HAND);
    }

    public static ItemStack getHeldItem(LivingEntity entity, InteractionHand hand)
    {
        return entity.getItemInHand(hand);
    }

    /**
     * Checks if the requested item is currently in the entity's hand such that it would be used for using/placing.
     * This means, that it must either be in the main hand, or the main hand must be empty and the item is in the offhand.
     * @param lenient if true, then NBT tags and also damage of damageable items are ignored
     */
    @Nullable
    public static InteractionHand getUsedHandForItem(LivingEntity entity, ItemStack stack, boolean lenient)
    {
        InteractionHand hand = null;
        InteractionHand tmpHand = ItemWrap.isEmpty(getMainHandItem(entity)) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack handStack = getHeldItem(entity, tmpHand);

        if ((lenient          && InventoryUtils.areItemsEqualIgnoreDurability(handStack, stack)) ||
            (lenient == false && InventoryUtils.areStacksEqual(handStack, stack)))
        {
            hand = tmpHand;
        }

        return hand;
    }
}
