package fi.dy.masa.malilib.util;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class NBTUtils
{
    public static CompoundTag createBlockPosTag(Vec3i pos)
    {
        return writeBlockPosToTag(pos, new CompoundTag());
    }

    public static CompoundTag writeBlockPosToTag(Vec3i pos, CompoundTag tag)
    {
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
        return tag;
    }

    @Nullable
    public static BlockPos readBlockPos(@Nullable CompoundTag tag)
    {
        if (tag != null &&
            tag.containsKey("x", Constants.NBT.TAG_INT) &&
            tag.containsKey("y", Constants.NBT.TAG_INT) &&
            tag.containsKey("z", Constants.NBT.TAG_INT))
        {
            return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
        }

        return null;
    }

    public static CompoundTag writeVec3dToTag(Vec3d vec, CompoundTag tag)
    {
        tag.putDouble("dx", vec.x);
        tag.putDouble("dy", vec.y);
        tag.putDouble("dz", vec.z);
        return tag;
    }

    public static CompoundTag writeEntityPositionToTag(Vec3d pos, CompoundTag tag)
    {
        ListTag posList = new ListTag();

        posList.add(new DoubleTag(pos.x));
        posList.add(new DoubleTag(pos.y));
        posList.add(new DoubleTag(pos.z));
        tag.put("Pos", posList);

        return tag;
    }

    @Nullable
    public static Vec3d readVec3d(@Nullable CompoundTag tag)
    {
        if (tag != null &&
            tag.containsKey("dx", Constants.NBT.TAG_DOUBLE) &&
            tag.containsKey("dy", Constants.NBT.TAG_DOUBLE) &&
            tag.containsKey("dz", Constants.NBT.TAG_DOUBLE))
        {
            return new Vec3d(tag.getDouble("dx"), tag.getDouble("dy"), tag.getDouble("dz"));
        }

        return null;
    }

    @Nullable
    public static Vec3d readEntityPositionFromTag(@Nullable CompoundTag tag)
    {
        if (tag != null && tag.containsKey("Pos", Constants.NBT.TAG_LIST))
        {
            ListTag tagList = tag.getList("Pos", Constants.NBT.TAG_DOUBLE);

            if (tagList.getType() == Constants.NBT.TAG_DOUBLE && tagList.size() == 3)
            {
                return new Vec3d(tagList.getDouble(0), tagList.getDouble(1), tagList.getDouble(2));
            }
        }

        return null;
    }
}
