package fi.dy.masa.malilib.util;

import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.hit.HitResult;

public class GameUtils
{
    public static MinecraftClient getClient()
    {
        return MinecraftClient.getInstance();
    }

    public static String getPlayerName()
    {
        ClientPlayerEntity player = getClientPlayer();
        return player != null ? player.getName().getString() : "?";
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

    @Nullable
    public static HitResult getRayTrace()
    {
        return getClient().crosshairTarget;
    }

    public static boolean isCreativeMode()
    {
        ClientPlayerEntity player = getClientPlayer();
        return player != null && player.getAbilities().creativeMode;
    }

    public static int getRenderDistanceChunks()
    {
        return getClient().options.getViewDistance().getValue();
    }

    public static boolean isSinglePlayer()
    {
        return getClient().isIntegratedServerRunning();
    }

    public static void scheduleToClientThread(Runnable task)
    {
        MinecraftClient mc = getClient();

        if (mc.isCallingFromMinecraftThread())
        {
            task.run();
        }
        else
        {
            mc.addScheduledTask(task);
        }
    }

    public static void openFile(File file)
    {
        OpenGlHelper.openFile(file);
    }

    public static class Options
    {
        public static boolean hideGui()
        {
            return getClient().options.hideGUI;
        }
    }
}
