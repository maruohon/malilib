package malilib.util.game.wrap;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import malilib.util.MathUtils;
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
        return entity.getPositionVector();
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
        double lastTickPos = entity.lastTickPosX;
        return lastTickPos + (getX(entity) - lastTickPos) * tickDelta;
    }

    public static double lerpY(Entity entity, float tickDelta)
    {
        double lastTickPos = entity.lastTickPosY;
        return lastTickPos + (getY(entity) - lastTickPos) * tickDelta;
    }

    public static double lerpZ(Entity entity, float tickDelta)
    {
        double lastTickPos = entity.lastTickPosZ;
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
        entity.rotationYaw = yaw;
    }

    public static void setPitch(Entity entity, float pitch)
    {
        entity.rotationPitch = pitch;
    }

    public static EnumFacing getClosestHorizontalLookingDirection(Entity entity)
    {
        return EnumFacing.fromAngle(EntityWrap.getYaw(entity));
    }

    /**
     * @param verticalThreshold The pitch rotation angle over which the up or down direction is preferred over the horizontal directions
     * @return the closest direction the entity is currently looking at.
     */
    public static EnumFacing getClosestLookingDirection(Entity entity, float verticalThreshold)
    {
        float pitch = EntityWrap.getPitch(entity);

        if (pitch > verticalThreshold)
        {
            return EnumFacing.DOWN;
        }
        else if (-pitch > verticalThreshold)
        {
            return EnumFacing.UP;
        }

        return getClosestHorizontalLookingDirection(entity);
    }

    public static ItemStack getMainHandItem(EntityLivingBase entity)
    {
        return getHeldItem(entity, EnumHand.MAIN_HAND);
    }

    public static ItemStack getOffHandItem(EntityLivingBase entity)
    {
        return getHeldItem(entity, EnumHand.OFF_HAND);
    }

    public static ItemStack getHeldItem(EntityLivingBase entity, EnumHand hand)
    {
        return entity.getHeldItem(hand);
    }

    /**
     * Checks if the requested item is currently in the entity's hand such that it would be used for using/placing.
     * This means, that it must either be in the main hand, or the main hand must be empty and the item is in the offhand.
     * @param lenient if true, then NBT tags and also damage of damageable items are ignored
     */
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
}
