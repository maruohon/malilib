package fi.dy.masa.malilib;

import net.minecraft.client.Minecraft;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

class ForgeTickEventHandler
{
    @SubscribeEvent
    public void onClientTickEnd(TickEvent.ClientTickEvent event)
    {
        if (event.phase == Phase.END)
        {
            KeybindMulti.reCheckPressedKeys();
            TickHandler.getInstance().onClientTick(Minecraft.getInstance());
        }
    }
}
