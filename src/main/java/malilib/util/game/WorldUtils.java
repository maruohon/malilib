package malilib.util.game;

import javax.annotation.Nullable;

import net.minecraft.world.World;

import malilib.util.game.wrap.GameUtils;

public class WorldUtils
{
    public static int getDimensionId(World world)
    {
        return world.dimension.id;
    }

    public static String getDimensionIdAsString(World world)
    {
        return String.valueOf(world.dimension.id);
    }

    /**
     * Best name. Returns the integrated server world for the current dimension
     * in single player, otherwise just the client world.
     */
    public static World getBestWorld()
    {
        return GameUtils.getClientWorld();
    }

    @Nullable
    public static World getServerWorldForClientWorld()
    {
        return GameUtils.getClientWorld();
    }

    @Nullable
    public static World getServerWorldForClientWorld(World world)
    {
        return world;
    }

    public static boolean isClientChunkLoaded(int chunkX, int chunkZ, World world)
    {
        return world.isChunkLoaded(chunkX << 4, 0, chunkZ << 4);
    }

    /*
    public static void loadClientChunk(int chunkX, int chunkZ, World world)
    {
        //world.(chunkX, chunkZ);
    }

    public static void unloadClientChunk(int chunkX, int chunkZ, World world)
    {
        //world.(chunkX, chunkZ);
    }
    */
}
