package malilib.mixin.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.nbt.NbtList;

@Mixin(NbtList.class)
public interface NbtListMixin
{
    @Accessor("type")
    byte malilib_getContainedType();
}
