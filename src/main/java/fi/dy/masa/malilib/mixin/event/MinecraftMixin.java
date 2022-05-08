package fi.dy.masa.malilib.mixin.event;

import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import fi.dy.masa.malilib.event.dispatch.ClientWorldChangeEventDispatcherImpl;
import fi.dy.masa.malilib.event.dispatch.TickEventDispatcherImpl;
import fi.dy.masa.malilib.registry.Registry;

@Mixin(MinecraftClient.class)
public abstract class MinecraftMixin
{
    @Shadow public ClientWorld world;
    @Shadow public ClientPlayerEntity player;

    private ClientWorld worldBefore;

    @Inject(method = "runTick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getSystemTime()J"))
    private void onRunTickEnd(CallbackInfo ci)
    {
        if (this.world != null && this.player != null)
        {
            ((TickEventDispatcherImpl) Registry.TICK_EVENT_DISPATCHER).onClientTick();
        }
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void onLoadWorldPre(@Nullable ClientWorld worldClientIn, String loadingMessage, CallbackInfo ci)
    {
        this.worldBefore = this.world;
        ((ClientWorldChangeEventDispatcherImpl) Registry.CLIENT_WORLD_CHANGE_EVENT_DISPATCHER).onWorldLoadPre(this.world, worldClientIn);
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("RETURN"))
    private void onLoadWorldPost(@Nullable ClientWorld worldClientIn, String loadingMessage, CallbackInfo ci)
    {
        ((ClientWorldChangeEventDispatcherImpl) Registry.CLIENT_WORLD_CHANGE_EVENT_DISPATCHER).onWorldLoadPost(this.worldBefore, worldClientIn);
        this.worldBefore = null;
    }
}
