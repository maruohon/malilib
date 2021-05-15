package fi.dy.masa.malilib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.inventory.ContainerHorseChest;

@Mixin(AbstractHorse.class)
public interface IMixinAbstractHorse
{
    @Accessor("horseChest")
    ContainerHorseChest malilib_getHorseChest();
}
