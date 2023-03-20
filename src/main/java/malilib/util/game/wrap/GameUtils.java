package malilib.util.game.wrap;

import java.nio.file.Path;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class GameUtils
{
    public static Minecraft getClient()
    {
        return Minecraft.getInstance();
    }

    @Nullable
    public static ClientLevel getClientWorld()
    {
        return getClient().level;
    }

    @Nullable
    public static ServerLevel getClientPlayersServerWorld()
    {
        Entity player = getClientPlayer();
        MinecraftServer server = getIntegratedServer();
        return player != null && server != null ? server.getLevel(player.getLevel().dimension()) : null;
    }

    @Nullable
    public static LocalPlayer getClientPlayer()
    {
        return getClient().player;
    }

    @Nullable
    public static Inventory getPlayerInventory()
    {
        Player player = getClient().player;
        return player != null ? player.getInventory() : null;
    }

    @Nullable
    public static AbstractContainerMenu getPlayerInventoryContainer()
    {
        Player player = getClient().player;
        return player != null ? player.inventoryMenu : null;
    }

    @Nullable
    public static AbstractContainerMenu getCurrentInventoryContainer()
    {
        Player player = getClient().player;
        return player != null ? player.containerMenu : null;
    }

    public static MultiPlayerGameMode getInteractionManager()
    {
        return getClient().gameMode;
    }

    public static double getPlayerReachDistance()
    {
        return getInteractionManager().getPickRange();
    }

    @Nullable
    public static MinecraftServer getIntegratedServer()
    {
        return getClient().getSingleplayerServer();
    }

    @Nullable
    public static ClientPacketListener getNetworkConnection()
    {
        return getClient().getConnection();
    }

    public static net.minecraft.client.Options getOptions()
    {
        return getClient().options;
    }

    public static void sendCommand(String command)
    {
        LocalPlayer player = getClientPlayer();

        if (player != null)
        {
            if (command.startsWith("/") == false)
            {
                command = "/" + command;
            }

            player.chat(command);
        }
    }

    /**
     * @return The camera entity, if it's not null, otherwise returns the client player entity.
     */
    @Nullable
    public static Entity getCameraEntity()
    {
        Minecraft mc = getClient();
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
        return getClient().hitResult;
    }

    public static long getCurrentWorldTick()
    {
        Level world = getClientWorld();
        return world != null ? world.getGameTime() : -1L;
    }

    public static boolean isCreativeMode()
    {
        LocalPlayer player = getClientPlayer();
        return player != null && player.getAbilities().instabuild;
    }

    public static int getRenderDistanceChunks()
    {
        return getOptions().getEffectiveRenderDistance();
    }

    public static int getVanillaOptionsScreenScale()
    {
        return GameUtils.getOptions().guiScale;
    }

    public static boolean isSinglePlayer()
    {
        return getClient().isLocalServer();
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
        getClient().getProfiler().popPush(name);
    }

    public static void profilerSwap(Supplier<String> nameSupplier)
    {
        getClient().getProfiler().popPush(nameSupplier);
    }

    public static void profilerPop()
    {
        getClient().getProfiler().pop();
    }

    public static void openFile(Path file)
    {
        Util.getPlatform().openFile(file.toFile());
    }

    public static class Options
    {
        public static boolean hideGui()
        {
            return getOptions().hideGui;
        }
    }
}
