package malilib.util.position;

public class ChunkPos
{
    public final int x;
    public final int z;

    public ChunkPos(int x, int z)
    {
        this.x = x;
        this.z = z;
    }

    public int getX()
    {
        return this.x;
    }

    public int getZ()
    {
        return this.z;
    }

    @Override
    public String toString()
    {
        return "ChunkPos{x=" + this.x + ", z=" + this.z + "}";
    }

    public static long asLong(int chunkX, int chunkZ)
    {
        return ((long) chunkZ << 32) | (long) chunkX;
    }
}
