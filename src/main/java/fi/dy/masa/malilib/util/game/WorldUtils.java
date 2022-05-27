package fi.dy.masa.malilib.util.game;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class WorldUtils
{
    public static int getDimensionId(World world)
    {
        return world.provider.getDimensionType().getId();
    }

    public static String getDimensionAsString(World world)
    {
        return String.valueOf(world.provider.getDimensionType().getId());
    }

    /**
     * Best name. Returns the integrated server world for the current dimension
     * in single player, otherwise just the client world.
     */
    public static World getBestWorld(Minecraft mc)
    {
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
    public static WorldServer getServerWorldForClientWorld(Minecraft mc)
    {
        if (mc.isSingleplayer() && mc.world != null)
        {
            IntegratedServer server = mc.getIntegratedServer();
            return server.getWorld(getDimensionId(mc.world));
        }
        else
        {
            return null;
        }
    }
}
