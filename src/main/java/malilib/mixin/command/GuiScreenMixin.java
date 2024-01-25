package malilib.mixin.command;

public abstract class GuiScreenMixin {}
/*
@Mixin(GuiScreen.class)
public abstract class GuiScreenMixin extends Gui
{
    @Shadow protected Minecraft mc;

    @Inject(method = "sendChatMessage(Ljava/lang/String;Z)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;sendChatMessage(Ljava/lang/String;)V"),
            cancellable = true)
    private void onSendMessage(String msg, boolean addToChat, CallbackInfo ci)
    {
        if (Registry.CLIENT_COMMAND_HANDLER.executeCommand(this.mc.player, msg) != 0)
        {
            ci.cancel();
        }
    }
}
*/
