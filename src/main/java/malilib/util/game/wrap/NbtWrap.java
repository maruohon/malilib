package malilib.util.game.wrap;

import java.util.Set;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

import malilib.util.data.Constants;

public class NbtWrap
{
    public static boolean containsByte(NBTTagCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_BYTE);
    }

    public static boolean containsShort(NBTTagCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_SHORT);
    }

    public static boolean containsInt(NBTTagCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_INT);
    }

    public static boolean containsLong(NBTTagCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_LONG);
    }

    public static boolean containsFloat(NBTTagCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_FLOAT);
    }

    public static boolean containsDouble(NBTTagCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_DOUBLE);
    }

    public static boolean containsString(NBTTagCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_STRING);
    }

    public static boolean containsCompound(NBTTagCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_COMPOUND);
    }

    public static boolean containsList(NBTTagCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_LIST);
    }

    public static boolean containsByteArray(NBTTagCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_BYTE_ARRAY);
    }

    public static boolean containsIntArray(NBTTagCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_INT_ARRAY);
    }

    public static boolean containsLongArray(NBTTagCompound tag, String name)
    {
        return contains(tag, name, Constants.NBT.TAG_LONG_ARRAY);
    }

    public static boolean contains(NBTTagCompound tag, String name, int typeId)
    {
        return tag.hasKey(name, typeId);
    }

    public static boolean hasUUID(NBTTagCompound tag)
    {
        return hasUUID(tag, "UUIDM", "UUIDL");
    }

    public static boolean hasUUID(NBTTagCompound tag, String keyM, String keyL)
    {
        return containsLong(tag, keyM) && containsLong(tag, keyL);
    }

    public static boolean getBoolean(NBTTagCompound tag, String name)
    {
        return tag.getBoolean(name);
    }

    public static byte getByte(NBTTagCompound tag, String name)
    {
        return tag.getByte(name);
    }

    public static short getShort(NBTTagCompound tag, String name)
    {
        return tag.getShort(name);
    }

    public static int getInt(NBTTagCompound tag, String name)
    {
        return tag.getInteger(name);
    }

    public static long getLong(NBTTagCompound tag, String name)
    {
        return tag.getLong(name);
    }

    public static float getFloat(NBTTagCompound tag, String name)
    {
        return tag.getFloat(name);
    }

    public static double getDouble(NBTTagCompound tag, String name)
    {
        return tag.getDouble(name);
    }

    public static String getString(NBTTagCompound tag, String name)
    {
        return tag.getString(name);
    }

    public static NBTTagCompound getCompound(NBTTagCompound tag, String name)
    {
        return tag.getCompoundTag(name);
    }

    public static NBTTagList getList(NBTTagCompound tag, String name, int type)
    {
        return tag.getTagList(name, type);
    }

    public static byte[] getByteArray(NBTTagCompound tag, String name)
    {
        return tag.getByteArray(name);
    }

    public static int[] getIntArray(NBTTagCompound tag, String name)
    {
        return tag.getIntArray(name);
    }

    public static NBTBase getTag(NBTTagCompound tag, String name)
    {
        return tag.getTag(name);
    }

    public static boolean getBooleanOrDefault(NBTTagCompound tag, String name, boolean defaultValue)
    {
        return containsByte(tag, name) ? getByte(tag, name) != 0 : defaultValue;
    }

    public static byte getByteOrDefault(NBTTagCompound tag, String name, byte defaultValue)
    {
        return containsByte(tag, name) ? getByte(tag, name) : defaultValue;
    }

    public static short getShortOrDefault(NBTTagCompound tag, String name, short defaultValue)
    {
        return containsShort(tag, name) ? getShort(tag, name) : defaultValue;
    }

    public static int getIntOrDefault(NBTTagCompound tag, String name, int defaultValue)
    {
        return containsInt(tag, name) ? getInt(tag, name) : defaultValue;
    }

    public static long getLongOrDefault(NBTTagCompound tag, String name, long defaultValue)
    {
        return containsLong(tag, name) ? getLong(tag, name) : defaultValue;
    }

    public static float getFloatOrDefault(NBTTagCompound tag, String name, float defaultValue)
    {
        return containsFloat(tag, name) ? getFloat(tag, name) : defaultValue;
    }

    public static double getDoubleOrDefault(NBTTagCompound tag, String name, double defaultValue)
    {
        return containsDouble(tag, name) ? getDouble(tag, name) : defaultValue;
    }

    public static String getStringOrDefault(NBTTagCompound tag, String name, String defaultValue)
    {
        return containsString(tag, name) ? getString(tag, name) : defaultValue;
    }

    public static String getCommandFeedbackName(NBTBase tag)
    {
        return NBTBase.getTypeName(getTypeId(tag));
    }

    public static int getTypeId(NBTBase tag)
    {
        return tag.getId();
    }

    public static NBTTagByte asByteTag(byte value)
    {
        return new NBTTagByte(value);
    }

    public static NBTTagShort asShortTag(short value)
    {
        return new NBTTagShort(value);
    }

    public static NBTTagInt asIntTag(int value)
    {
        return new NBTTagInt(value);
    }

    public static NBTTagLong asLongTag(long value)
    {
        return new NBTTagLong(value);
    }

    public static NBTTagFloat asFloatTag(float value)
    {
        return new NBTTagFloat(value);
    }

    public static NBTTagDouble asDoubleTag(double value)
    {
        return new NBTTagDouble(value);
    }

    public static NBTTagString asStringTag(String value)
    {
        return new NBTTagString(value);
    }

    public static void putBoolean(NBTTagCompound tag, String name, boolean value)
    {
        tag.setBoolean(name, value);
    }

    public static void putByte(NBTTagCompound tag, String name, byte value)
    {
        tag.setByte(name, value);
    }

    public static void putShort(NBTTagCompound tag, String name, short value)
    {
        tag.setShort(name, value);
    }

    public static void putInt(NBTTagCompound tag, String name, int value)
    {
        tag.setInteger(name, value);
    }

    public static void putLong(NBTTagCompound tag, String name, long value)
    {
        tag.setLong(name, value);
    }

    public static void putFloat(NBTTagCompound tag, String name, float value)
    {
        tag.setFloat(name, value);
    }

    public static void putDouble(NBTTagCompound tag, String name, double value)
    {
        tag.setDouble(name, value);
    }

    public static void putString(NBTTagCompound tag, String name, String value)
    {
        tag.setString(name, value);
    }

    public static void putTag(NBTTagCompound tag, String name, NBTBase value)
    {
        tag.setTag(name, value);
    }

    public static void putByteArray(NBTTagCompound tag, String name, byte[] value)
    {
        tag.setByteArray(name, value);
    }

    public static void putIntArray(NBTTagCompound tag, String name, int[] value)
    {
        tag.setIntArray(name, value);
    }

    public static void remove(NBTTagCompound tag, String name)
    {
        tag.removeTag(name);
    }

    public static void addTag(NBTTagList listTag, NBTBase value)
    {
        listTag.appendTag(value);
    }

    public static Set<String> getKeys(NBTTagCompound tag)
    {
        return tag.getKeySet();
    }

    public static int getListSize(NBTTagList list)
    {
        return list.tagCount();
    }

    public static int getListStoredType(NBTTagList listTag)
    {
        return listTag.getTagType();
    }

    public static NBTTagList getListOfCompounds(NBTTagCompound tag, String name)
    {
        return getList(tag, name, Constants.NBT.TAG_COMPOUND);
    }

    public static double getDoubleAt(NBTTagList listTag, int index)
    {
        return listTag.getDoubleAt(index);
    }

    public static int getIntAt(NBTTagList listTag, int index)
    {
        return listTag.getIntAt(index);
    }

    public static NBTTagCompound getCompoundAt(NBTTagList listTag, int index)
    {
        return listTag.getCompoundTagAt(index);
    }

    public static NBTTagCompound copy(NBTTagCompound tag)
    {
        return tag.m_1696745();//copy();
    }

    public static NBTTagList copy(NBTTagList tag)
    {
        return tag.m_4371252();//copy();
    }
}
