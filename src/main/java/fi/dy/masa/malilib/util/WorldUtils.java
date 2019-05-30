package fi.dy.masa.malilib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.World;

public class WorldUtils
{
    public static int getDimensionId(World world)
    {
        return world.dimension.getType().getId();
    }

    /**
     * Best name. Returns the integrated server world for the current dimension
     * in single player, otherwise just the client world.
     * @param mc
     * @return
     */
    public static World getBestWorld(Minecraft mc)
    {
        if (mc.isSingleplayer() && mc.world != null)
        {
            IntegratedServer server = mc.getIntegratedServer();
            return server.getWorld(mc.world.dimension.getType());
        }
        else
        {
            return mc.world;
        }
    }
}
