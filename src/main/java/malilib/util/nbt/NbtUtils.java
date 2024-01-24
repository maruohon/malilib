package malilib.util.nbt;

import java.io.BufferedOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;

import malilib.MaLiLib;
import malilib.mixin.access.NbtElementMixin;
import malilib.util.data.Constants;
import malilib.util.game.wrap.NbtWrap;
import malilib.util.position.BlockPos;
import malilib.util.position.Vec3d;
import malilib.util.position.Vec3i;

public class NbtUtils
{
    @Nullable
    public static UUID readUUID(NbtCompound tag)
    {
        return readUUID(tag, "UUIDM", "UUIDL");
    }

    @Nullable
    public static UUID readUUID(NbtCompound tag, String keyM, String keyL)
    {
        if (NbtWrap.containsLong(tag, keyM) && NbtWrap.containsLong(tag, keyL))
        {
            return new UUID(NbtWrap.getLong(tag, keyM), NbtWrap.getLong(tag, keyL));
        }

        return null;
    }

    public static void writeUUID(NbtCompound tag, UUID uuid)
    {
        writeUUID(tag, uuid, "UUIDM", "UUIDL");
    }

    public static void writeUUID(NbtCompound tag, UUID uuid, String keyM, String keyL)
    {
        NbtWrap.putLong(tag, keyM, uuid.getMostSignificantBits());
        NbtWrap.putLong(tag, keyL, uuid.getLeastSignificantBits());
    }

    public static NbtCompound getOrCreateCompound(NbtCompound tagIn, String tagName)
    {
        NbtCompound nbt;

        if (NbtWrap.containsCompound(tagIn, tagName))
        {
            nbt = NbtWrap.getCompound(tagIn, tagName);
        }
        else
        {
            nbt = new NbtCompound();
            NbtWrap.putTag(tagIn, tagName, nbt);
        }

        return nbt;
    }

    public static <T> NbtList asListTag(Collection<T> values, Function<T, NbtElement> tagFactory)
    {
        NbtList list = new NbtList();

        for (T val : values)
        {
            NbtWrap.addTag(list, tagFactory.apply(val));
        }

        return list;
    }

    public static NbtCompound createBlockPosTag(Vec3i pos)
    {
        return putVec3i(new NbtCompound(), pos);
    }

    public static NbtCompound putVec3i(NbtCompound tag, Vec3i pos)
    {
        NbtWrap.putInt(tag, "x", pos.getX());
        NbtWrap.putInt(tag, "y", pos.getY());
        NbtWrap.putInt(tag, "z", pos.getZ());
        return tag;
    }

    @Nullable
    public static NbtCompound writeBlockPosToListTag(Vec3i pos, NbtCompound tag, String tagName)
    {
        NbtList tagList = new NbtList();

        NbtWrap.addTag(tagList, NbtWrap.asIntTag(pos.getX()));
        NbtWrap.addTag(tagList, NbtWrap.asIntTag(pos.getY()));
        NbtWrap.addTag(tagList, NbtWrap.asIntTag(pos.getZ()));
        NbtWrap.putTag(tag, tagName, tagList);

        return tag;
    }

    @Nullable
    public static NbtCompound writeBlockPosToArrayTag(Vec3i pos, NbtCompound tag, String tagName)
    {
        int[] arr = new int[] { pos.getX(), pos.getY(), pos.getZ() };

        NbtWrap.putIntArray(tag, tagName, arr);

        return tag;
    }

    @Nullable
    public static BlockPos readBlockPos(@Nullable NbtCompound tag)
    {
        if (tag != null &&
            NbtWrap.containsInt(tag, "x") &&
            NbtWrap.containsInt(tag, "y") &&
            NbtWrap.containsInt(tag, "z"))
        {
            return new BlockPos(NbtWrap.getInt(tag, "x"), NbtWrap.getInt(tag, "y"), NbtWrap.getInt(tag, "z"));
        }

        return null;
    }

