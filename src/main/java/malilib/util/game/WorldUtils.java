package malilib.util.game;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import malilib.util.game.wrap.GameUtils;

public class WorldUtils
{
    public static int getDimensionId(World world)
    {
        return world.provider.getDimensionType().getId();
    }

    public static String getDimensionIdAsString(World world)
    {
        return String.valueOf(world.provider.getDimensionType().getId());
    }

    /**
     * Best name. Returns the integrated server world for the current dimension
     * in single player, otherwise just the client world.
     */
    public static World getBestWorld()
    {
        Minecraft mc = GameUtils.getClient();

        if (mc.isSingleplayer() && mc.world != null)
        {
            IntegratedServer server = mc.getIntegratedServer();
            return server.getWorld(getDimensionId(mc.world));
        }
        else
        {
            return mc.world;
        }
    }

    @Nullable
    public static WorldServer getServerWorldForClientWorld()
    {
        World world = GameUtils.getClientWorld();
        return world != null ? getServerWorldForClientWorld(world) : null;
    }

    @Nullable
    public static WorldServer getServerWorldForClientWorld(World world)
    {
        MinecraftServer server = GameUtils.getIntegratedServer();
        return server != null ? server.getWorld(getDimensionId(world)) : null;
    }

    public static boolean isClientChunkLoaded(int chunkX, int chunkZ, WorldClient world)
    {
        return world.getChunkProvider().isChunkGeneratedAt(chunkX, chunkZ);
    }

    public static void loadClientChunk(int chunkX, int chunkZ, WorldClient world)
    {
        world.m_5061960()/*getChunkProvider()*/.loadChunk(chunkX, chunkZ);
    }

    public static void unloadClientChunk(int chunkX, int chunkZ, WorldClient world)
    {
        world.m_5061960()/*getChunkProvider()*/.unloadChunk(chunkX, chunkZ);
    }
}
