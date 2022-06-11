package fi.dy.masa.malilib.util.game.wrap;

import java.nio.file.Path;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;

public class GameUtils
{
    public static Minecraft getClient()
    {
        return Minecraft.getMinecraft();
    }

    @Nullable
    public static WorldClient getClientWorld()
    {
        return getClient().world;
    }

    @Nullable
    public static EntityPlayerSP getClientPlayer()
    {
        return getClient().player;
    }

    public static PlayerControllerMP getInteractionManager()
    {
        return getClient().playerController;
    }

    /**
     * @return The camera entity, if it's not null, otherwise returns the client player entity.
     */
    @Nullable
    public static Entity getCameraEntity()
    {
        Minecraft mc = getClient();
        Entity entity = mc.getRenderViewEntity();
        return entity != null ? entity : mc.player;
    }

    public static String getPlayerName()
    {
        Entity player = getClientPlayer();
        return player != null ? player.getName() : "?";
    }

    @Nullable
    public static RayTraceResult getHitResult()
    {
        return getClient().objectMouseOver;
    }

    public static boolean isCreativeMode()
    {
        EntityPlayerSP player = getClientPlayer();
        return player != null && player.capabilities.isCreativeMode;
    }

    public static int getRenderDistanceChunks()
    {
        return getClient().gameSettings.renderDistanceChunks;
    }

    public static boolean isSinglePlayer()
    {
        return getClient().isSingleplayer();
    }

    public static void scheduleToClientThread(Runnable task)
    {
        Minecraft mc = getClient();

        if (mc.isCallingFromMinecraftThread())
        {
            task.run();
        }
        else
        {
            mc.addScheduledTask(task);
        }
    }

    public static void profilerPush(String name)
    {
        getClient().profiler.startSection(name);
    }

    public static void profilerPush(Supplier<String> nameSupplier)
    {
        getClient().profiler.func_194340_a(nameSupplier);
    }

    public static void profilerSwap(String name)
    {
        getClient().profiler.endStartSection(name);
    }

    public static void profilerSwap(Supplier<String> nameSupplier)
    {
        getClient().profiler.func_194339_b(nameSupplier);
    }

    public static void profilerPop()
    {
        getClient().profiler.endSection();
    }

    public static void openFile(Path file)
    {
        OpenGlHelper.openFile(file.toFile());
    }

    public static class Options
    {
        public static boolean hideGui()
        {
            return getClient().gameSettings.hideGUI;
        }
    }
}
