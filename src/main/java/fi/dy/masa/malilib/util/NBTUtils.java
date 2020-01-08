package fi.dy.masa.malilib.util;

import java.io.File;
import java.io.FileInputStream;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
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
        tag.setInteger("x", pos.getX());
        tag.setInteger("y", pos.getY());
        tag.setInteger("z", pos.getZ());
        return tag;
    }

    @Nullable
    public static NBTTagCompound writeBlockPosToListTag(Vec3i pos, NBTTagCompound tag, String tagName)
    {
        NBTTagList tagList = new NBTTagList();

        tagList.appendTag(new NBTTagInt(pos.getX()));
        tagList.appendTag(new NBTTagInt(pos.getY()));
        tagList.appendTag(new NBTTagInt(pos.getZ()));
        tag.setTag(tagName, tagList);

        return tag;
    }

    @Nullable
    public static NBTTagCompound writeBlockPosToArrayTag(Vec3i pos, NBTTagCompound tag, String tagName)
    {
        int[] arr = new int[] { pos.getX(), pos.getY(), pos.getZ() };

        tag.setIntArray(tagName, arr);

        return tag;
    }

    @Nullable
    public static BlockPos readBlockPos(@Nullable NBTTagCompound tag)
    {
        if (tag != null &&
            tag.hasKey("x", Constants.NBT.TAG_INT) &&
            tag.hasKey("y", Constants.NBT.TAG_INT) &&
            tag.hasKey("z", Constants.NBT.TAG_INT))
        {
            return new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
        }

        return null;
    }

    @Nullable
    public static BlockPos readBlockPosFromListTag(NBTTagCompound tag, String tagName)
    {
        if (tag.hasKey(tagName, Constants.NBT.TAG_LIST))
        {
            NBTTagList tagList = tag.getTagList(tagName, Constants.NBT.TAG_INT);

            if (tagList.tagCount() == 3)
            {
                return new BlockPos(tagList.getIntAt(0), tagList.getIntAt(1), tagList.getIntAt(2));
            }
        }

        return null;
    }

    @Nullable
    public static BlockPos readBlockPosFromArrayTag(NBTTagCompound tag, String tagName)
    {
        if (tag.hasKey(tagName, Constants.NBT.TAG_INT_ARRAY))
        {
            int[] pos = tag.getIntArray("Pos");

            if (pos.length == 3)
            {
                return new BlockPos(pos[0], pos[1], pos[2]);
            }
        }

        return null;
    }

    public static NBTTagCompound removeBlockPosFromTag(NBTTagCompound tag)
    {
        tag.removeTag("x");
        tag.removeTag("y");
        tag.removeTag("z");
        return tag;
    }

    public static NBTTagCompound writeVec3dToTag(Vec3d vec, NBTTagCompound tag)
    {
        tag.setDouble("dx", vec.x);
        tag.setDouble("dy", vec.y);
        tag.setDouble("dz", vec.z);
        return tag;
    }

    public static NBTTagCompound writeEntityPositionToTag(Vec3d pos, NBTTagCompound tag)
    {
        NBTTagList posList = new NBTTagList();

        posList.appendTag(new NBTTagDouble(pos.x));
        posList.appendTag(new NBTTagDouble(pos.y));
        posList.appendTag(new NBTTagDouble(pos.z));
        tag.setTag("Pos", posList);

        return tag;
    }

    @Nullable
    public static Vec3d readVec3d(@Nullable NBTTagCompound tag)
    {
        if (tag != null &&
            tag.hasKey("dx", Constants.NBT.TAG_DOUBLE) &&
            tag.hasKey("dy", Constants.NBT.TAG_DOUBLE) &&
            tag.hasKey("dz", Constants.NBT.TAG_DOUBLE))
        {
            return new Vec3d(tag.getDouble("dx"), tag.getDouble("dy"), tag.getDouble("dz"));
        }

        return null;
    }

    @Nullable
    public static Vec3d readEntityPositionFromTag(@Nullable NBTTagCompound tag)
    {
        if (tag != null && tag.hasKey("Pos", Constants.NBT.TAG_LIST))
        {
            NBTTagList tagList = tag.getTagList("Pos", Constants.NBT.TAG_DOUBLE);

            if (tagList.getTagType() == Constants.NBT.TAG_DOUBLE && tagList.tagCount() == 3)
            {
                return new Vec3d(tagList.getDoubleAt(0), tagList.getDoubleAt(1), tagList.getDoubleAt(2));
            }
        }

        return null;
    }

    @Nullable
    public static NBTTagCompound readNbtFromFile(File file)
    {
        if (file.exists() == false || file.canRead() == false)
        {
            return null;
        }

        try
        {
            FileInputStream is = new FileInputStream(file);
            NBTTagCompound nbt = CompressedStreamTools.readCompressed(is);
            is.close();

            return nbt;
        }
        catch (Exception e)
        {
        }

        return null;
    }
}
