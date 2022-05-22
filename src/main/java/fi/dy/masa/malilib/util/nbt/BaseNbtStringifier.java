package fi.dy.masa.malilib.util.nbt;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import fi.dy.masa.malilib.mixin.IMixinNBTTagLongArray;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.Constants;

public abstract class BaseNbtStringifier
{
    protected final String baseColor;
    protected final boolean colored;
    protected final boolean useNumberSuffix;

    protected String tagNameQuote = "\"";
    protected String keyColor;
    protected String numberColor;
    protected String numberTypeColor;
    protected String stringColor;

    public BaseNbtStringifier(boolean useNumberSuffix)
    {
        this(false, useNumberSuffix, "");
    }

    public BaseNbtStringifier(boolean colored, boolean useNumberSuffix, String baseColor)
    {
        this.colored = colored;
        this.useNumberSuffix = useNumberSuffix;
        this.baseColor = baseColor;
        this.keyColor        = StringUtils.translate("malilib.label.nbt_tooltip.key_color");
        this.numberColor     = StringUtils.translate("malilib.label.nbt_tooltip.number_color");
        this.numberTypeColor = StringUtils.translate("malilib.label.nbt_tooltip.number_type_color");
        this.stringColor     = StringUtils.translate("malilib.label.nbt_tooltip.string_color");
    }

    protected String getFormattedTagName(String name)
    {
        if (name.length() == 0)
        {
            return name;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(this.tagNameQuote);

        if (this.colored)
        {
            sb.append(this.keyColor);
            sb.append(name);
            sb.append(this.baseColor);
        }
        else
        {
            sb.append(name);
        }

        sb.append(this.tagNameQuote);

        return sb.toString();
    }

    @Nullable
    protected String getPrimitiveValue(NBTBase tag)
    {
        switch (NbtUtils.getTypeId(tag))
        {
            case Constants.NBT.TAG_BYTE:    return String.valueOf(((NBTTagByte) tag).getByte());
            case Constants.NBT.TAG_SHORT:   return String.valueOf(((NBTTagShort) tag).getShort());
            case Constants.NBT.TAG_INT:     return String.valueOf(((NBTTagInt) tag).getInt());
            case Constants.NBT.TAG_LONG:    return String.valueOf(((NBTTagLong) tag).getLong());
            case Constants.NBT.TAG_FLOAT:   return String.valueOf(((NBTTagFloat) tag).getFloat());
            case Constants.NBT.TAG_DOUBLE:  return String.valueOf(((NBTTagDouble) tag).getDouble());
            case Constants.NBT.TAG_STRING:  return ((NBTTagString) tag).getString();
        }

        return null;
    }

    @Nullable
    protected String getNumberSuffix(int tagId)
    {
        switch (tagId)
        {
            case Constants.NBT.TAG_BYTE:    return "b";
            case Constants.NBT.TAG_SHORT:   return "s";
            case Constants.NBT.TAG_LONG:    return "L";
            case Constants.NBT.TAG_FLOAT:   return "f";
            case Constants.NBT.TAG_DOUBLE:  return "d";
        }

        return null;
    }

    @Nullable
    protected String getPrimitiveColorCode(int tagId)
    {
        switch (tagId)
        {
            case Constants.NBT.TAG_BYTE:
            case Constants.NBT.TAG_SHORT:
            case Constants.NBT.TAG_INT:
            case Constants.NBT.TAG_LONG:
            case Constants.NBT.TAG_FLOAT:
            case Constants.NBT.TAG_DOUBLE:
                return this.numberColor;

            case Constants.NBT.TAG_STRING:
                return this.stringColor;
        }

        return null;
    }

    protected String getFormattedPrimitiveString(NBTBase tag)
    {
        int typeId = NbtUtils.getTypeId(tag);
        String valueStr = this.getPrimitiveValue(tag);
        String valueColorStr = this.colored ? this.getPrimitiveColorCode(typeId) : null;
        String numberSuffixStr = this.useNumberSuffix ? this.getNumberSuffix(typeId) : null;
        boolean useQuotes = typeId == Constants.NBT.TAG_STRING;

        return this.getFormattedPrimitiveString(valueStr, useQuotes, valueColorStr, numberSuffixStr);
    }

    protected String getFormattedPrimitiveString(String valueStr, boolean useQuotes, @Nullable String valueColorStr, @Nullable String numberSuffixStr)
    {
        StringBuilder sb = new StringBuilder();

        if (valueStr == null)
        {
            return "";
        }

        if (useQuotes)
        {
            sb.append('"');
        }

        if (valueColorStr != null)
        {
            sb.append(valueColorStr);
        }

        sb.append(valueStr);

        if (numberSuffixStr != null)
        {
            if (this.colored)
            {
                sb.append(this.numberTypeColor);
            }

            sb.append(numberSuffixStr);
        }

        if (this.colored)
        {
            sb.append(this.baseColor);
        }

        if (useQuotes)
        {
            sb.append('"');
        }

        return sb.toString();
    }

    protected void appendTag(String tagName, NBTBase tag)
    {
        switch (NbtUtils.getTypeId(tag))
        {
            case Constants.NBT.TAG_COMPOUND:
                this.appendCompound(tagName, (NBTTagCompound) tag);
                break;

            case Constants.NBT.TAG_LIST:
                this.appendList(tagName, (NBTTagList) tag);
                break;

            case Constants.NBT.TAG_BYTE_ARRAY:
                this.appendByteArray(tagName, ((NBTTagByteArray) tag).getByteArray());
                break;

            case Constants.NBT.TAG_INT_ARRAY:
                this.appendIntArray(tagName, ((NBTTagIntArray) tag).getIntArray());
                break;

            case Constants.NBT.TAG_LONG_ARRAY:
                this.appendLongArray(tagName, ((IMixinNBTTagLongArray) tag).getArray());
                break;

            default:
                this.appendPrimitive(tagName, tag);
        }
    }

    protected abstract void appendPrimitive(String tagName, NBTBase tag);
    protected abstract void appendCompound(String tagName, NBTTagCompound tag);
    protected abstract void appendList(String tagName, NBTTagList list);
    protected abstract void appendByteArray(String tagName, byte[] arr);
    protected abstract void appendIntArray(String tagName, int[] arr);
    protected abstract void appendLongArray(String tagName, long[] arr);
}
