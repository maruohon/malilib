package fi.dy.masa.malilib.event.forge;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;

public class ForgeTickEventHandler
{
    @SubscribeEvent
    public void onClientTickEnd(TickEvent.ClientTickEvent event)
    {
        if (event.phase == Phase.END)
        {
            KeybindMulti.reCheckPressedKeys();
            TickHandler.getInstance().onClientTick(Minecraft.getMinecraft());
        }
    }
}
