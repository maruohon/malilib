package malilib.util.game.wrap;

import java.nio.file.Path;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.InputPlayerEntity;
import net.minecraft.client.entity.living.player.LocalPlayerEntity;
import net.minecraft.client.interaction.ClientPlayerInteractionManager;
import net.minecraft.client.network.handler.ClientNetworkHandler;
import net.minecraft.client.options.GameOptions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.world.HitResult;
import net.minecraft.world.World;

import malilib.mixin.access.MinecraftMixin;

public class GameUtils
{
    public static Minecraft getClient()
    {
        return MinecraftMixin.malilib_getMinecraft();
    }

    @Nullable
    public static World getClientWorld()
    {
        return getClient().world;
    }

    @Nullable
    public static World getClientPlayersServerWorld()
    {
        return getClientWorld();
    }

    @Nullable
    public static InputPlayerEntity getClientPlayer()
    {
        return getClient().player;
    }

    @Nullable
    public static PlayerInventory getPlayerInventory()
    {
        PlayerEntity player = getClient().player;
        return player != null ? player.inventory : null;
    }

    @Nullable
    public static InventoryMenu getPlayerInventoryContainer()
    {
        PlayerEntity player = getClient().player;
        return player != null ? player.playerMenu : null;
    }

    @Nullable
    public static InventoryMenu getCurrentInventoryContainer()
    {
        PlayerEntity player = getClient().player;
        return player != null ? player.menu : null;
    }

    public static ClientPlayerInteractionManager getInteractionManager()
    {
        return getClient().interactionManager;
    }

    public static double getPlayerReachDistance()
    {
        return getInteractionManager().getReach();
    }

    /*
    @Nullable
    public static MinecraftServer getIntegratedServer()
    {
        return getClient().getIntegratedServer();
    }
    */

    @Nullable
    public static ClientNetworkHandler getNetworkConnection()
    {
        InputPlayerEntity player = getClientPlayer();
        return player instanceof LocalPlayerEntity ? ((LocalPlayerEntity) player).networkHandler : null;
    }

    public static GameOptions getOptions()
    {
        return getClient().options;
    }

    public static void printMessageToChat(String message)
    {
        GameUtils.getClient().gui.addChatMessage(message);
    }

    public static void sendCommand(String command)
    {
        InputPlayerEntity player = getClientPlayer();

        if (player != null)
        {
            player.sendChat(command);
        }
    }

    /**
     * @return The camera entity, if it's not null, otherwise returns the client player entity.
     */
    @Nullable
    public static Entity getCameraEntity()
    {
        Minecraft mc = getClient();
        Entity entity = mc.camera;
        return entity != null ? entity : mc.player;
    }

    public static String getPlayerName()
    {
        PlayerEntity player = getClientPlayer();
        return player != null ? player.name : "?";
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
        return false;
    }

    public static int getRenderDistanceChunks()
    {
        return getOptions().viewDistance;
    }

    public static int getVanillaOptionsScreenScale()
    {
        return GameUtils.getOptions().guiScale;
    }

    public static boolean isSinglePlayer()
    {
        return getClient().isMultiplayer() == false;
    }

    public static boolean isUnicode()
    {
        return false; // TODO b1.7.3 getClient().isUnicode();
    }

    public static void scheduleToClientThread(Runnable task)
    {
        /* TODO b1.7.3
        Minecraft mc = getClient();

        if (mc.isCallingFromMinecraftThread())
        {
            task.run();
        }
        else
        {
            mc.addScheduledTask(task);
        }
        */
    }

    public static void profilerPush(String name)
    {
        //getClient().profiler.startSection(name);
    }

    public static void profilerPush(Supplier<String> nameSupplier)
    {
        //getClient().profiler.m_4994039(nameSupplier);
    }

    public static void profilerSwap(String name)
    {
        //getClient().profiler.endStartSection(name);
    }

    public static void profilerSwap(Supplier<String> nameSupplier)
    {
        //getClient().profiler.m_3681950(nameSupplier);
    }

    public static void profilerPop()
    {
        //getClient().profiler.endSection();
    }

    public static void openFile(Path file)
    {
        // TODO b1.7.3
        //OpenGlHelper.openFile(file.toFile());
    }

    @Nullable
    public static Path getCurrentSinglePlayerWorldDirectory()
    {
        /* TODO b1.7.3
        if (isSinglePlayer())
        {
            MinecraftServer server = getIntegratedServer();
            File file = server.getActiveAnvilConverter().getFile(server.getFolderName(), "icon.png");
            return file.getParentFile().toPath();
        }
        */

        return null;
    }

    public static class Options
    {
        public static boolean hideGui()
        {
            return false; // TODO b1.7.3 getOptions().hideGUI;
        }
    }
}
