package fi.dy.masa.malilib.util.nbt;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.data.Constants;

public class NbtUtils
{
    public static boolean hasByte(NbtCompound tag, String name)
    {
        return tag.contains(name, Constants.NBT.TAG_BYTE);
    }

    public static boolean hasShort(NbtCompound tag, String name)
    {
        return tag.contains(name, Constants.NBT.TAG_SHORT);
    }

    public static boolean hasInt(NbtCompound tag, String name)
    {
        return tag.contains(name, Constants.NBT.TAG_INT);
    }

    public static boolean hasLong(NbtCompound tag, String name)
    {
        return tag.contains(name, Constants.NBT.TAG_LONG);
    }

    public static boolean hasFloat(NbtCompound tag, String name)
    {
        return tag.contains(name, Constants.NBT.TAG_FLOAT);
    }

    public static boolean hasDouble(NbtCompound tag, String name)
    {
        return tag.contains(name, Constants.NBT.TAG_DOUBLE);
    }

    public static boolean hasString(NbtCompound tag, String name)
    {
        return tag.contains(name, Constants.NBT.TAG_STRING);
    }

    public static boolean hasCompound(NbtCompound tag, String name)
    {
        return tag.contains(name, Constants.NBT.TAG_COMPOUND);
    }

    public static boolean hasList(NbtCompound tag, String name)
    {
        return tag.contains(name, Constants.NBT.TAG_LIST);
    }

    public static boolean hasByteArray(NbtCompound tag, String name)
    {
        return tag.contains(name, Constants.NBT.TAG_BYTE_ARRAY);
    }

    public static boolean hasIntArray(NbtCompound tag, String name)
    {
        return tag.contains(name, Constants.NBT.TAG_INT_ARRAY);
    }

    public static boolean hasLongArray(NbtCompound tag, String name)
    {
        return tag.contains(name, Constants.NBT.TAG_LONG_ARRAY);
    }

    public static boolean getBoolean(NbtCompound tag, String name)
    {
        return tag.getBoolean(name);
    }

    public static byte getByte(NbtCompound tag, String name)
    {
        return tag.getByte(name);
    }

    public static short getShort(NbtCompound tag, String name)
    {
        return tag.getShort(name);
    }

    public static int getInt(NbtCompound tag, String name)
    {
        return tag.getInt(name);
    }

    public static long getLong(NbtCompound tag, String name)
    {
        return tag.getLong(name);
    }

    public static float getFloat(NbtCompound tag, String name)
    {
        return tag.getFloat(name);
    }

    public static double getDouble(NbtCompound tag, String name)
    {
        return tag.getDouble(name);
    }

    public static String getString(NbtCompound tag, String name)
    {
        return tag.getString(name);
    }

    public static NbtCompound getCompound(NbtCompound tag, String name)
    {
        return tag.getCompound(name);
    }

    public static NbtList getList(NbtCompound tag, String name, int type)
    {
        return tag.getList(name, type);
    }

    public static byte[] getByteArray(NbtCompound tag, String name)
    {
        return tag.getByteArray(name);
    }

    public static int[] getIntArray(NbtCompound tag, String name)
    {
        return tag.getIntArray(name);
    }

    /*
    public static long[] getLongArray(NbtCompound tag, String name)
    {
        return tag.getTag(name);
    }
    */

    public static void remove(NbtCompound tag, String name)
    {
        tag.remove(name);
    }

    public static Set<String> getKeys(NbtCompound tag)
    {
        return tag.getKeys();
    }

    public static int getListSize(NbtList list)
    {
        return list.size();
    }

    public static NbtList getListOfCompounds(NbtCompound tag, String name)
    {
        return tag.getList(name, Constants.NBT.TAG_COMPOUND);
    }

    @Nullable
    public static UUID readUUID(NbtCompound tag)
    {
        return readUUID(tag, "UUIDM", "UUIDL");
    }

    @Nullable
    public static UUID readUUID(NbtCompound tag, String keyM, String keyL)
    {
        if (hasLong(tag, keyM) && hasLong(tag, keyL))
        {
            return new UUID(tag.getLong(keyM), tag.getLong(keyL));
        }

        return null;
    }

    public static void writeUUID(NbtCompound tag, UUID uuid)
    {
        writeUUID(tag, uuid, "UUIDM", "UUIDL");
    }

    public static void writeUUID(NbtCompound tag, UUID uuid, String keyM, String keyL)
    {
        tag.putLong(keyM, uuid.getMostSignificantBits());
        tag.putLong(keyL, uuid.getLeastSignificantBits());
    }

