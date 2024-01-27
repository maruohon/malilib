package malilib.mixin.event;

import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;

import malilib.event.dispatch.ClientWorldChangeEventDispatcherImpl;
import malilib.event.dispatch.InitializationDispatcherImpl;
import malilib.event.dispatch.TickEventDispatcherImpl;
import malilib.registry.Registry;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    @Shadow public World world;
    @Shadow public PlayerEntity player;

    private World worldBefore;

    @Inject(method = "init", at = @At("RETURN"))
    private void malilib_onInitComplete(CallbackInfo ci)
    {
        // Register all mod handlers
        ((InitializationDispatcherImpl) Registry.INITIALIZATION_DISPATCHER).onGameInitDone();
    }

    @Inject(method = "tick()V", at = @At(value = "TAIL")) // TODO b1.7.3 is this ok?
    private void malilib_onTickEnd(CallbackInfo ci)
    {
        if (this.world != null && this.player != null)
        {
            ((TickEventDispatcherImpl) Registry.TICK_EVENT_DISPATCHER).onClientTick();
        }
    }

    @Inject(method = "setWorld(Lnet/minecraft/world/World;Ljava/lang/String;Lnet/minecraft/entity/living/player/PlayerEntity;)V",
            at = @At("HEAD"))
    private void malilib_onLoadWorldPre(@Nullable World worldIn, String loadingMessage, PlayerEntity player, CallbackInfo ci)
    {
        this.worldBefore = this.world;
        ((ClientWorldChangeEventDispatcherImpl) Registry.CLIENT_WORLD_CHANGE_EVENT_DISPATCHER).onWorldLoadPre(this.world, worldIn);
    }

    @Inject(method = "setWorld(Lnet/minecraft/world/World;Ljava/lang/String;Lnet/minecraft/entity/living/player/PlayerEntity;)V",
            at = @At("RETURN"))
    private void malilib_onLoadWorldPost(@Nullable World worldIn, String loadingMessage, PlayerEntity player, CallbackInfo ci)
    {
        ((ClientWorldChangeEventDispatcherImpl) Registry.CLIENT_WORLD_CHANGE_EVENT_DISPATCHER).onWorldLoadPost(this.worldBefore, worldIn);
        this.worldBefore = null;
    }
}
