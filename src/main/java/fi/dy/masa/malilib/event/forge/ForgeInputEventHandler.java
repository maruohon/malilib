package fi.dy.masa.malilib.event.forge;

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import fi.dy.masa.malilib.event.InputEventHandler;

public class ForgeInputEventHandler
{
    @SubscribeEvent
    public void onKeyboardInput(InputEvent.KeyInputEvent event)
    {
        // This event isn't cancellable, and is fired after vanilla key handling >_>
        // So this one is handled with a Mixin in MixinMinecraft
        //((InputEventHandler) InputEventHandler.getInputManager()).onKeyInput();
    }

    @SubscribeEvent
    public void onMouseEvent(MouseEvent event)
    {
        if (((InputEventHandler) InputEventHandler.getInputManager()).onMouseInput())
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onGuiKeyboardInputPre(GuiScreenEvent.KeyboardInputEvent.Pre event)
    {
        if (((InputEventHandler) InputEventHandler.getInputManager()).onKeyInput())
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onGuiMouseInputPre(GuiScreenEvent.MouseInputEvent.Pre event)
    {
        if (((InputEventHandler) InputEventHandler.getInputManager()).onMouseInput())
        {
            event.setCanceled(true);
        }
    }
}
