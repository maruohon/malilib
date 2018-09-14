package fi.dy.masa.malilib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.command.ClientCommandHandler;
import fi.dy.masa.malilib.event.InputEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen extends Gui
{
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

    @Inject(method = "handleInput", cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleKeyboardInput()V"))
    private void onKeyboardInputGui(CallbackInfo ci)
    {
        if (InputEventHandler.getInstance().onKeyInput(true))
        {
            ci.cancel();
        }
    }

    @Inject(method = "handleInput", cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleMouseInput()V"))
    private void onMouseInputGui(CallbackInfo ci)
    {
        if (InputEventHandler.getInstance().onMouseInput(true))
        {
            ci.cancel();
        }
    }
}
