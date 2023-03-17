package malilib.util.game.wrap;

import java.nio.file.Path;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

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
    public static ServerWorld getClientPlayersServerWorld()
    {
        Entity player = getClientPlayer();
        MinecraftServer server = getIntegratedServer();
        return player != null && server != null ? server.getWorld(player.getWorld().getRegistryKey()) : null;
    }

    @Nullable
    public static ClientPlayerEntity getClientPlayer()
    {
        return getClient().player;
    }

    @Nullable
    public static PlayerInventory getPlayerInventory()
    {
        PlayerEntity player = getClient().player;
        return player != null ? player.getInventory() : null;
    }

    @Nullable
    public static ScreenHandler getPlayerInventoryContainer()
    {
        PlayerEntity player = getClient().player;
        return player != null ? player.playerScreenHandler : null;
    }

    @Nullable
    public static ScreenHandler getCurrentInventoryContainer()
    {
        PlayerEntity player = getClient().player;
        return player != null ? player.currentScreenHandler : null;
    }

    public static ClientPlayerInteractionManager getInteractionManager()
    {
        return getClient().interactionManager;
    }

    public static double getPlayerReachDistance()
    {
        return getInteractionManager().getReachDistance();
    }

    @Nullable
    public static MinecraftServer getIntegratedServer()
    {
        return getClient().getServer();
    }

    @Nullable
    public static ClientPlayNetworkHandler getNetworkConnection()
    {
        return getClient().getNetworkHandler();
    }

    public static GameOptions getOptions()
    {
        return getClient().options;
    }

    public static void sendCommand(String command)
    {
        ClientPlayerEntity player = getClientPlayer();

        if (player != null)
        {
            player.sendCommand(command);
        }
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
        return player != null ? player.getName().getString() : "?";
    }

    @Nullable
    public static HitResult getHitResult()
    {
        return getClient().crosshairTarget;
    }

    public static long getCurrentWorldTick()
    {
        World world = getClientWorld();
        return world != null ? world.getTime() : -1L;
    }

    public static boolean isCreativeMode()
    {
        ClientPlayerEntity player = getClientPlayer();
        return player != null && player.getAbilities().creativeMode;
    }

    public static int getRenderDistanceChunks()
    {
        return getOptions().getViewDistance().getValue();
    }

    public static int getVanillaOptionsScreenScale()
    {
        return GameUtils.getOptions().getGuiScale().getValue();
    }

    public static boolean isSinglePlayer()
    {
        return getClient().isInSingleplayer();
    }

    public static void scheduleToClientThread(Runnable task)
    {
        getClient().execute(task);
    }

    public static void profilerPush(String name)
    {
        getClient().getProfiler().push(name);
    }

    public static void profilerPush(Supplier<String> nameSupplier)
    {
        getClient().getProfiler().push(nameSupplier);
    }

    public static void profilerSwap(String name)
    {
        getClient().getProfiler().swap(name);
    }

    public static void profilerSwap(Supplier<String> nameSupplier)
    {
        getClient().getProfiler().swap(nameSupplier);
    }

    public static void profilerPop()
    {
        getClient().getProfiler().pop();
    }

    public static void openFile(Path file)
    {
        Util.getOperatingSystem().open(file.toFile());
    }

    public static class Options
    {
        public static boolean hideGui()
        {
            return getOptions().hudHidden;
        }
    }
}
