package fi.dy.masa.malilib.util.game.wrap;

import java.nio.file.Path;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;

public class GameUtils
{
    public static MinecraftClient getClient()
    {
        return MinecraftClient.getInstance();
    }

    @Nullable
    public static ClientWorld getClientWorld()
    {
        return getClient().world;
    }

    @Nullable
    public static ClientPlayerEntity getClientPlayer()
    {
        return getClient().player;
    }

    public static ClientPlayerInteractionManager getInteractionManager()
    {
        return getClient().interactionManager;
    }

    /**
     * @return The camera entity, if it's not null, otherwise returns the client player entity.
     */
    @Nullable
    public static Entity getCameraEntity()
    {
        MinecraftClient mc = getClient();
        Entity entity = mc.getCameraEntity();
        return entity != null ? entity : mc.player;
    }

    public static String getPlayerName()
    {
        Entity player = getClientPlayer();
        return player != null ? player.getName() : "?";
    }

    @Nullable
    public static HitResult getHitResult()
    {
        return getClient().crosshairTarget;
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
        return getClient().isIntegratedServerRunning();
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
            return getClient().options.hudHidden;
        }
    }
}
