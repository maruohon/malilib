package malilib.mixin.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;

@Mixin(Minecraft.class)
public interface MinecraftMixin
{
    @Accessor("timer")
    Timer malilib_getTimer();
}
