package malilib.mixin.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.inventory.SimpleInventory;

@Mixin(AbstractHorseEntity.class)
public interface AbstractHorseEntityMixin
{
    @Accessor("items")
    SimpleInventory malilib_getHorseChest();
}