    @Nullable
    public static BlockPos readBlockPosFromListTag(NbtCompound tag, String tagName)
    {
        if (NbtWrap.containsList(tag, tagName))
        {
            NbtList tagList = NbtWrap.getList(tag, tagName, Constants.NBT.TAG_INT);

            if (NbtWrap.getListSize(tagList) == 3)
            {
                return new BlockPos(NbtWrap.getIntAt(tagList, 0), NbtWrap.getIntAt(tagList, 1), NbtWrap.getIntAt(tagList, 2));
            }
        }

        return null;
    }

    @Nullable
    public static BlockPos readBlockPosFromArrayTag(NbtCompound tag, String tagName)
    {
        if (NbtWrap.containsIntArray(tag, tagName))
        {
            int[] pos = NbtWrap.getIntArray(tag, "Pos");

            if (pos.length == 3)
            {
                return new BlockPos(pos[0], pos[1], pos[2]);
            }
        }

        return null;
    }

    public static NbtCompound removeBlockPosFromTag(NbtCompound tag)
    {
        NbtWrap.remove(tag, "x");
        NbtWrap.remove(tag, "y");
        NbtWrap.remove(tag, "z");

        return tag;
    }

    public static NbtCompound writeVec3dToListTag(Vec3d pos, NbtCompound tag)
    {
        return writeVec3dToListTag(pos, tag, "Pos");
    }

    public static NbtCompound writeVec3dToListTag(Vec3d pos, NbtCompound tag, String tagName)
    {
        NbtList posList = new NbtList();

        NbtWrap.addTag(posList, NbtWrap.asDoubleTag(pos.x));
        NbtWrap.addTag(posList, NbtWrap.asDoubleTag(pos.y));
        NbtWrap.addTag(posList, NbtWrap.asDoubleTag(pos.z));
        NbtWrap.putTag(tag, tagName, posList);

        return tag;
    }

    @Nullable
    public static Vec3d readVec3d(@Nullable NbtCompound tag)
    {
        if (tag != null &&
            NbtWrap.containsDouble(tag, "dx") &&
            NbtWrap.containsDouble(tag, "dy") &&
            NbtWrap.containsDouble(tag, "dz"))
        {
            return new Vec3d(NbtWrap.getDouble(tag, "dx"), NbtWrap.getDouble(tag, "dy"), NbtWrap.getDouble(tag, "dz"));
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
        if (tag != null && NbtWrap.containsList(tag, tagName))
        {
            NbtList tagList = NbtWrap.getList(tag, tagName, Constants.NBT.TAG_DOUBLE);

            if (NbtWrap.getListStoredType(tagList) == Constants.NBT.TAG_DOUBLE && NbtWrap.getListSize(tagList) == 3)
            {
                return new Vec3d(NbtWrap.getDoubleAt(tagList, 0), NbtWrap.getDoubleAt(tagList, 1), NbtWrap.getDoubleAt(tagList, 2));
            }
        }

        return null;
    }

    @Nullable
    public static NbtCompound readNbtFromFile(Path file)
    {
        if (Files.isReadable(file) == false)
        {
            return null;
        }

        try (InputStream is = Files.newInputStream(file))
        {
            return NbtIo.readCompressed(is);
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read NBT data from file '{}'", file.toAbsolutePath());
        }

        return null;
    }

    /**
     * Write the compound tag, gzipped, to the output stream.
     */
    public static void writeCompressed(NbtCompound tag, String tagName, OutputStream outputStream) throws IOException
    {
        try (DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream))))
        {
            writeTag(tag, tagName, dataoutputstream);
        }
    }

    private static void writeTag(NbtElement tag, String tagName, DataOutput output) throws IOException
    {
        int typeId = NbtWrap.getTypeId(tag);
        output.writeByte(typeId);

        if (typeId != 0)
        {
            output.writeUTF(tagName);
            ((NbtElementMixin) tag).malilib_invokeWrite(output);
        }
    }
}
