package malilib.mixin.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.inventory.ContainerHorseChest;

@Mixin(AbstractHorse.class)
public interface AbstractHorseMixin
{
    @Accessor("horseChest")
    ContainerHorseChest malilib_getHorseChest();
}
