package malilib.util.game;

import javax.annotation.Nullable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import malilib.util.game.wrap.GameUtils;

public class WorldUtils
{
    public static String getDimensionIdAsString(World world)
    {
        Identifier id = world.getRegistryKey().getValue();
        return id != null ? id.getNamespace() + "_" + id.getPath() : "__fallback";
    }

    /**
     * Best name. Returns the integrated server world for the current dimension
     * in single player, otherwise just the client world.
     */
    public static World getBestWorld()
    {
        World world = GameUtils.getClientWorld();
        MinecraftServer server = GameUtils.getIntegratedServer();

        if (server != null && world != null)
        {
            return server.getWorld(world.getRegistryKey());
        }
        else
        {
            return world;
        }
    }

    @Nullable
    public static ServerWorld getServerWorldForClientWorld()
    {
        World world = GameUtils.getClientWorld();
        return world != null ? getServerWorldForClientWorld(world) : null;
    }

    @Nullable
    public static ServerWorld getServerWorldForClientWorld(World world)
    {
        MinecraftServer server = GameUtils.getIntegratedServer();
        return server != null ? server.getWorld(world.getRegistryKey()) : null;
    }
}
