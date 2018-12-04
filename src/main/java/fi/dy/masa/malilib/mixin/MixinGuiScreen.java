package fi.dy.masa.malilib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen extends Gui
{
    /*
    @Shadow
    protected Minecraft mc;

    @Inject(method = "sendChatMessage(Ljava/lang/String;Z)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;sendChatMessage(Ljava/lang/String;)V"),
            cancellable = true)
    private void onSendMessage(String msg, boolean addToChat, CallbackInfo ci)
    {
        if (ClientCommandHandler.INSTANCE.executeCommand(this.mc.player, msg) != 0)
        {
            ci.cancel();
        }
    }
    */
}
