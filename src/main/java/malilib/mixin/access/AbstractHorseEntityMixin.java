package malilib.mixin.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

@Mixin(AbstractHorse.class)
public interface AbstractHorseEntityMixin
{
    @Accessor("items")
    SimpleContainer malilib_getHorseChest();
}
