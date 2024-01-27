package malilib.util.position;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;

public class HitResult
{
    public final Type type;
    @Nullable public final BlockPos blockPos;
    @Nullable public final Direction side;
    @Nullable public final Vec3d pos;
    @Nullable public final Entity entity;

    public HitResult(Type type, @Nullable BlockPos blockPos, @Nullable Direction side, @Nullable Vec3d pos, @Nullable Entity entity)
    {
        this.type = type;
        this.blockPos = blockPos;
        this.side = side;
        this.pos = pos;
        this.entity = entity;
    }

    public enum Type
    {
        MISS,
        BLOCK,
        ENTITY;
    }

    public static HitResult miss()
    {
        return new HitResult(Type.MISS, null, null, null, null);
    }

    public static HitResult block(BlockPos pos, Direction side, Vec3d exactPos)
    {
        return new HitResult(Type.BLOCK, pos, side, exactPos, null);
    }

    public static HitResult entity(Entity entity, Vec3d exactPos)
    {
        return new HitResult(Type.ENTITY, null, null, exactPos, entity);
    }
}
