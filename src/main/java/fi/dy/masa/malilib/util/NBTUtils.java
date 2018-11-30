package fi.dy.masa.malilib.util;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class NBTUtils
{
    public static NBTTagCompound createBlockPosTag(Vec3i pos)
    {
        return writeBlockPosToTag(pos, new NBTTagCompound());
    }

    public static NBTTagCompound writeBlockPosToTag(Vec3i pos, NBTTagCompound tag)
    {
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
        return tag;
    }

    @Nullable
    public static BlockPos readBlockPos(@Nullable NBTTagCompound tag)
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

    public static NBTTagCompound writeVec3dToTag(Vec3d vec, NBTTagCompound tag)
    {
        tag.putDouble("dx", vec.x);
        tag.putDouble("dy", vec.y);
        tag.putDouble("dz", vec.z);
        return tag;
    }

    public static NBTTagCompound writeEntityPositionToTag(Vec3d pos, NBTTagCompound tag)
    {
        NBTTagList posList = new NBTTagList();

        posList.add(new NBTTagDouble(pos.x));
        posList.add(new NBTTagDouble(pos.y));
        posList.add(new NBTTagDouble(pos.z));
        tag.put("Pos", posList);

        return tag;
    }

    @Nullable
    public static Vec3d readVec3d(@Nullable NBTTagCompound tag)
    {
        if (tag != null &&
            tag.contains("dx", Constants.NBT.TAG_DOUBLE) &&
            tag.contains("dy", Constants.NBT.TAG_DOUBLE) &&
            tag.contains("dz", Constants.NBT.TAG_DOUBLE))
        {
            return new Vec3d(tag.getDouble("dx"), tag.getDouble("dy"), tag.getDouble("dz"));
        }

        return null;
    }

    @Nullable
    public static Vec3d readEntityPositionFromTag(@Nullable NBTTagCompound tag)
    {
        if (tag != null && tag.contains("Pos", Constants.NBT.TAG_LIST))
        {
            NBTTagList tagList = tag.getList("Pos", Constants.NBT.TAG_DOUBLE);

            if (tagList.getTagType() == Constants.NBT.TAG_DOUBLE && tagList.size() == 3)
            {
                return new Vec3d(tagList.getDouble(0), tagList.getDouble(1), tagList.getDouble(2));
            }
        }

        return null;
    }
}
