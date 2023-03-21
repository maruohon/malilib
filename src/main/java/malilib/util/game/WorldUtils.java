package malilib.util.game;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import malilib.util.game.wrap.GameUtils;

public class WorldUtils
{
    public static String getDimensionIdAsString(Level world)
    {
        ResourceLocation id = world.dimension().location();
        return id != null ? id.getNamespace() + "_" + id.getPath() : "__fallback";
    }

    /**
     * Best name. Returns the integrated server world for the current dimension
     * in single player, otherwise just the client world.
     */
    public static Level getBestWorld()
    {
        Level world = GameUtils.getClientWorld();
        MinecraftServer server = GameUtils.getIntegratedServer();

        if (server != null && world != null)
        {
            return server.getLevel(world.dimension());
        }
        else
        {
            return world;
        }
    }

    @Nullable
    public static ServerLevel getServerWorldForClientWorld()
    {
        Level world = GameUtils.getClientWorld();
        return world != null ? getServerWorldForClientWorld(world) : null;
    }

    @Nullable
    public static ServerLevel getServerWorldForClientWorld(Level world)
    {
        MinecraftServer server = GameUtils.getIntegratedServer();
        return server != null ? server.getLevel(world.dimension()) : null;
    }
}
