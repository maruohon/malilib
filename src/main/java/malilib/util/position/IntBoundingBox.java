package malilib.util.position;

import javax.annotation.Nullable;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import malilib.MaLiLib;

public class IntBoundingBox
{
    public static final IntBoundingBox ORIGIN = new IntBoundingBox(0, 0, 0, 0, 0, 0);

    public final int minX;
    public final int minY;
    public final int minZ;
    public final int maxX;
    public final int maxY;
    public final int maxZ;

    public IntBoundingBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
    {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public int getMinX()
    {
        return this.minX;
    }

    public int getMinY()
    {
        return this.minY;
    }

    public int getMinZ()
    {
        return this.minZ;
    }

    public int getMaxX()
    {
        return this.maxX;
    }

    public int getMaxY()
    {
        return this.maxY;
    }

    public int getMaxZ()
    {
        return this.maxZ;
    }

    public boolean contains(Vec3i pos)
    {
        return pos.getX() >= this.minX &&
               pos.getX() <= this.maxX &&
               pos.getZ() >= this.minZ &&
               pos.getZ() <= this.maxZ &&
               pos.getY() >= this.minY &&
               pos.getY() <= this.maxY;
    }

    public boolean intersects(IntBoundingBox box)
    {
        return this.maxX >= box.minX &&
               this.minX <= box.maxX &&
               this.maxZ >= box.minZ &&
               this.minZ <= box.maxZ &&
               this.maxY >= box.minY &&
               this.minY <= box.maxY;
    }

    public int getMinValueForAxis(Direction.Axis axis)
    {
        switch (axis)
        {
            case X: return this.minX;
            case Y: return this.minY;
            case Z: return this.minZ;
        }

        return 0;
    }

    public int getMaxValueForAxis(Direction.Axis axis)
    {
        switch (axis)
        {
            case X: return this.maxX;
            case Y: return this.maxY;
            case Z: return this.maxZ;
        }

        return 0;
    }

    public StructureBoundingBox toVanillaBox()
    {
        return new StructureBoundingBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    public NBTTagIntArray toNbtIntArray()
    {
        return new NBTTagIntArray(new int[] { this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ });
    }

    public JsonArray toJson()
    {
        JsonArray arr = new JsonArray();

        arr.add(new JsonPrimitive(this.minX));
        arr.add(new JsonPrimitive(this.minY));
        arr.add(new JsonPrimitive(this.minZ));
        arr.add(new JsonPrimitive(this.maxX));
        arr.add(new JsonPrimitive(this.maxY));
        arr.add(new JsonPrimitive(this.maxZ));

        return arr;
    }

    @Nullable
    public IntBoundingBox createIntersectingBox(IntBoundingBox other)
    {
        if (this.intersects(other))
        {
            int minX = Math.max(this.minX, other.minX);
            int minY = Math.max(this.minY, other.minY);
            int minZ = Math.max(this.minZ, other.minZ);
            int maxX = Math.min(this.maxX, other.maxX);
            int maxY = Math.min(this.maxY, other.maxY);
            int maxZ = Math.min(this.maxZ, other.maxZ);

            return new IntBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        }

        return null;
    }

    @Nullable
    public static IntBoundingBox fromJson(JsonArray arr)
    {
        if (arr.size() == 6)
        {
            try
            {
                return new IntBoundingBox(
                        arr.get(0).getAsInt(),
                        arr.get(1).getAsInt(),
                        arr.get(2).getAsInt(),
                        arr.get(3).getAsInt(),
                        arr.get(4).getAsInt(),
                        arr.get(5).getAsInt());
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.warn("Failed to read an IntBoundingBox from JSON '" + arr + "'");
            }
        }

        return null;
    }

    public static IntBoundingBox fromVanillaBox(StructureBoundingBox box)
    {
        return createProper(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    public static IntBoundingBox createProper(Vec3i pos1, Vec3i pos2)
    {
        return createProper(pos1.getX(), pos1.getY(), pos1.getZ(),
                            pos2.getX(), pos2.getY(), pos2.getZ());
    }

    public static IntBoundingBox createProper(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        return new IntBoundingBox(
                Math.min(x1, x2),
                Math.min(y1, y2),
                Math.min(z1, z2),
                Math.max(x1, x2),
                Math.max(y1, y2),
                Math.max(z1, z2));
    }

    public static IntBoundingBox fromArray(int[] coords)
    {
        if (coords.length == 6)
        {
            return new IntBoundingBox(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
        }
        else
        {
            return IntBoundingBox.ORIGIN;
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.maxX;
        result = prime * result + this.maxY;
        result = prime * result + this.maxZ;
        result = prime * result + this.minX;
        result = prime * result + this.minY;
        result = prime * result + this.minZ;

        return result;
    }

    @Override
    public boolean equals(Object otherObj)
    {
        if (otherObj == this)
        {
            return true;
        }

        if (otherObj == null || this.getClass() != otherObj.getClass())
        {
            return false;
        }

        if (otherObj instanceof IntBoundingBox)
        {
            IntBoundingBox other = (IntBoundingBox) otherObj;

            return this.minX == other.minX && this.minY == other.minY && this.minZ == other.minZ &&
                   this.maxX == other.maxX && this.maxY == other.maxY && this.maxZ == other.maxZ;
        }

        return false;
    }

    @Override
    public String toString()
    {
        return String.format("IntBoundingBox:{minX:%d, minY:%d, minZ:%d, maxX:%d, maxY:%d, maxZ:%d}\n",
                             this.minX, this.minY, this.minZ,
                             this.maxX, this.maxY, this.maxZ);
    }
}
