package malilib.mixin.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.World;
import net.minecraft.world.storage.WorldStorage;

@Mixin(World.class)
public interface WorldMixin
{
    @Accessor("storage")
    WorldStorage malilib_getWorldStorage();
}
