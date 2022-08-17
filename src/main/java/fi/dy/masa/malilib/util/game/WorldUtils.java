package fi.dy.masa.malilib.util.game;

import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class WorldUtils
{
    public static String getDimensionAsString(World world)
    {
        Identifier id = world.getRegistryKey().getValue();
        return id != null ? id.getNamespace() + "_" + id.getPath() : "__fallback";
    }

    /**
     * Best name. Returns the integrated server world for the current dimension
     * in single player, otherwise just the client world.
     */
    public static World getBestWorld(MinecraftClient mc)
    {
        IntegratedServer server = mc.getServer();

        if (mc.world != null && server != null)
        {
            return server.getWorld(mc.world.getRegistryKey());
        }
        else
        {
            return mc.world;
        }
    }

    @Nullable
    public static ServerWorld getServerWorldForClientWorld(MinecraftClient mc)
    {
        if (mc.isIntegratedServerRunning() && mc.world != null)
        {
            IntegratedServer server = mc.getServer();
            return server.getWorld(mc.world.getRegistryKey());
        }
        else
        {
            return null;
        }
    }
}
