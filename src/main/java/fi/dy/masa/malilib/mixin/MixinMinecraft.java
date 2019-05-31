package fi.dy.masa.malilib.mixin;

import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.event.WorldLoadHandler;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft
{
    @Shadow
    public WorldClient world;

    private WorldClient worldBefore;

    @Inject(method = "init", at = @At("RETURN"))
    private void onInitComplete(CallbackInfo ci)
    {
        // Register all mod handlers
        ((InitializationHandler) InitializationHandler.getInstance()).onGameInitDone();
    }

    @Inject(method = "shutdown()V", at = @At("RETURN"))
    private void onTick(CallbackInfo ci)
    {
        ((ConfigManager) ConfigManager.getInstance()).saveAllConfigs();
    }

    @Inject(method = "runTick", at = @At(
                value = "INVOKE", shift = At.Shift.AFTER,
                target = "Lnet/minecraft/client/Minecraft;processKeyBinds()V"))
    private void onPostKeyboardInput(CallbackInfo ci)
    {
        KeybindMulti.reCheckPressedKeys();
    }

    @Inject(method = "runTick()V", at = @At("RETURN"))
    private void onRunTickEnd(CallbackInfo ci)
    {
        TickHandler.getInstance().onClientTick((Minecraft)(Object) this);
    }

    @Inject(method = "loadWorld(" +
                            "Lnet/minecraft/client/multiplayer/WorldClient;" +
                            "Lnet/minecraft/client/gui/GuiScreen;)V",
            at = @At("HEAD"))
    private void onLoadWorldPre(@Nullable WorldClient worldClientIn, GuiScreen gui, CallbackInfo ci)
    {
        this.worldBefore = this.world;
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPre(this.world, worldClientIn, (Minecraft)(Object) this);
    }

    @Inject(method = "loadWorld(" +
                            "Lnet/minecraft/client/multiplayer/WorldClient;" +
                            "Lnet/minecraft/client/gui/GuiScreen;)V",
            at = @At("RETURN"))
    private void onLoadWorldPost(@Nullable WorldClient worldClientIn, GuiScreen gui, CallbackInfo ci)
    {
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPost(this.worldBefore, worldClientIn, (Minecraft)(Object) this);
        this.worldBefore = null;
    }
}
