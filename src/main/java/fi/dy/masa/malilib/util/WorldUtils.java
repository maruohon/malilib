package fi.dy.masa.malilib.util;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;

public class WorldUtils
{
    public static RegistryKey<World> getDimensionId(World world)
    {
        return world.getDimensionKey();
    }

    /**
     * Best name. Returns the integrated server world for the current dimension
     * in single player, otherwise just the client world.
     * @param mc
     * @return
     */
    @Nullable
    public static World getBestWorld(Minecraft mc)
    {
        IntegratedServer server = mc.getIntegratedServer();

        if (mc.world != null && server != null)
        {
            return server.getWorld(mc.world.getDimensionKey());
        }
        else
        {
            return mc.world;
        }
    }

    /**
     * Returns the requested chunk from the integrated server, if it's available.
     * Otherwise returns the client world chunk.
     * @param chunkX
     * @param chunkZ
     * @param mc
     * @return
     */
    @Nullable
    public static Chunk getBestChunk(int chunkX, int chunkZ, Minecraft mc)
    {
        IntegratedServer server = mc.getIntegratedServer();
        Chunk chunk = null;

        if (mc.world != null && server != null)
        {
            ServerWorld world = server.getWorld(mc.world.getDimensionKey());
            chunk = world.getChunk(chunkX, chunkZ);
        }

        if (chunk != null)
        {
            return chunk;
        }

        return mc.world.getChunk(chunkX, chunkZ);
    }
}
