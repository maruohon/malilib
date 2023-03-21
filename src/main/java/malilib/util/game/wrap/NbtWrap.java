package malilib.util.game.wrap;

import java.util.Set;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import malilib.util.data.Constants;

public class NbtWrap
{
    public static boolean containsByte(CompoundTag tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_BYTE);
    }

    public static boolean containsShort(CompoundTag tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_SHORT);
    }

    public static boolean containsInt(CompoundTag tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_INT);
    }

    public static boolean containsLong(CompoundTag tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_LONG);
    }

    public static boolean containsFloat(CompoundTag tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_FLOAT);
    }

    public static boolean containsDouble(CompoundTag tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_DOUBLE);
    }

    public static boolean containsString(CompoundTag tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_STRING);
    }

    public static boolean containsCompound(CompoundTag tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_COMPOUND);
    }

    public static boolean containsList(CompoundTag tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_LIST);
    }

    public static boolean containsByteArray(CompoundTag tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_BYTE_ARRAY);
    }

    public static boolean containsIntArray(CompoundTag tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_INT_ARRAY);
    }

    public static boolean containsLongArray(CompoundTag tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_LONG_ARRAY);
    }

    public static boolean contains(CompoundTag tag, String name, int typeId)
    {
        return tag.contains(name, typeId);
    }

    public static boolean hasUUID(CompoundTag tag)
    {
        return hasUUID(tag, "UUIDM", "UUIDL");
    }

    public static boolean hasUUID(CompoundTag tag, String keyM, String keyL)
    {
        return containsLong(tag, keyM) && containsLong(tag, keyL);
    }

    public static boolean getBoolean(CompoundTag tag, String name)
    {
        return tag.getBoolean(name);
    }

    public static byte getByte(CompoundTag tag, String name)
    {
        return tag.getByte(name);
    }

    public static short getShort(CompoundTag tag, String name)
    {
        return tag.getShort(name);
    }

    public static int getInt(CompoundTag tag, String name)
    {
        return tag.getInt(name);
    }

    public static long getLong(CompoundTag tag, String name)
    {
        return tag.getLong(name);
    }

    public static float getFloat(CompoundTag tag, String name)
    {
        return tag.getFloat(name);
    }

    public static double getDouble(CompoundTag tag, String name)
    {
        return tag.getDouble(name);
    }

    public static String getString(CompoundTag tag, String name)
    {
        return tag.getString(name);
    }

    public static CompoundTag getCompound(CompoundTag tag, String name)
    {
        return tag.getCompound(name);
    }

    public static ListTag getList(CompoundTag tag, String name, int type)
    {
        return tag.getList(name, type);
    }

    public static byte[] getByteArray(CompoundTag tag, String name)
    {
        return tag.getByteArray(name);
    }

    public static int[] getIntArray(CompoundTag tag, String name)
    {
        return tag.getIntArray(name);
    }

    public static Tag getTag(CompoundTag tag, String name)
    {
        return tag.get(name);
    }

    public static boolean getBooleanOrDefault(CompoundTag tag, String name, boolean defaultValue)
    {
        return containsByte(tag, name) ? getByte(tag, name) != 0 : defaultValue;
    }

    public static byte getByteOrDefault(CompoundTag tag, String name, byte defaultValue)
    {
        return containsByte(tag, name) ? getByte(tag, name) : defaultValue;
    }

    public static short getShortOrDefault(CompoundTag tag, String name, short defaultValue)
    {
        return containsShort(tag, name) ? getShort(tag, name) : defaultValue;
    }

    public static int getIntOrDefault(CompoundTag tag, String name, int defaultValue)
    {
        return containsInt(tag, name) ? getInt(tag, name) : defaultValue;
    }

    public static long getLongOrDefault(CompoundTag tag, String name, long defaultValue)
    {
        return containsLong(tag, name) ? getLong(tag, name) : defaultValue;
    }

    public static float getFloatOrDefault(CompoundTag tag, String name, float defaultValue)
    {
        return containsFloat(tag, name) ? getFloat(tag, name) : defaultValue;
    }

    public static double getDoubleOrDefault(CompoundTag tag, String name, double defaultValue)
    {
        return containsDouble(tag, name) ? getDouble(tag, name) : defaultValue;
    }

    public static String getStringOrDefault(CompoundTag tag, String name, String defaultValue)
    {
        return containsString(tag, name) ? getString(tag, name) : defaultValue;
    }

    public static String getCommandFeedbackName(Tag tag)
    {
        return tag.getType().getPrettyName();
    }

    public static int getTypeId(Tag tag)
    {
        return tag.getId();
    }

    public static ByteTag asByteTag(byte value)
    {
        return ByteTag.valueOf(value);
    }

    public static ShortTag asShortTag(short value)
    {
        return ShortTag.valueOf(value);
    }

    public static IntTag asIntTag(int value)
    {
        return IntTag.valueOf(value);
    }

    public static LongTag asLongTag(long value)
    {
        return LongTag.valueOf(value);
    }

    public static FloatTag asFloatTag(float value)
    {
        return FloatTag.valueOf(value);
    }

    public static DoubleTag asDoubleTag(double value)
    {
        return DoubleTag.valueOf(value);
    }

    public static StringTag asStringTag(String value)
    {
        return StringTag.valueOf(value);
    }

    public static void putBoolean(CompoundTag tag, String name, boolean value)
    {
        tag.putBoolean(name, value);
    }

    public static void putByte(CompoundTag tag, String name, byte value)
    {
        tag.putByte(name, value);
    }

    public static void putShort(CompoundTag tag, String name, short value)
    {
        tag.putShort(name, value);
    }

    public static void putInt(CompoundTag tag, String name, int value)
    {
        tag.putInt(name, value);
    }

    public static void putLong(CompoundTag tag, String name, long value)
    {
        tag.putLong(name, value);
    }

    public static void putFloat(CompoundTag tag, String name, float value)
    {
        tag.putFloat(name, value);
    }

    public static void putDouble(CompoundTag tag, String name, double value)
    {
        tag.putDouble(name, value);
    }

    public static void putString(CompoundTag tag, String name, String value)
    {
        tag.putString(name, value);
    }

    public static void putTag(CompoundTag tag, String name, Tag value)
    {
        tag.put(name, value);
    }

    public static void putByteArray(CompoundTag tag, String name, byte[] value)
    {
        tag.putByteArray(name, value);
    }

    public static void putIntArray(CompoundTag tag, String name, int[] value)
    {
        tag.putIntArray(name, value);
    }

    public static void remove(CompoundTag tag, String name)
    {
        tag.remove(name);
    }

    public static void addTag(ListTag listTag, Tag value)
    {
        listTag.add(value);
    }

    public static Set<String> getKeys(CompoundTag tag)
    {
        return tag.getAllKeys();
    }

    public static int getListSize(ListTag list)
    {
        return list.size();
    }

    public static int getListStoredType(ListTag listTag)
    {
        return listTag.getElementType();
    }

    public static ListTag getListOfCompounds(CompoundTag tag, String name)
    {
        return getList(tag, name, Constants.NBT.TAG_COMPOUND);
    }

    public static double getDoubleAt(ListTag listTag, int index)
    {
        return listTag.getDouble(index);
    }

    public static int getIntAt(ListTag listTag, int index)
    {
        return listTag.getInt(index);
    }

    public static CompoundTag getCompoundAt(ListTag listTag, int index)
    {
        return listTag.getCompound(index);
    }
}
