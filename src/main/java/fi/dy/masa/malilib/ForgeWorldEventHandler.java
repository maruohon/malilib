package fi.dy.masa.malilib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.WorldLoadHandler;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

class ForgeWorldEventHandler
{
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if (event.getWorld().getClass() == ClientWorld.class)
        {
            Minecraft mc = Minecraft.getInstance();
            ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPre(mc.world, (ClientWorld) event.getWorld(), mc);
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event)
    {
        Minecraft mc = Minecraft.getInstance();

        if (event.getWorld() == mc.world)
        {
            ((ConfigManager) ConfigManager.getInstance()).saveAllConfigs();
            ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPre(mc.world, null, mc);
        }
    }
}