    public static NbtCompound getOrCreateCompound(NbtCompound tagIn, String tagName)
    {
        NbtCompound nbt;

        if (hasCompound(tagIn, tagName))
        {
            nbt = tagIn.getCompound(tagName);
        }
        else
        {
            nbt = new NbtCompound();
            tagIn.put(tagName, nbt);
        }

        return nbt;
    }

    public static <T> NbtList asListTag(Collection<T> values, Function<T, NbtElement> tagFactory)
    {
        NbtList list = new NbtList();

        for (T val : values)
        {
            list.add(tagFactory.apply(val));
        }

        return list;
    }

    public static NbtCompound createBlockPosTag(Vec3i pos)
    {
        return writeBlockPosToTag(pos, new NbtCompound());
    }

    public static NbtCompound writeBlockPosToTag(Vec3i pos, NbtCompound tag)
    {
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
        return tag;
    }

    @Nullable
    public static NbtCompound writeBlockPosToListTag(Vec3i pos, NbtCompound tag, String tagName)
    {
        NbtList tagList = new NbtList();

        tagList.add(NbtInt.of(pos.getX()));
        tagList.add(NbtInt.of(pos.getY()));
        tagList.add(NbtInt.of(pos.getZ()));
        tag.put(tagName, tagList);

        return tag;
    }

    @Nullable
    public static NbtCompound writeBlockPosToArrayTag(Vec3i pos, NbtCompound tag, String tagName)
    {
        int[] arr = new int[] { pos.getX(), pos.getY(), pos.getZ() };

        tag.putIntArray(tagName, arr);

        return tag;
    }

    @Nullable
    public static BlockPos readBlockPos(@Nullable NbtCompound tag)
    {
        if (tag != null &&
            hasInt(tag, "x") &&
            hasInt(tag, "y") &&
            hasInt(tag, "z"))
        {
            return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
        }

        return null;
    }

    @Nullable
    public static BlockPos readBlockPosFromListTag(NbtCompound tag, String tagName)
    {
        if (hasList(tag, tagName))
        {
            NbtList tagList = tag.getList(tagName, Constants.NBT.TAG_INT);

            if (tagList.size() == 3)
            {
                return new BlockPos(tagList.getInt(0), tagList.getInt(1), tagList.getInt(2));
            }
        }

        return null;
    }

    @Nullable
    public static BlockPos readBlockPosFromArrayTag(NbtCompound tag, String tagName)
    {
        if (hasIntArray(tag, tagName))
        {
            int[] pos = tag.getIntArray("Pos");

            if (pos.length == 3)
            {
                return new BlockPos(pos[0], pos[1], pos[2]);
            }
        }

        return null;
    }

    public static NbtCompound removeBlockPosFromTag(NbtCompound tag)
    {
        tag.remove("x");
        tag.remove("y");
        tag.remove("z");
        return tag;
    }

    public static NbtCompound writeVec3dToListTag(Vec3d pos, NbtCompound tag)
    {
        return writeVec3dToListTag(pos, tag, "Pos");
    }

    public static NbtCompound writeVec3dToListTag(Vec3d pos, NbtCompound tag, String tagName)
    {
        NbtList posList = new NbtList();

        posList.add(NbtDouble.of(pos.x));
        posList.add(NbtDouble.of(pos.y));
        posList.add(NbtDouble.of(pos.z));
        tag.put(tagName, posList);

        return tag;
    }

    @Nullable
    public static Vec3d readVec3d(@Nullable NbtCompound tag)
    {
        if (tag != null &&
            hasDouble(tag, "dx") &&
            hasDouble(tag, "dy") &&
            hasDouble(tag, "dz"))
        {
            return new Vec3d(tag.getDouble("dx"), tag.getDouble("dy"), tag.getDouble("dz"));
        }

        return null;
    }

    @Nullable
    public static Vec3d readVec3dFromListTag(@Nullable NbtCompound tag)
    {
        return readVec3dFromListTag(tag, "Pos");
    }

    @Nullable
    public static Vec3d readVec3dFromListTag(@Nullable NbtCompound tag, String tagName)
    {
        if (tag != null && hasList(tag, tagName))
        {
            NbtList tagList = tag.getList(tagName, Constants.NBT.TAG_DOUBLE);

            if (tagList.getHeldType() == Constants.NBT.TAG_DOUBLE && tagList.size() == 3)
            {
                return new Vec3d(tagList.getDouble(0), tagList.getDouble(1), tagList.getDouble(2));
            }
        }

        return null;
    }

    @Nullable
    public static NbtCompound readNbtFromFile(File file)
    {
        if (file.exists() == false || file.canRead() == false)
        {
            return null;
        }

        try
        {
            FileInputStream is = new FileInputStream(file);
            NbtCompound nbt = NbtIo.readCompressed(is);
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
