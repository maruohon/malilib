package fi.dy.masa.malilib.util.nbt;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.data.Constants;

public class NbtUtils
{
    public static boolean hasByte(NBTTagCompound tag, String name)
    {
        return tag.hasKey(name, Constants.NBT.TAG_BYTE);
    }

    public static boolean hasShort(NBTTagCompound tag, String name)
    {
        return tag.hasKey(name, Constants.NBT.TAG_SHORT);
    }

    public static boolean hasInt(NBTTagCompound tag, String name)
    {
        return tag.hasKey(name, Constants.NBT.TAG_INT);
    }

    public static boolean hasLong(NBTTagCompound tag, String name)
    {
        return tag.hasKey(name, Constants.NBT.TAG_LONG);
    }

    public static boolean hasFloat(NBTTagCompound tag, String name)
    {
        return tag.hasKey(name, Constants.NBT.TAG_FLOAT);
    }

    public static boolean hasDouble(NBTTagCompound tag, String name)
    {
        return tag.hasKey(name, Constants.NBT.TAG_DOUBLE);
    }

    public static boolean hasString(NBTTagCompound tag, String name)
    {
        return tag.hasKey(name, Constants.NBT.TAG_STRING);
    }

    public static boolean hasCompound(NBTTagCompound tag, String name)
    {
        return tag.hasKey(name, Constants.NBT.TAG_COMPOUND);
    }

    public static boolean hasList(NBTTagCompound tag, String name)
    {
        return tag.hasKey(name, Constants.NBT.TAG_LIST);
    }

    public static boolean hasByteArray(NBTTagCompound tag, String name)
    {
        return tag.hasKey(name, Constants.NBT.TAG_BYTE_ARRAY);
    }

    public static boolean hasIntArray(NBTTagCompound tag, String name)
    {
        return tag.hasKey(name, Constants.NBT.TAG_INT_ARRAY);
    }

    public static boolean hasLongArray(NBTTagCompound tag, String name)
    {
        return tag.hasKey(name, Constants.NBT.TAG_LONG_ARRAY);
    }

    @Nullable
    public static UUID readUUID(NBTTagCompound tag)
    {
        return readUUID(tag, "UUIDM", "UUIDL");
    }

    @Nullable
    public static UUID readUUID(NBTTagCompound tag, String keyM, String keyL)
    {
        if (hasLong(tag, keyM) && hasLong(tag, keyL))
        {
            return new UUID(tag.getLong(keyM), tag.getLong(keyL));
        }

        return null;
    }

    public static void writeUUID(NBTTagCompound tag, UUID uuid)
    {
        writeUUID(tag, uuid, "UUIDM", "UUIDL");
    }

    public static void writeUUID(NBTTagCompound tag, UUID uuid, String keyM, String keyL)
    {
        tag.setLong(keyM, uuid.getMostSignificantBits());
        tag.setLong(keyL, uuid.getLeastSignificantBits());
    }

    public static NBTTagCompound getOrCreateCompound(NBTTagCompound tagIn, String tagName)
    {
        NBTTagCompound nbt;

        if (tagIn.hasKey(tagName, Constants.NBT.TAG_COMPOUND))
        {
            nbt = tagIn.getCompoundTag(tagName);
        }
        else
        {
            nbt = new NBTTagCompound();
            tagIn.setTag(tagName, nbt);
        }

        return nbt;
    }

    public static <T> NBTTagList asListTag(Collection<T> values, Function<T, NBTBase> tagFactory)
    {
        NBTTagList list = new NBTTagList();

        for (T val : values)
        {
            list.appendTag(tagFactory.apply(val));
        }

        return list;
    }

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

    public static NBTTagCompound writeVec3dToListTag(Vec3d pos, NBTTagCompound tag)
    {
        return writeVec3dToListTag(pos, tag, "Pos");
    }

    public static NBTTagCompound writeVec3dToListTag(Vec3d pos, NBTTagCompound tag, String tagName)
    {
        NBTTagList posList = new NBTTagList();

        posList.appendTag(new NBTTagDouble(pos.x));
        posList.appendTag(new NBTTagDouble(pos.y));
        posList.appendTag(new NBTTagDouble(pos.z));
        tag.setTag(tagName, posList);

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
    public static Vec3d readVec3dFromListTag(@Nullable NBTTagCompound tag)
    {
        return readVec3dFromListTag(tag, "Pos");
    }

    @Nullable
    public static Vec3d readVec3dFromListTag(@Nullable NBTTagCompound tag, String tagName)
    {
        if (tag != null && tag.hasKey(tagName, Constants.NBT.TAG_LIST))
        {
            NBTTagList tagList = tag.getTagList(tagName, Constants.NBT.TAG_DOUBLE);

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
            MaLiLib.LOGGER.warn("Failed to read NBT data from file '{}'", file.getAbsolutePath());
        }

        return null;
    }
}
