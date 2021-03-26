package fi.dy.masa.malilib.util;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

public class NBTUtils
{
    public static CompoundNBT createBlockPosTag(Vector3i pos)
    {
        return writeBlockPosToTag(pos, new CompoundNBT());
    }

    public static CompoundNBT writeBlockPosToTag(Vector3i pos, CompoundNBT tag)
    {
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
        return tag;
    }

    @Nullable
    public static BlockPos readBlockPos(@Nullable CompoundNBT tag)
    {
        if (tag != null &&
            tag.contains("x", Constants.NBT.TAG_INT) &&
            tag.contains("y", Constants.NBT.TAG_INT) &&
            tag.contains("z", Constants.NBT.TAG_INT))
        {
            return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
        }

        return null;
    }

    public static CompoundNBT writeVec3dToTag(Vector3d vec, CompoundNBT tag)
    {
        tag.putDouble("dx", vec.x);
        tag.putDouble("dy", vec.y);
        tag.putDouble("dz", vec.z);
        return tag;
    }

    public static CompoundNBT writeEntityPositionToTag(Vector3d pos, CompoundNBT tag)
    {
        ListNBT posList = new ListNBT();

        posList.add(DoubleNBT.valueOf(pos.x));
        posList.add(DoubleNBT.valueOf(pos.y));
        posList.add(DoubleNBT.valueOf(pos.z));
        tag.put("Pos", posList);

        return tag;
    }

    @Nullable
    public static Vector3d readVec3d(@Nullable CompoundNBT tag)
    {
        if (tag != null &&
            tag.contains("dx", Constants.NBT.TAG_DOUBLE) &&
            tag.contains("dy", Constants.NBT.TAG_DOUBLE) &&
            tag.contains("dz", Constants.NBT.TAG_DOUBLE))
        {
            return new Vector3d(tag.getDouble("dx"), tag.getDouble("dy"), tag.getDouble("dz"));
        }

        return null;
    }

    @Nullable
    public static Vector3d readEntityPositionFromTag(@Nullable CompoundNBT tag)
    {
        if (tag != null && tag.contains("Pos", Constants.NBT.TAG_LIST))
        {
            ListNBT tagList = tag.getList("Pos", Constants.NBT.TAG_DOUBLE);

            if (tagList.getTagType() == Constants.NBT.TAG_DOUBLE && tagList.size() == 3)
            {
                return new Vector3d(tagList.getDouble(0), tagList.getDouble(1), tagList.getDouble(2));
            }
        }

        return null;
    }
}
